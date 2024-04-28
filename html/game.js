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
  playerList: document.getElementById("playerListHorizontal"),
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
    document.getElementById("playerListHorizontal").style.display = "none";
  }

  const message = JSON.parse(event.data);

  // ["data",{"id":30,"data":"\<message content\>"}]
  // ["data",{"id":1,"data":{"id":<id>}}]

  if (message[0] == "data") {
    // message[1] is the data object
    const data = message[1];
    console.log(data.id);
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
                JSON.stringify({
                  type: "joinLobby",
                  lobbyId: id,
                })
              );

              clientState.lobby.id = id;
              clientState.lobby.name = lobby.lobbyName;
              clientState.lobby.owner = lobby.ownerID;
              clientState.inLobby = true;

              // hide the lobby list
              document.querySelector(
                ".lobby-creation-container"
              ).style.display = "none";
              document.querySelector(".lobby-list").style.display = "none";

              // show the player list
              document.getElementById("playerListHorizontal").style.display =
                "block";
            });

            tbody.appendChild(row);
          });
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

  if (message.type == "lobbyUpdate") {
    const lobby = message.lobby;
    const lobbyName = lobby.name;
    const lobbyID = lobby.id;
    const members = lobby.players;
    clientState.lobby.players = members;

    

    document.getElementById("lobbyName").textContent = "Lobby Name: "+ lobbyName;
    document.getElementById("roomID").textContent = "Lobby ID: "+ lobbyID;

    // update players
    const playerList = this.widgets.playerList;
    playerList.innerHTML = "";
    members.forEach(function (member) {
      const player = document.createElement("ul");
      player.textContent = member.nickname;
      playerList.appendChild(player);
    });
  }


  if ("lobbyList" in message) {
    if (clientState.inLobby) {
      widgets.lobbyCreationContainer.style.display = "none";
      // hide the lobby list
      document.querySelector(".lobby-list").style.display = "none";

      // show the player list
      document.getElementById("playerListHorizontal").style.display = "block";

      // show the lobby settings container
      document.getElementById('lobby-settings-container').style.display = "block";

      // fill out the lobby settings form
      document.getElementById('lobby-name-display').value = clientState.lobby.name;
      document.getElementById('lobby-id-display').value = clientState.lobby.id;
      document.getElementById('player-count-input').value = message.lobby.playerCount;
      document.getElementById('game-type-input').value = message.lobby.lobbyMode;

      // Populate the kick player select
      const kickPlayerSelect = document.getElementById('kick-player-select');
      kickPlayerSelect.innerHTML = '';
      message.lobby.players.forEach(player => {
        const option = documnet.createElement('option');
        option.value = player.id;
        option.textContent = player.nickname;
        kickPlayerSelect.appendChild(option);
      });
    }
    /**
     * {"lobbyList" : [
     * {"lobbyName": "Lobby 1", "status": "In Progress", "playerCount": "2", "lobbyId": ""},]}
     */
    let tbody = document.querySelector(".lobbyList tbody");
    tbody.innerHTML = "";
    message.lobbyList.forEach(function (lobby) {
      let row = document.createElement("tr");
      const lobbyName = document.createElement("td");
      let owner = document.createElement("td");
      let status = document.createElement("td");
      let playerCount = document.createElement("td");
      let joinButton = document.createElement("button");

      lobbyName.textContent = lobby.lobbyName;
      owner.textContent = lobby.ownerName;
      status.textContent = lobby.lobbyStatus;
      playerCount.textContent = lobby.playerCount;
      joinButton.textContent = "Join";
      joinButton.className = "joinButton btn btn-primary";

      const lobbyOwner = lobby.ownerID;
      const id = lobby.id;

      row.appendChild(owner);
      row.appendChild(lobbyName);
      row.appendChild(status);
      row.appendChild(playerCount);

      if (lobby.lobbyStatus === "WAITING" && lobbyOwner !== clientState.uuid) {
        row.appendChild(joinButton);
      }

      joinButton.addEventListener("click", function () {
        send(
          JSON.stringify({
            type: "joinLobby",
            lobbyId: id,
          })
        );

        clientState.lobby.id = id;
        clientState.lobby.name = lobby.lobbyName;
        clientState.lobby.owner = lobby.ownerID;
        clientState.inLobby = true;

        // hide the lobby list
        document.querySelector(".lobbyList").style.display = "none";
        document.querySelector(".lobby").style.display = "block";

        // show the player list
        document.getElementById("playerListHorizontal").style.display = "block";
      });

      tbody.appendChild(row);
    });
  }
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
  }
};

