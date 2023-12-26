package sg.edu.nus.miniproject.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  private final SimpMessagingTemplate messagingTemplate;

  public MessageService(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public void sendMessageToClient(String destination, String message) {
    messagingTemplate.convertAndSend(destination, message);
  }
}
