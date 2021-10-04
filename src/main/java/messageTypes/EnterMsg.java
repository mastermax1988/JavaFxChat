package messageTypes;

import java.io.Serializable;

public class EnterMsg implements Serializable {
  private static final long serialVersionUID = 1773591252331086019L;
  public String name;
  public EnterMsg(String name){
    this.name = name;
  }
}
