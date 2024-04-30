const serverUrl =
  "ws://" + window.location.hostname + ":" + (parseInt(location.port) + 100);
console.log(serverUrl);
const webSocket = new WebSocket(serverUrl);

webSocket.onopen = function (event) {
  console.log("WebSocket connection opened.");
};

webSocket.onerror = function (event) {
  console.error("WebSocket error: ", event);
};

webSocket.onclose = function (event) {
  console.log("WebSocket connection closed.");
};

this.waitForConnection = function (callback, interval) {
  if (webSocket.readyState === 1) {
    callback();
  } else {
    let that = this;
    // optional: implement backoff for interval here
    setTimeout(function () {
      that.waitForConnection(callback, interval);
    }, interval);
  }
};

this.send = function (message, callback) {
  this.waitForConnection(function () {
    webSocket.send(message);
    if (typeof callback !== "undefined") {
      callback();
    }
  }, 1000);
};

this.clientState = {
  lobby: {
    id: "",
    name: "",
    owner: "",
    players: [],
    readyCount: 0,
    playerCount: 0,
  },

  username: "",
  uuid: "",
  inGame: false, // game has started
  inLobby: false,
};

this.widgets = {
  lobbyForm: document.getElementById("lobbyForm"),
  lobbyCreationContainer: document.getElementById("lobby-creation-container"),
  lobbyList: document.querySelector(".lobby-list"),
  playerList: document.getElementById("playerList"),
};

function sendMessage() {
  // get the message
  const messageText = document.getElementById("message").value;

  // get the timestamp
  const timestamp = new Date().toLocaleTimeString();

  // add the message
  // addMessage("You", messageText, timestamp);

  // websocket request
  /*
  send(
    JSON.stringify({
      type: "message",
      content: messageText,
      timestamp: timestamp, // add current timestamp
    })
  );
*/

  send(
    JSON.stringify([
      "data",
      {
        id: 30,
        data: {
          id: clientState.uuid,
          msg: clientState.username + ": " + messageText,
        },
      },
    ])
  );
  // clear the input field
  document.getElementById("message").value = "";

  // scroll the chatBox to the bottom
  const chatMessages = document.querySelector(".chatBox");
  chatMessages.scrollTop = chatMessages.scrollHeight;
  chatMessages.scrollIntoView({ behavior: "smooth", block: "end" });
}

this.clientState.username = localStorage.getItem("username") || "";

// JavaScript to handle message sent via enter key
document
  .getElementById("message")
  .addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
      sendMessage();
    }
  });

const buildGridBase = () => {
  const table = document.getElementById("wordGridTable");
  table.style.width = "70%";
  table.style.height = "70%";
  const tbody = table.querySelector("tbody");
  for (let i = 0; i < 20; i++) {
    let row = document.createElement("tr");
    for (let j = 0; j < 20; j++) {
      let cell = document.createElement("td");
      cell.textContent = " ";
      row.appendChild(cell);
    }
    tbody.appendChild(row);
  }
};

const sendToast = (message) => {
  Toastify({
    text: message,
    duration: 3000,
    newWindow: false,
    close: true,
    gravity: "bottom", // `top` or `bottom`
    position: "center", // `left`, `center` or `right`
    stopOnFocus: true, // Prevents dismissing of toast on hover
    style: {
      background: "linear-gradient(to right, #00b09b, #96c93d)",
    },
    onClick: function () {}, // Callback after click
  }).showToast();
};

// ask for username the second player joins and create floating form

const setUsername = () => {
  clientState.username = document.getElementById("new-username").value;
  if (clientState.username.trim() === "") {
    sendToast("Please enter a username.");
    return;
  }

  document.getElementById("new-username").value = "";

  // websocket request
  send(
    JSON.stringify([
      "join",
      {
        id: clientState.uuid,
        data: {
          username: clientState.username,
        },
      },
    ])
  );

  //sendToast("Username updated to " + username);

  //localStorage.setItem("username", username);
};

if (clientState.username != "") {
  sendToast("Welcome back, " + clientState.username + "!");
}

