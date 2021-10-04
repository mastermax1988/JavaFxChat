package messagetypes;

import java.io.Serializable;

public class ChatMessageRec implements Serializable {

  private static final long serialVersionUID = -2533962131796153882L;

  public String name;
  public String msg;
  public ChatMessageRec(String name, String msg){
    this.msg = msg;
    this.name = name;
  }
}
