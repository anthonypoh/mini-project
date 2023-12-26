package sg.edu.nus.miniproject.controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import sg.edu.nus.miniproject.model.Lobby;
import sg.edu.nus.miniproject.service.ApiCallService;
import sg.edu.nus.miniproject.service.LobbyService;
import sg.edu.nus.miniproject.websocket.WebSocketConfig;

@Controller
@RequestMapping(path = "/lobby")
public class LobbyController {

  @Autowired
  private ApiCallService apiCallService;

  @Autowired
  private LobbyService lobbyService;

  // @Autowired
  // private MessageService messageService;
  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  private Set<Lobby> lobbies = new HashSet<>();

  private static final String ALPHANUMERIC_CHARS =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int ID_LENGTH = 6;

  @GetMapping
  public ModelAndView getLobby(
    @RequestParam("lobbyId") String lobbyId,
    @RequestParam("name") String name
  ) {
    ModelAndView mav = new ModelAndView("lobby");
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
    lobby.setLobbyId(generateUniqueId());
    while (!lobbies.add(lobby)) {
      lobby.setLobbyId(generateUniqueId());
    }
    String lobbyId = lobby.getLobbyId();
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