webSocket.onmessage = function (event) {
  /*
   * id (1) player add ["data" : {"id": 1, "data": <user information here>}] S->C
   * id (2) player remove ["data" : {"id": 2, "data": {"id" : <user id>}}]
   * id (11) server->client message ["data": {"id": 11, "data":}]
   * id (12) client->server message ["data": {"id": 12, "data":}]
   *
   * id (30) send message ["data",{"id":30,"data":"\<message content\>"}]
   * id (30) send message (server->client)
   * ["data",{"id":30,"data":{"id":0,"msg":"test2"}}] id: id of sender, msg:
   * message content
   *
   */

  if (!clientState.inLobby) {
    document.getElementById("wordGrid").style.display = "none";
    //document.getElementById("playerListHorizontal").style.display = "none";
  }

  const message = JSON.parse(event.data);

  // ["data",{"id":30,"data":"\<message content\>"}]
  // ["data",{"id":1,"data":{"id":<id>}}]

  if (message[0] == "data") {
    // message[1] is the data object
    const data = message[1];

    switch (data.id) {
      // get the type of data were parsing
      case 1:
        // add player
        clientState.uuid = data.data.id;

        if (localStorage.getItem("username") != null) {
          send(
            JSON.stringify([
              "join",
              {
                id: clientState.uuid,
                data: {
                  username: clientState.username,
                },
              },
            ])
          );
        }
        break;
      case 11:
        // ["data",{"id":11,"data": {"id":0,"data":[{"lobbyName":"sfs","lobbyStatus":"WAITING","playerCount":1,"id":"e82cfd8e-b917-4bab-a27a-260346c03339","ownerID":"247bb7d4-7262-4a42-989b-44fd6c5856d7","ownerName":"person"}]}}]
        // 0 public lobby list
        // 1 private lobby information
        if (data.data.id == 7) {
          // game started
          clientState.gameStarted = true;
          sendToast("Game started.");

          // show the word grid
          // create a word grid

          buildGridBase();
        }
        if (data.data.id == 0) {
          // update lobby list
          const lobbyList = data.data.data;
          sendToast("Lobby list updated.");

          if (clientState.inLobby) {
            widgets.lobbyCreationContainer.style.display = "none";
            // hide the lobby list
            document.querySelector(".lobby-list").style.display = "none";

            // show the player list
            document.getElementById("playerListHorizontal").style.display =
              "block";
          }

          let tbody = document.querySelector(".lobbyList tbody");
          tbody.innerHTML = "";
          lobbyList.forEach(function (lobby) {
            let row = document.createElement("tr");
            const lobbyName = document.createElement("td");
            let owner = document.createElement("td");
            let status = document.createElement("td");
            let playerCount = document.createElement("td");
            let mode = document.createElement("td");
            let joinButton = document.createElement("button");

            lobbyName.textContent = lobby.lobbyName;
            owner.textContent = lobby.ownerName;
            status.textContent = lobby.lobbyStatus;
            playerCount.textContent = lobby.playerCount;
            mode.textContent = lobby.lobbyMode;
            joinButton.textContent = "Join";
            joinButton.className = "joinButton btn btn-primary";

            const lobbyOwner = lobby.ownerID;
            const id = lobby.id;

            row.appendChild(owner);
            row.appendChild(lobbyName);
            row.appendChild(status);
            row.appendChild(playerCount);
            row.appendChild(mode);

            if (
              lobby.lobbyStatus === "WAITING" &&
              lobbyOwner !== clientState.uuid
            ) {
              row.appendChild(joinButton);
            }

            joinButton.addEventListener("click", function () {
              send(
                JSON.stringify([
                  "data",
                  {
                    id: 12, // C->S
                    data: {
                      id: 1, // join lobby
                      data: {
                        id: id,
                      },
                    },
                  },
                ])
              );

              clientState.lobby.id = id;
              clientState.lobby.name = lobby.lobbyName;
              clientState.lobby.owner = lobby.ownerID;
              clientState.inLobby = true;

              // show the player list
              document.getElementById("playerListHorizontal").style.display =
                "block";
            });

            tbody.appendChild(row);
          });
        }

        if (data.data.id == 1) {
          // update private lobby information
          const lobby = data.data.data;

          clientState.lobby.players = lobby.players;
          clientState.lobby.playerCount = lobby.playerCount;
          clientState.lobby.readyCount = lobby.readyCount;

          clientState.lobby.players = lobby.players;
          clientState.lobby.id = lobby.id;
          clientState.inGame = lobby.lobbyStatus === "IN_PROGRESS";

          // show word grid

          document.getElementById("lobbyName").textContent =
            "Lobby Name: " + lobby.lobbyName;

          document.getElementById("roomID").textContent =
            "Lobby ID: " + lobby.id;

          // update players list
          let playerTableBody = document.getElementById("playerListHorizontal"); // tbody
          playerTableBody.innerHTML = "";
          lobby.players.forEach((player) => {
            let row = document.createElement("tr");
            let cell = document.createElement("td");

            cell.textContent = player.nickname;
            row.appendChild(cell);
            playerTableBody.appendChild(row);
          });

          // check players ready == playerCount and in progress
        }

        if (data.data.id == 3) {
          // retrieve grid data
          // [ [ "a", "b", "c" ], [ "d", "e", "f" ], [ "g", "h", "i" ] ]

          const grid = data.data.data;
          console.log(grid);

          // clear the table html so that we can update it with the new data

          const tbody = document
            .getElementById("wordGridTable")
            .querySelector("tbody");

          populateGrid(grid);

          // show the word grid
          document.getElementById("wordGrid").style.display = "block";
          widgets.lobbyList.style.display = "none";
          document.getElementById("playerList").style.display = "none";
        }
        break;

      case 30:
        // add message
        addMessage(data.data.id, data.data.msg);
        break;

      default:
        break;
    }
  }

  if (message.type == "usernameQuery") {
    //const username = document.getElementById("new-username").value;
    if (message.accepted) {
      localStorage.setItem("username", clientState.username);
      sendToast("Username updated to " + clientState.username);
    } else {
      sendToast("Username already taken.");
      localStorage.setItem("username", "");
    }
  }
  /* 
  if (message.type == "lobbyUpdate") {
    const playerTable = document.getElementById("playerListHorizontal");
    const lobby = message.lobby;
    const lobbyName = lobby.name;
    console.log(lobby);
    const lobbyID = lobby.id;
    const members = lobby.players;
    clientState.lobby.players = members;

    document.getElementById("lobbyName").textContent =
      "Lobby Name: " + lobbyName;
    document.getElementById("roomID").textContent = "Lobby ID: " + lobbyID;

    // update players
    playerTable.innerHTML = "";
    members.forEach((player) => {
      let row = document.createElement("tr");
      let cell = document.createElement("td");
      cell.textContent = player.nickname;
      row.appendChild(cell);
      playerTable.appendChild(row);
    });
  }
*/
};

