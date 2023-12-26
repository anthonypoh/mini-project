package sg.edu.nus.miniproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Question {

  private String cmd;
  private String type;
  private String difficulty;
  private String category;
  private String question;

  @JsonProperty("correct_answer")
  private String correctAnswer;

  @JsonProperty("incorrect_answers")
  private List<String> incorrectAnswers;

  public Question() {}

  public Question(
    String type,
    String difficulty,
    String category,
    String question,
    String correctAnswer,
    List<String> incorrectAnswers
  ) {
    this.type = type;
    this.difficulty = difficulty;
    this.category = category;
    this.question = question;
    this.correctAnswer = correctAnswer;
    this.incorrectAnswers = incorrectAnswers;
  }

  public String getCmd() {
    return this.cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDifficulty() {
    return this.difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  public String getCategory() {
    return this.category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getQuestion() {
    return this.question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getCorrectAnswer() {
    return this.correctAnswer;
  }

  public void setCorrectAnswer(String correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  public List<String> getIncorrectAnswers() {
    return this.incorrectAnswers;
  }

  public void setIncorrectAnswers(List<String> incorrectAnswers) {
    this.incorrectAnswers = incorrectAnswers;
  }
}
