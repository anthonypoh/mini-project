package sg.edu.nus.miniproject.model;

import java.util.Objects;

public class Lobby {

  private String lobbyId;
  private Integer numOfQuestions;
  private String category;
  private String difficulty;

  public String getLobbyId() {
    return this.lobbyId;
  }

  public void setLobbyId(String lobbyId) {
    this.lobbyId = lobbyId;
  }

  public Integer getNumOfQuestions() {
    return this.numOfQuestions;
  }

  public void setNumOfQuestions(Integer numOfQuestions) {
    this.numOfQuestions = numOfQuestions;
  }

  public String getCategory() {
    return this.category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getDifficulty() {
    return this.difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Lobby lobby = (Lobby) o;
    return (
      Objects.equals(lobbyId, lobby.lobbyId) &&
      Objects.equals(numOfQuestions, lobby.numOfQuestions) &&
      Objects.equals(category, lobby.category) &&
      Objects.equals(difficulty, lobby.difficulty)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(lobbyId, numOfQuestions, category, difficulty);
  }
}