const addMessage = (username, message, timestamp) => {
  let chatMessages = document.querySelector(".chatMessages");
  let chatMessage = document.createElement("div");
  chatMessage.classList.add("chat-message");
  //chatMessage.textContent = username + ": " + message;
  //chatMessages.appendChild(chatMessage);

  // create the mesage text element
  let messageTextElement = document.createElement("div");
  messageTextElement.classList.add("chat-message-text");
  messageTextElement.textContent = `${message}`;

  // create the timestamp
  let timestampElement = document.createElement("div");
  timestampElement.classList.add("chat-message-timestamp");
  timestampElement.textContent = new Date().toLocaleTimeString(); // add the timestamp

  // append the message text and timestamp together
  chatMessage.appendChild(messageTextElement);
  chatMessage.appendChild(timestampElement);

  chatMessages.appendChild(chatMessage);
};

const showPlayerList = () => {
  document.querySelector("#playerList").style.display = "block";
  //document.querySelector(".lobby-list").style.display = "none";
};

const showLobbyList = () => {
  document.querySelector("#playerList").style.display = "none";
  document.querySelector(".lobby-list").style.display = "block";
  document.querySelector(".lobby-creation-container").style.display = "block";
};

const populateGrid = (chars) => {
  // chars -> "{\"claimId\":\"\",\"letter\":\"B\",\"isClaimed\":false}"
  const table = document.getElementById("wordGridTable");
  table.style.width = "70%";
  table.style.height = "70%";
  const tbody = table.querySelector("tbody");

  let isMouseDown = false;
  let startX, startY;
  let coords = new Set();

  function broadcastLastCell(highlighted) {
    const lastCell = coords[coords.length - 1]; // Get the last cell from the coords array

    const message = JSON.stringify([
      "data",
      {
        id: 12,
        data: {
          id: 6,
          data: {
            coords: [lastCell], // Broadcast only the last cell
            lobbyID: clientState.lobby.id,
            highlighted: highlighted, // Add a flag to indicate if it's highlighted or unhighlighted
            id: clientState.uuid,
          },
        },
      },
    ]);

    // Here, you need to send the message to the server
    send(message);
  }

  function startDragging(event) {
    isMouseDown = true;
    const cell = event.target;
    startX = parseInt(cell.id.split("-")[1]);
    startY = parseInt(cell.id.split("-")[2]);
    event.preventDefault();
    unhighlightAllCells();
    highlightCell(startX, startY);
    coords = [[startX, startY]]; // Reset coords array
  }

  function dragOverCell(event) {
    if (isMouseDown) {
      const cell = event.target;
      broadcastLastCell(true);
      const endX = parseInt(cell.id.split("-")[1]);
      const endY = parseInt(cell.id.split("-")[2]);
      if (
        startX === endX ||
        startY === endY ||
        Math.abs(startX - endX) === Math.abs(startY - endY)
      ) {
        unhighlightAllCells();
        highlightCellsInDirection(startX, startY, endX, endY);
        updateCoords(startX, startY, endX, endY);
      }
    }
  }

  function stopDragging() {
    isMouseDown = false;
    sendCoordsToServer(coords);
    unhighlightAllCells();

    // unhighlight all cells on server
    // for every cell in coords, unhighlight, then clear coords

    for (let i = 0; i < coords.length; i++) {
      const message = JSON.stringify([
        "data",
        {
          id: 12,
          data: {
            id: 6,
            data: {
              coords: [coords[i]], // Broadcast only the last cell
              lobbyID: clientState.lobby.id,
              highlighted: false, // Add a flag to indicate if it's highlighted or unhighlighted
              id: clientState.uuid,
            },
          },
        },
      ]);

      // Here, you need to send the message to the server
      send(message);
    }
  }

  function disableCell(cell) {
    cell.style.backgroundColor = "gray";
    cell.style.color = "white";
    cell.removeEventListener("mousedown", startDragging);
    cell.removeEventListener("mouseover", dragOverCell);
    cell.removeEventListener("mouseup", stopDragging);
    cell.style.pointerEvents = "none";
  }

  function highlightCell(x, y) {
    const cell = document.getElementById(`cell-${x}-${y}`);
    cell.style.backgroundColor = "yellow";
  }

  function unhighlightAllCells() {
    const cells = document.querySelectorAll("td");
    cells.forEach((cell) => {
      cell.style.backgroundColor = "";
    });
  }

  function highlightCellsInDirection(startX, startY, endX, endY) {
    let x = startX;
    let y = startY;
    while (x !== endX || y !== endY) {
      highlightCell(x, y);
      if (x < endX) x++;
      else if (x > endX) x--;
      if (y < endY) y++;
      else if (y > endY) y--;
    }
    highlightCell(endX, endY);
  }

  function updateCoords(startX, startY, endX, endY) {
    coords = [];
    let x = startX;
    let y = startY;
    while (x !== endX || y !== endY) {
      coords.push([x, y]);
      if (x < endX) x++;
      else if (x > endX) x--;
      if (y < endY) y++;
      else if (y > endY) y--;
    }
    coords.push([endX, endY]);
  }

  function sendCoordsToServer(coords) {
    const message = JSON.stringify([
      "data",
      {
        id: 12,
        data: {
          id: 5,
          data: {
            coords: coords,
            lobbyID: clientState.lobby.id,
          },
        },
      },
    ]);
    // Here, you need to send the message to the server
    // For demonstration purposes, let's just log the message
    console.log("Sending message to server:", message);

    send(message);
    coords = [];
  }

  // Modify the grid cells
  // table already created when game started
  const cells = tbody.querySelectorAll("td");
  let i = 0;
  for (let row = 0; row < 20; row++) {
    for (let col = 0; col < 20; col++) {
      const cell = cells[i];
      const char = chars[row][col];
      cell.textContent = char.letter;

      // make sure ours is yellow, only others will be another color
      if (char.isHighlighted && char.selectorID !== clientState.uuid) {
        cell.style.backgroundColor = "blue";
      }

      if (!char.isHighlighted) {
        cell.style.backgroundColor = "";
      }

      if (char.isClaimed) {
        disableCell(cell);
      }

      cell.id = `cell-${row}-${col}`;

      // only add event listener if cell does not have an event listenr
      if (!cell.hasEventListener) {
        cell.addEventListener("mousedown", startDragging);
        cell.addEventListener("mouseover", dragOverCell);
        cell.addEventListener("mouseup", stopDragging);
        cell.hasEventListener = true;
      }
      i++;
    }
  }
};

