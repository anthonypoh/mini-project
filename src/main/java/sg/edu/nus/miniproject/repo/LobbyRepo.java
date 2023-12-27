package sg.edu.nus.miniproject.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
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

  public void createLobby(String lobbyId)
    throws JsonMappingException, JsonProcessingException {
    HashOperations<String, String, String> hashValue = template.opsForHash();

    List<Question> questions = lobbyService.getQuestions();
    String jsonQuestions = objectMapper.writeValueAsString(questions);
    System.out.println(jsonQuestions);

    hashValue.put(lobbyId, "players", "");
    hashValue.put(lobbyId, "questions", jsonQuestions);
  }
}
