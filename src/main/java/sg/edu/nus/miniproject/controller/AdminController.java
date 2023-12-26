package sg.edu.nus.miniproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sg.edu.nus.miniproject.model.Lobby;

@Controller
@RequestMapping(path = "/admin")
public class AdminController {

  @GetMapping
  public String adminPage(Model m) {
    Lobby lobby = new Lobby();
    lobby.setNumOfQuestions(10);
    lobby.setDifficulty("Easy");
    m.addAttribute("lobby", lobby);
    return "admin";
  }
}