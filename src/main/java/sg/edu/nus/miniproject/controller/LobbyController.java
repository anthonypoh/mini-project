package sg.edu.nus.miniproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
  public ModelAndView createLobby(
    @Valid @ModelAttribute Lobby lobby,
    BindingResult result
  ) throws IOException {
    ModelAndView mav = new ModelAndView("host-lobby");

    // Form Validation
    if (result.hasErrors()) {
      mav.setViewName("admin");
      return mav;
    }

    String lobbyId = generateUniqueId();

    while (lr.lobbyExists(lobbyId)) {
      lobbyId = generateUniqueId();
    }

    lobby.setLobbyId(lobbyId);
    lr.createLobby(lobby);
    mav.addObject("lobbyId", lobbyId);

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