// event listeners so we don't get the bullshit function not defined error
document.addEventListener("DOMContentLoaded", function () {
  if (!clientState.gameStarted) {
    // hide the word grid if the game has not started
    //document.getElementById("wordGrid").style.display = "some";
  }

  // set wordGrid to 50x50 grid

  let table = document.createElement("table");
  for (let i = 0; i < 50; i++) {
    let row = document.createElement("tr");
    for (let j = 0; j < 50; j++) {
      let cell = document.createElement("td");
      cell.textContent = " ";
      row.appendChild(cell);
    }
    table.appendChild(row);
  }

  document.getElementById("wordGrid").appendChild(table);

  document
    .getElementById("updateUsernameButton")
    .addEventListener("click", function () {
      setUsername();
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

const searchInput = document.getElementById('searchInput');
const lobbyTableBody = document.getElementById('lobbyTableBody');
let selectedLobbyId = null;

// Add event listener to the search input
searchInput.addEventListener('input', function() {
    filterLobbyList(message.lobbyList, this.value.toLowerCase());
});

// Add event listener to the lobby table rows
lobbyTableBody.addEventListener('click', function(event) {
    if (event.target.classList.contains('join-button')) {
        const row = event.target.closest('tr');
        const lobbyId = row.dataset.lobbyId;
        selectedLobbyId = lobbyId;
        highlightSelectedRow(row);
    }
});

// Add event listener to the join button
document.getElementById('joinButton').addEventListener('click', function() {
    if (selectedLobbyId) {
        send(
            JSON.stringify({
                type: "joinLobby",
                lobbyId: selectedLobbyId,
            })
        );

        // Update client state and UI
        clientState.lobby.id = selectedLobbyId;
        // ...
    } else {
        sendToast("Please select a lobby to join.");
    }
});

function highlightSelectedRow(row) {
    const rows = Array.from(lobbyTableBody.getElementsByTagName('tr'));
    rows.forEach(r => r.classList.remove('table-primary'));
    row.classList.add('table-primary');
}

function filterLobbyList(lobbies, searchTerm) {
    const tableRows = Array.from(lobbyTableBody.getElementsByTagName('tr'));

    tableRows.forEach(row => {
        const lobbyName = row.getElementsByTagName('td')[1].textContent.toLowerCase();
        const ownerName = row.getElementsByTagName('td')[0].textContent.toLowerCase();

        const matchingLobby = lobbies.find(lobby =>
            lobby.lobbyName.toLowerCase().includes(searchTerm) ||
            lobby.ownerName.toLowerCase().includes(searchTerm)
        );

        if (matchingLobby) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

const playerListHorizontal = document.getElementById('playerListHorizontal');

// Assuming you have an array of player objects
const players = [
  { id: 1, name: 'John Doe', color: 'Blue', wins: 5, lastStanding: '2nd' },
  { id: 2, name: 'Jane Smith', color: 'Red', wins: 3, lastStanding: '1st' },
  { id: 3, name: 'Bob Johnson', color: 'Green', wins: 2, lastStanding: '3rd' },
  { id: 4, name: 'Alice Williams', color: 'Yellow', wins: 1, lastStanding: '4th' },
  { id: 5, name: 'Tom Davis', color: 'Purple', wins: 0, lastStanding: '5th' },
];

players.forEach(player => {
  const row = document.createElement('tr');

  const nameCell = document.createElement('td');
  nameCell.textContent = player.name;
  row.appendChild(nameCell);

  const colorCell = document.createElement('td');
  colorCell.innerHTML = `
    <div class="color-square-container">
      <span>${player.color} :</span>
      <div class="color-squares-container">
        ${getColorSquares(player.color)}
      </div>
    </div>
  `;
  row.appendChild(colorCell);

  const winsCell = document.createElement('td');
  winsCell.textContent = player.wins;
  row.appendChild(winsCell);

  const lastStandingCell = document.createElement('td');
  lastStandingCell.textContent = player.lastStanding;
  row.appendChild(lastStandingCell);

  playerListHorizontal.appendChild(row);
});

function getColorSquares(color) {
  let colorSquares = '';
  switch (color) {
    case 'Blue':
      colorSquares = `
        <div class="color-square" style="background-color: #7ba8ff;"></div>
        <div class="color-square" style="background-color: #b5cefe;"></div>
      `;
      break;
    case 'Red':
      colorSquares = `
        <div class="color-square" style="background-color: #f96f6f;"></div>
        <div class="color-square" style="background-color: #ffbebe;"></div>
      `;
      break;
    case 'Green':
      colorSquares = `
        <div class="color-square" style="background-color: #46ff43;"></div>
        <div class="color-square" style="background-color: #a8ffa6;"></div>
      `;
      break;
    case 'Yellow':
      colorSquares = `
        <div class="color-square" style="background-color: #ffe143;"></div>
        <div class="color-square" style="background-color: #fff1a6;"></div>
      `;
      break;
    case 'Purple':
      colorSquares = `
        <div class="color-square" style="background-color: #b366ff;"></div>
        <div class="color-square" style="background-color: #e6bcff;"></div>
      `;
      break;
    case 'Cyan':
      colorSquares = `
        <div class="color-square" style="background-color: #4bf4ff;"></div>
        <div class="color-square" style="background-color: #b9fbff;"></div>
      `;
      break;
    default:
      colorSquares = '';
  }
  return colorSquares;
}
