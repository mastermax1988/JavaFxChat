package messagetypes;


import java.io.Serializable;

public class RegisterMessage implements Serializable {

  private static final long serialVersionUID = 8035308375938588001L;
  public String name;
  public RegisterMessage(String name){
    this.name = name;
  }
}
