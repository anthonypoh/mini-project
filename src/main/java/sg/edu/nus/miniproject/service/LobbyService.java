package sg.edu.nus.miniproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import sg.edu.nus.miniproject.model.Question;
import sg.edu.nus.miniproject.model.QuestionResponse;

@Service
public class LobbyService {

  public List<String> parseJson(String json) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
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
}
