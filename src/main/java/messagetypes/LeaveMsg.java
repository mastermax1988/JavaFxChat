package messagetypes;

import java.io.Serializable;

public class LeaveMsg extends Message {

  private static final long serialVersionUID = -6663593544237894402L;
  public String name;
  public LeaveMsg(String name){
    this.name = name;
  }
}
