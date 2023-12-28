package sg.edu.nus.miniproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import sg.edu.nus.miniproject.model.Question;
import sg.edu.nus.miniproject.model.QuestionResponse;
import sg.edu.nus.miniproject.repo.LobbyRepo;

@Service
public class LobbyService {

  @Autowired
  private ApiCallService apiCallService;

  ObjectMapper objectMapper = new ObjectMapper();

  public List<String> parseJson(String json) throws IOException {
    QuestionResponse questionResponse = objectMapper.readValue(
      json,
      QuestionResponse.class
    );

    return questionResponse
      .getResults()
      .stream()
      .map(this::formatQuestionAndAnswers)
      .collect(Collectors.toList());
  }

  private String formatQuestionAndAnswers(Question question) {
    StringBuilder formattedQuestion = new StringBuilder();
    formattedQuestion
      .append("Question: ")
      .append(question.getQuestion())
      .append("\n");
    formattedQuestion
      .append("Answer: ")
      .append(question.getCorrectAnswer())
      .append("\n");
    formattedQuestion
      .append("Incorrect Answers: ")
      .append(String.join(", ", question.getIncorrectAnswers()));

    return formattedQuestion.toString();
  }

  public List<Question> getQuestions()
    throws JsonMappingException, JsonProcessingException {
    String jsonRequest = apiCallService.fetchDataFromApi(
      "https://opentdb.com/api.php?amount=5&category=18&difficulty=medium"
    );
    QuestionResponse qr = objectMapper.readValue(
      jsonRequest,
      QuestionResponse.class
    );
    List<Question> questions = qr.getResults();
    return questions;
  }

  public boolean checkAnswer(
    List<Question> questions,
    String lobbyId,
    String question,
    String answer
  ) throws JsonMappingException, JsonProcessingException {
    boolean correct = false;
    // System.out.printf("the answer from js: %s\n", answer);

    for (Question q : questions) {
      if (HtmlUtils.htmlUnescape(q.getQuestion()).equals(question)) {
        if (HtmlUtils.htmlUnescape(q.getCorrectAnswer()).equals(answer)) {
          correct = true;
        }
        break;
      }
    }

    return correct;
  }
}
