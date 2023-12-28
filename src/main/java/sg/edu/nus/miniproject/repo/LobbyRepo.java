package sg.edu.nus.miniproject.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import sg.edu.nus.miniproject.model.Player;
import sg.edu.nus.miniproject.model.Question;
import sg.edu.nus.miniproject.service.LobbyService;

@Repository
public class LobbyRepo {

  @Autowired
  private LobbyService lobbyService;

  @Autowired
  @Qualifier("myredis")
  private RedisTemplate<String, String> template;

  ObjectMapper objectMapper = new ObjectMapper();

  public List<Player> getPlayers(String lobbyId)
    throws JsonMappingException, JsonProcessingException {
    HashOperations<String, String, String> hashValue = template.opsForHash();
    String jsonPlayers = hashValue.get(lobbyId, "players");
    if (jsonPlayers.equals("newGame")) {
      List<Player> players = new ArrayList<>();
      return players;
    }
    List<Player> players = objectMapper.readValue(
      jsonPlayers,
      new TypeReference<List<Player>>() {}
    );

    return players;
  }

  public List<Question> getQuestions(String lobbyId)
    throws JsonMappingException, JsonProcessingException {
    HashOperations<String, String, String> hashValue = template.opsForHash();
    String jsonQuestions = hashValue.get(lobbyId, "questions");
    List<Question> questions = objectMapper.readValue(
      jsonQuestions,
      new TypeReference<List<Question>>() {}
    );
    return questions;
  }

  public void createLobby(String lobbyId)
    throws JsonMappingException, JsonProcessingException {
    HashOperations<String, String, String> hashValue = template.opsForHash();

    List<Question> questions = lobbyService.getQuestions();
    String jsonQuestions = objectMapper.writeValueAsString(questions);

    hashValue.put(lobbyId, "players", "newGame");
    hashValue.put(lobbyId, "questions", jsonQuestions);
    template.expire(lobbyId, 10, TimeUnit.MINUTES);
  }

  public boolean addPlayer(String name, String lobbyId)
    throws JsonMappingException, JsonProcessingException {
    boolean added = false;
    List<Player> players = getPlayers(lobbyId);
    for (Player player : players) {
      if (player.getName().equals(name)) {
        added = true;
      }
    }

    if (!added) {
      HashOperations<String, String, String> hashValue = template.opsForHash();
      players.add(new Player(name));
      String jsonPlayers = objectMapper.writeValueAsString(players);
      hashValue.put(lobbyId, "players", jsonPlayers);
    }
    return added;
  }

  public void updatePoints(String playerName, String lobbyId, int points)
    throws JsonMappingException, JsonProcessingException {
    List<Player> players = getPlayers(lobbyId);
    for (Player player : players) {
      if (player.getName().equals(playerName)) {
        player.setScore(player.getScore() + points);
        break;
      }
    }
    HashOperations<String, String, String> hashValue = template.opsForHash();
    String jsonPlayers = objectMapper.writeValueAsString(players);
    hashValue.put(lobbyId, "players", jsonPlayers);
  }
}