const debounce = (func, delay) => {
  let timer;
  return function (...args) {
    clearTimeout(timer);
    timer = setTimeout(() => {
      func.apply(this, args);
    }, delay);
  };
};

const createLobby = () => {
  if (clientState.username == "") {
    sendToast("Please enter a username.");
  } else {
    // websocket request

    if (document.getElementById("lobby-name").value.trim() === "") {
      sendToast("Please enter a lobby name.");
      return;
    }

    this.clientState.inLobby = true;

    send(
      JSON.stringify([
        "create",
        {
          id: clientState.uuid,
          data: {
            name: document.getElementById("lobby-name").value,
            owner: clientState.uuid,
            players: [],
            playerCount: document.getElementById("player-count").value,
            lobbyMode: document.getElementById("game-type").value,
            password: document.getElementById("lobby-password").value,
          },
        },
      ])
    );

    // hide the lobby list
    showPlayerList();
  }
};

// event listeners so we don't get the bullshit function not defined error
document.addEventListener("DOMContentLoaded", function () {
  // ask for username by showing alert

  if (clientState.username == "") {
    username = prompt("Please enter a username.");
    clientState.username = username;
    localStorage.setItem("username", username);
  }

  showLobbyList();

  if (!clientState.gameStarted) {
    // hide the word grid if the game has not started
    //document.getElementById("wordGrid").style.display = "some";
  }

  document
    .getElementById("updateUsernameButton")
    .addEventListener("click", function () {
      setUsername();
    });

  document.getElementById("readyButton").addEventListener("click", function () {
    document.getElementById("readyButton").textContent === "Ready"
      ? (document.getElementById("readyButton").textContent = "Unready")
      : (document.getElementById("readyButton").textContent = "Ready");
    if (document.getElementById("readyButton").textContent === "Ready") {
      send(
        JSON.stringify([
          "data",
          {
            id: 12,
            data: {
              id: 4,
              data: {
                id: clientState.lobby.id,
                status: "READY",
              },
            },
          },
        ])
      );
    } else {
      send(
        JSON.stringify([
          "data",
          {
            id: 12,
            data: {
              id: 5,
              data: {
                id: clientState.lobby.id,
                status: "UNREADY",
              },
            },
          },
        ])
      );
    }
  });

  document.getElementById("startButton").addEventListener("click", function () {
    if (clientState.lobby.readyCount < clientState.lobby.playerCount) {
      sendToast("Need all players to be ready.");
      return;
    }

    if (clientState.lobby)
      send(
        JSON.stringify([
          "data",
          {
            id: 12,
            data: {
              id: 2,
              data: {
                id: clientState.lobby.id,
              },
            },
          },
        ])
      );
  });

  document
    .getElementById("createLobbyButton")
    .addEventListener("click", function () {
      createLobby();
    });

  document
    .getElementById("sendMessageButton")
    .addEventListener("click", function () {
      sendMessage();
    });

  document
    .getElementById("new-username")
    .addEventListener("keydown", function (event) {
      if (event.key === "Enter") {
        setUsername();
      }
    });
});

