const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/gs-guide-websocket'
});

console.log("Lobby ID: " + lobbyId);
console.log("Name: " + playerName);
let questionTime = 0;

stompClient.onConnect = (frame) => {
  setConnected(true);
  console.log('Connected: ' + frame);
  stompClient.subscribe('/topic/' + lobbyId, (json) => {
    showGreeting(JSON.parse(json.body).content);
  });
  stompClient.subscribe('/topic/game/' + lobbyId, (json) => {
    switch (JSON.parse(json.body).cmd) {
      case "questionTime":
        questionTime = JSON.parse(json.body).content;
        questionTimer(questionTime);
        break;
      case "start":
        $("#wait").hide();
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
  sendName();
};

stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  if (connected) {
    $("#conversation").show();
  }
  else {
    $("#conversation").hide();
  }
  $("#greetings").html("");
}

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

function showGreeting(message) {
  $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showTimer(message) {
  if (message == 1) {
    $("#timerHeader").hide();
    $("#greeting").hide();
  }
  $("#timer").html(message);
}

function questionTimer(message) {
  $("#questionTimer").html("Points: " + message);
  if (message == 0) {

    $("#result").show();
    disableAnswers(true);
  }
}

function showQuestion(question) {
  $("#result").hide();
  disableAnswers(false);
  $('#result').removeClass('alert-success').removeClass('alert-danger').addClass('alert-warning');
  $("#result").html("No answer");
  $("#gameQuestion").html(question.question);
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

function shuffleQuestions(array) {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
}

function handleAnswer(message) {
  disableAnswers(true);
  var answer = $('#gameQuestion' + message).text();
  var question = $('#gameQuestion').text();
  var jsonRequest = JSON.stringify({ 'playerName': playerName, 'question': question, 'answer': answer, 'points': questionTime });
  sendDataToSpring(jsonRequest);
}

function disableAnswers(disabled) {
  for (let i = 1; i <= 5; i++) {
    $('#gameQuestion' + i).prop('disabled', disabled);
  }
}

function sendDataToSpring(jsonRequest) {
  $.ajax({
    type: "POST",
    url: "/api/check/" + lobbyId,
    contentType: "application/json",
    data: jsonRequest,
    success: function (response) {
      console.log("Data sent successfully!");
      // console.log(response);
      changeResult(response.message);
    },
    error: function (error) {
      console.error("Error sending data:", error);
    }
  });
}

function changeResult(message) {
  if (message == "wrong") {
    $('#result').removeClass('alert-success').removeClass('alert-warning').addClass('alert-danger');
    $('#result').html("Wrong!");
  } else if (message == "correct") {
    $('#result').removeClass('alert-danger').removeClass('alert-warning').addClass('alert-success');
    $('#result').html("Correct!");
  }
}

// $(function () {
//     $("form").on('submit', (e) => e.preventDefault());
//     $("#connect").click(() => connect());
//     $("#disconnect").click(() => disconnect());
//     $("#send").click(() => sendName());
// });

connect();