// const stompClient = new StompJs.Client({
//   brokerURL: 'ws://localhost:8080/mini-project-websocket'
// });
const stompClient = new StompJs.Client({
  brokerURL: 'wss://zesty-cars-production.up.railway.app/mini-project-websocket'
});

console.log("Lobby ID: " + lobbyId);

stompClient.onConnect = (frame) => {
  console.log('Connected: ' + frame);

  stompClient.subscribe('/topic/' + lobbyId, (json) => {
    showMessage(JSON.parse(json.body).content);
  });
  stompClient.subscribe('/topic/host/' + lobbyId, (json) => {
    switch (JSON.parse(json.body).cmd) {
      case "time":
        showTimer(JSON.parse(json.body).content);
        break;
      case "gameEnd":
        if (JSON.parse(json.body).content == 1) {
          stompClient.publish({
            destination: "/topic/game/" + lobbyId,
            body: JSON.stringify({ 'cmd': "gameEnd" })
          });
          console.log("Game ended!")
        } else {
          console.log("something went wrong.");
        }
        break;
      default:
        console.log("error occured");
        break;
    }
  });
  stompClient.subscribe('/topic/game/' + lobbyId, (json) => {
    switch (JSON.parse(json.body).cmd) {
      case "questionTime":
        questionTimer(JSON.parse(json.body).content);
        break;
      case "leaderboard":
        var players = JSON.parse(json.body).content;
        leaderboard(players);
        break;
      case "leaderboardTime":
        leaderboardTimer(JSON.parse(json.body).content);
        break;
      case "start":
        $("#game").show();
        break;
      case "question":
        showQuestion(JSON.parse(json.body));
        break;
      default:
        console.log("error occured");
        break;
    }
  });
  stompClient.publish({
    destination: "/app/host/" + lobbyId,
    body: JSON.stringify({ 'lobbyId': lobbyId })
  });
};

stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};


function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

function sendName() {
  stompClient.publish({
    destination: "/app/hello/" + lobbyId,
    body: JSON.stringify({ 'name': playerName, 'lobbyId': lobbyId })
  });
}

function showMessage(message) {
  $("#players").append("<tr><td>" + message + "</td></tr>");
}

function showTimer(message) {
  $("#initTimer").html(message);
  if (message == 0) {
    $("#init").hide();
  }
}

function questionTimer(message) {
  $("#questionTimer").html("Points: " + message);
}

function leaderboard(message) {
  $("#gameAnswer").show();
  $("#leaderboard").show();
  var playersListElement = document.getElementById('players-list');

  message.forEach(function (player) {
    var listItem = document.createElement('li');
    listItem.className = 'list-group-item';
    listItem.textContent = player.name + ': ' + player.score;
    playersListElement.appendChild(listItem);
  })
}

function leaderboardTimer(message) {
  $("#questionTimer").html(message);
  $("#gameQuestion").html("Leaderboard");
  $("#questionsDiv").hide();
}

function showQuestion(question) {
  $("#gameAnswer").hide();
  $("#players-list").empty();
  $("#leaderboard").hide();
  $("#questionsDiv").show();
  $("#gameQuestion").html(question.question);
  $("#gameAnswer h1").html("Correct Answer: " + question.correct_answer);

  if (question.type == "multiple") {
    var questions = question.answers;

    for (let i = 0; i < questions.length; i++) {
      $("#gameQuestion" + (i + 1)).html(questions[i]);
    }

    $("#gameQuestion3").show();
    $("#gameQuestion4").show();
  } else if (question.type == "boolean") {
    $("#gameQuestion1").html("True");
    $("#gameQuestion2").html("False");
    $("#gameQuestion3").hide();
    $("#gameQuestion4").hide();
  }
}
connect();