document
  .getElementById("leaveLobbyButton")
  .addEventListener("click", function () {
    send(
      JSON.stringify([
        "leave",
        {
          id: clientState.uuid,
          data: {
            username: clientState.username,
          },
        },
      ])
    );

    clientState.inLobby = false;
    clientState.inGame = false;
    showLobbyList();
  });

// JavaScript to handle form submission
/*
        document.getElementById("lobbyForm").addEventListener("submit", function (event) {
            event.preventDefault(); // Prevent the default form submission
            let username = document.getElementById("username").value;
            if (username.trim() !== "") {
                // Redirect to the game page or perform any other action
                alert("Welcome, " + username + "! Starting the game...");
                // Replace 'game.html' with the actual URL of your game page
                window.location.href = "game.html";
            } else {
                alert("Please enter a username.");
            }
        });
*/

// Javascript to handle password being required
function togglePassword() {
  let passwordInput = document.getElementById("lobby-password");
  let requirePassword = document.getElementById("require-password");
  if (requirePassword.checked) {
    passwordInput.required = true;
    passwordInput.pattern = "[0-9]{4}";
  } else {
    passwordInput.required = false;
    passwordInput.pattern = "";
  }
}

function togglePasswordField() {
  let passwordInput = document.getElementById("lobby-password");
  let requirePassword = document.getElementById("require-password");

  if (requirePassword.checked) {
    passwordInput.disabled = false;
    passwordInput.type = "text";
    passwordInput.pattern = "[0-9]{4}";
  } else {
    passwordInput.disabled = true;
    passwordInput.type = "text";
    passwordInput.pattern = "";
    passwordInput.value = ""; //clear the input if there is any
  }
}

