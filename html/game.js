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
};

function sendMessage() {
  // get the message
  const messageText = document.getElementById("message").value;

  // get the timestamp
  const timestamp = new Date().toLocaleTimeString();

  // add the message
  // addMessage("You", messageText, timestamp);

  // websocket request
  send(
    JSON.stringify({
      type: "message",
      content: messageText,
      timestamp: timestamp, // add current timestamp
    })
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

if (localStorage.getItem("username") != null) {
  send(
    JSON.stringify({
      type: "setUsername",
      username: localStorage.getItem("username"),
    })
  );
}

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
    JSON.stringify({
      type: "setUsername",
      username: clientState.username,
    })
  );

  //sendToast("Username updated to " + username);

  //localStorage.setItem("username", username);
};

if (clientState.username != "") {
  sendToast("Welcome back, " + clientState.username + "!");
}

webSocket.onmessage = function (event) {
  if (!clientState.inLobby) {
    document.getElementById("wordGrid").style.display = "none";
    document.getElementById("playerListHorizontal").style.display = "none";
  }

  const message = JSON.parse(event.data);

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
    const members = lobby.players;
    clientState.lobby.players = members;

    // update players
    const playerList = document.getElementById("playerListHorizontal");
    playerList.innerHTML = "";
    members.forEach(function (member) {
      const player = document.createElement("ul");
      player.textContent = member.nickname;
      playerList.appendChild(player);
    });
  }

  if ("messageBoard" in message) {
    addMessage(
      message.messageBoard[message.messageBoard.length - 1].username,
      message.messageBoard[message.messageBoard.length - 1].message
    );
  }

  if (message.type == "selfID") {
    clientState.uuid = message.id;
  } 
    if (message.type === "playerReadiedUp") {
    const playerId = message.playerId;
    // Update the UI to reflect player readiness
    handlePlayerReadiedUp(playerId);
}


function handlePlayerReadiedUp(playerId) {
// Find and update the UI to reflect player readiness
const playerElement = document.getElementById(playerId);
if (playerElement) {
    playerElement.classList.add('ready'); // 
}
}

  if ("lobbyList" in message) {
    if (clientState.inLobby) {
      widgets.lobbyCreationContainer.style.display = "none";
      // hide the lobby list
      document.querySelector(".lobby-list").style.display = "none";

      // show the player list
      document.getElementById("playerListHorizontal").style.display = "block";
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
  messageTextElement.textContent = `${username}: ${message}`;

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
      JSON.stringify({
        type: "createLobby",
        lobbyName: document.getElementById("lobby-name").value,
        playerCount: document.getElementById("player-count").value,
        lobbyMode: document.getElementById("game-type").value,
        password: document.getElementById("lobby-password").value,
      })
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
