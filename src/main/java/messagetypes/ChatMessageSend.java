package messagetypes;

import java.io.Serializable;

public class ChatMessageSend extends Message {

  private static final long serialVersionUID = -5080490434723273743L;
  public String msg;
  public ChatMessageSend(String msg){
    this.msg = msg;
  }
}
