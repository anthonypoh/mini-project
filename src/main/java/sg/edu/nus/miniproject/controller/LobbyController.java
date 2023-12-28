package sg.edu.nus.miniproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import sg.edu.nus.miniproject.model.Lobby;
import sg.edu.nus.miniproject.repo.LobbyRepo;

@Controller
@RequestMapping(path = "/lobby")
public class LobbyController {

  @Autowired
  private LobbyRepo lr;

  private Set<Lobby> lobbies = new HashSet<>();

  private static final String ALPHANUMERIC_CHARS =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int ID_LENGTH = 6;

  @GetMapping
  public ModelAndView getLobby(
    @RequestParam("lobbyId") String lobbyId,
    @RequestParam("name") String name
  ) throws JsonMappingException, JsonProcessingException {
    ModelAndView mav = new ModelAndView("lobby");
    boolean added = lr.addPlayer(name, lobbyId);
    if (added) {
      mav.addObject("added", added);
      mav.setViewName("index");
      return mav;
    }
    mav.addObject("name", name);
    mav.addObject("lobbyId", lobbyId);
    return mav;
  }

  @GetMapping(path = "/create-lobby")
  public ModelAndView createLobby(@ModelAttribute Lobby lobby)
    throws IOException {
    // System.out.println(lobby.getNumOfQuestions());
    // System.out.println(lobby.getCategory());
    // System.out.println(lobby.getDifficulty());
    // String jsonRequest = apiCallService.fetchDataFromApi(
    //   "https://opentdb.com/api.php?amount=10&category=18&difficulty=medium"
    // );
    // List<String> questions = lobbyService.parseJson(jsonRequest);
    // for (String question : questions) {
    //   System.out.println(question);
    // }

    ModelAndView mav = new ModelAndView("host-lobby");

    // if have time, do it with redis, get keys -> loop -> verifiy -> add keys
    lobby.setLobbyId(generateUniqueId());
    while (!lobbies.add(lobby)) {
      lobby.setLobbyId(generateUniqueId());
    }
    String lobbyId = lobby.getLobbyId();
    lr.createLobby(lobbyId);
    mav.addObject("lobbyId", lobbyId);
    // lobby = new Lobby();
    // mav.addObject("lobbies", lobbies);
    // mav.setViewName("redirect:/lobby");

    return mav;
  }

  public static String generateUniqueId() {
    StringBuilder sb = new StringBuilder(ID_LENGTH);
    SecureRandom random = new SecureRandom();

    for (int i = 0; i < ID_LENGTH; i++) {
      int randomIndex = random.nextInt(ALPHANUMERIC_CHARS.length());
      char randomChar = ALPHANUMERIC_CHARS.charAt(randomIndex);
      sb.append(randomChar);
    }

    return sb.toString();
  }
}
