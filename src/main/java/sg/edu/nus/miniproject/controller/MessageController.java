package sg.edu.nus.miniproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import sg.edu.nus.miniproject.model.Message;
import sg.edu.nus.miniproject.model.Player;
import sg.edu.nus.miniproject.model.Question;
import sg.edu.nus.miniproject.repo.LobbyRepo;
import sg.edu.nus.miniproject.service.MessageService;

@Controller
public class MessageController {

  @Autowired
  private MessageService messageService;

  @Autowired
  private LobbyRepo lr;

  ObjectMapper objectMapper = new ObjectMapper();

  @MessageMapping("/hello/{lobbyId}")
  @SendTo("/topic/{lobbyId}")
  public Message greeting(Player message) throws Exception {
    return new Message(
      "greeting",
      "Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"
    );
  }

  @MessageMapping("/host/{lobbyId}")
  @SendTo("/topic/host/{lobbyId}")
  public Message initGame(@DestinationVariable String lobbyId, String json)
    throws InterruptedException, IOException {
    for (int i = 30; i >= 0; i--) {
      broadcastInitTime(i, lobbyId);
      Thread.sleep(1000);
    }

    List<Question> questions = lr.getQuestions(lobbyId);

    messageService.sendMessageToClient(
      "/topic/game/" + lobbyId,
      "{\"cmd\":\"start\",\"content\":\"1\"}"
    );

    for (Question question : questions) {
      question.setCmd("question");
      question.combineAndShuffleAnswers();
      String questionString = objectMapper.writeValueAsString(question);
      broadcastQuestion(questionString, lobbyId);
      for (int i = 100; i >= 0; i--) {
        broadcastQuestionTime(i, lobbyId);
        Thread.sleep(100);
      }

      List<Player> players = lr.getPlayers(lobbyId);
      Collections.sort(
        players,
        Comparator.comparing(Player::getScore).reversed()
      );

      String jsonPlayers = objectMapper.writeValueAsString(players);
      broadcastLeaderboard(lobbyId, jsonPlayers);

      for (int i = 5; i > 0; i--) {
        broadcastLeaderboardTime(i, lobbyId);
        Thread.sleep(1000);
      }
    }

    return new Message("gameEnd", "1");
  }

  private void broadcastInitTime(int i, String lobbyId) {
    messageService.sendMessageToClient(
      "/topic/host/" + lobbyId,
      String.format("{\"cmd\":\"time\",\"content\":\"%d\"}", i)
    );
  }

  private void broadcastLeaderboard(String lobbyId, String jsonPlayers) {
    messageService.sendMessageToClient(
      "/topic/game/" + lobbyId,
      String.format("{\"cmd\":\"leaderboard\",\"content\":%s}", jsonPlayers)
    );
  }

  private void broadcastQuestionTime(int i, String lobbyId) {
    messageService.sendMessageToClient(
      "/topic/game/" + lobbyId,
      String.format("{\"cmd\":\"questionTime\",\"content\":\"%d\"}", i)
    );
  }

  private void broadcastLeaderboardTime(int i, String lobbyId) {
    messageService.sendMessageToClient(
      "/topic/game/" + lobbyId,
      String.format("{\"cmd\":\"leaderboardTime\",\"content\":\"%d\"}", i)
    );
  }

  private void broadcastQuestion(String qs, String lobbyId) {
    messageService.sendMessageToClient("/topic/game/" + lobbyId, qs);
  }
}