function toggleSettings() {
  const settingsContainer = document.querySelector(".settings-container");
  const overlay = document.querySelector(".overlay");

  settingsContainer.style.display =
    settingsContainer.style.display === "none" ? "block" : "none";
  overlay.style.display = overlay.style.display === "none" ? "block" : "none";
}

function closeSettings() {
  // Hide the settings menu and overlay
  const settingsContainer = document.querySelector(".settings-container");
  const overlay = document.querySelector(".overlay");
  settingsContainer.style.display = "none";
  overlay.style.display = "none";
}

function toggleStats() {
  const settingsContainer = document.querySelector(".stats-container");
  const overlay = document.querySelector(".overlay");

  settingsContainer.style.display =
    settingsContainer.style.display === "none" ? "block" : "none";
  overlay.style.display = overlay.style.display === "none" ? "block" : "none";
}

function closeStats() {
  // Hide the settings menu and overlay
  const settingsContainer = document.querySelector(".stats-container");
  const overlay = document.querySelector(".overlay");
  settingsContainer.style.display = "none";
  overlay.style.display = "none";
}

const searchInput = document.getElementById("searchInput");
const lobbyTableBody = document.getElementById("lobbyTableBody");
let selectedLobbyId = null;

// Add event listener to the search input
searchInput.addEventListener("input", function () {
  filterLobbyList(message.lobbyList, this.value.toLowerCase());
});

// Add event listener to the join button
document.getElementById("joinButton").addEventListener("click", function () {
  if (selectedLobbyId) {
    send(
      JSON.stringify({
        type: "joinLobby",
        lobbyId: selectedLobbyId,
      })
    );

    // Update client state and UI
    clientState.lobby.id = selectedLobbyId;
    clientState.inLobby = true;

    // Hide the lobby list and show the player list
    //showPlayerList();
    document.getElementById("playerList").style.display = "block";
    document.querySelector("#playerListHorizontal").style.display = "block";

    // if not lobby owner, don't show the start button
    if (clientState.lobby.owner !== clientState.uuid) {
      document.getElementById("startButton").style.display = "none";
    }

    // ...
  } else {
    sendToast("Please select a lobby to join.");
  }
});

function highlightSelectedRow(row) {
  const rows = Array.from(lobbyTableBody.getElementsByTagName("tr"));
  rows.forEach((r) => r.classList.remove("table-primary"));
  row.classList.add("table-primary");
}

function filterLobbyList(lobbies, searchTerm) {
  const tableRows = Array.from(lobbyTableBody.getElementsByTagName("tr"));

  /* 
  tableRows.forEach((row) => {
    const lobbyName = row
      .getElementsByTagName("td")[1]
      .textContent.toLowerCase();
    const ownerName = row
      .getElementsByTagName("td")[0]
      .textContent.toLowerCase();

    const matchingLobby = lobbies.find(
      (lobby) =>
        lobby.lobbyName.toLowerCase().includes(searchTerm) ||
        lobby.ownerName.toLowerCase().includes(searchTerm)
    );

    if (matchingLobby) {
      row.style.display = "";
    } else {
      row.style.display = "none";
    }
  });
  */

  // function cellClickHandler(row, col) {
  //   const cellId = `cell-${row}-${col}`;
  //   const cell = document.getElementById(cellId);
  //   if (!startPoint) {
  //     // get the starting point
  //     startPoint = { row, col };
  //     cell.style.backgroundColor = "blue";
  //   } else {
  //     highlightSelection;
  //   }
  // }
}
