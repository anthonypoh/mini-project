package sg.edu.nus.miniproject.model;

public class Player {

  private String name;
  private Integer score;

  public Player() {}

  public Player(String name) {
    this.name = name;
    this.score = 0;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getScore() {
    return this.score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }
}
