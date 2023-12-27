const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/gs-guide-websocket'
});

console.log("Lobby ID: " + lobbyId);

stompClient.onConnect = (frame) => {
  // setConnected(true);
  console.log('Connected: ' + frame);

  stompClient.subscribe('/topic/' + lobbyId, (json) => {
    showMessage(JSON.parse(json.body).content);
  });
  stompClient.subscribe('/topic/host/' + lobbyId, (json) => {
    switch (JSON.parse(json.body).cmd) {
      case "time":
        showTimer(JSON.parse(json.body).content);
        break;
      // case "questionTime":
      //   questionTimer(JSON.parse(json.body).content);
      //   break;
      // case "start":
      //   $("#game").show();
      //   break;
      // case "question":
      //   showQuestion(JSON.parse(json.body));
      //   break;
      case "gameEnd":
        if (JSON.parse(json.body).content == 1) {
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

// function setConnected(connected) {
//   if (connected) {
//     $("#game").hide();
//   }
// }

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
  if (message == 1) {
    $("#init").hide();
  }
  $("#initTimer").html(message);
}

function questionTimer(message) {
  $("#questionTimer").html(message);
}

function showQuestion(question) {
  $("#gameQuestion").html(question.question);
  if (question.type == "multiple") {
    var questions = question.answers;
    // questions.push(question.correct_answer);
    // shuffleQuestions(questions);

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

  function shuffleQuestions(array) {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [array[i], array[j]] = [array[j], array[i]];
    }
  }
}


connect();