package sg.edu.nus.miniproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.nus.miniproject.model.Question;
import sg.edu.nus.miniproject.repo.LobbyRepo;
import sg.edu.nus.miniproject.service.LobbyService;

@RestController
@RequestMapping(path = "/api")
public class LobbyRestController {

  @Autowired
  private LobbyService ls;

  @Autowired
  private LobbyRepo lr;

  ObjectMapper objectMapper = new ObjectMapper();

  @PostMapping(
    path = "/check/{lobbyId}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<String> checkAnswer(
    @PathVariable("lobbyId") String lobbyId,
    @RequestBody String jsonRequest
  ) throws JsonMappingException, JsonProcessingException {
    JsonNode jsonNode = objectMapper.readTree(jsonRequest);
    String playerName = jsonNode.get("playerName").asText();
    String question = jsonNode.get("question").asText();
    String answer = jsonNode.get("answer").asText();
    int points = jsonNode.get("points").asInt();
    String message = "{\"message\": \"wrong\"}";

    List<Question> questions = lr.getQuestions(lobbyId);
    boolean correct = ls.checkAnswer(questions, lobbyId, question, answer);

    if (correct) {
      lr.updatePoints(playerName, lobbyId, points);
      message = "{\"message\": \"correct\"}";
    }

    return new ResponseEntity<>(message, HttpStatus.OK);
  }
}
