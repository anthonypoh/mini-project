package sg.edu.nus.miniproject.model;

public class Player {

  private String name;
  private String lobbyId;

  public Player() {}

  public Player(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public String getLobbyId() {
    return this.lobbyId;
  }

  public void setLobbyId(String lobbyId) {
    this.lobbyId = lobbyId;
  }

  public void setName(String name) {
    this.name = name;
  }
}
