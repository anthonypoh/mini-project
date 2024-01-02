package sg.edu.nus.miniproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import sg.edu.nus.miniproject.model.Question;
import sg.edu.nus.miniproject.model.QuestionResponse;

@Service
public class LobbyService {

  @Autowired
  private ApiCallService apiCallService;

  private int initCountdownValue = 10;
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

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

  public List<Question> getQuestions(
    int amount,
    String category,
    String difficulty
  ) throws JsonMappingException, JsonProcessingException {
    String jsonRequest = apiCallService.fetchDataFromApi(
      String.format(
        "https://opentdb.com/api.php?amount=%d&category=%s&difficulty=%s",
        amount,
        category,
        difficulty
      )
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

  @PostConstruct
  public void startCountdown() {
    executorService.scheduleAtFixedRate(
      this::updateCountdown,
      0,
      1,
      TimeUnit.SECONDS
    );
  }

  public int getinitCountdownValue() {
    return initCountdownValue;
  }

  private void updateCountdown() {
    if (initCountdownValue > 0) {
      initCountdownValue--;
    } else {
      executorService.shutdown();
    }
  }
}
