package sg.edu.nus.miniproject.model;

import java.util.List;

public class QuestionResponse {

  private int response_code;
  private List<Question> results;

  public int getResponse_code() {
    return this.response_code;
  }

  public void setResponse_code(int response_code) {
    this.response_code = response_code;
  }

  public List<Question> getResults() {
    return this.results;
  }

  public void setResults(List<Question> results) {
    this.results = results;
  }
}
