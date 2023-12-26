package sg.edu.nus.miniproject.model;

public class Message {

  private String cmd;
  private String content;

  public Message() {}

  public Message(String cmd, String content) {
    this.cmd = cmd;
    this.content = content;
  }

  public String getCmd() {
    return this.cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String getContent() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
