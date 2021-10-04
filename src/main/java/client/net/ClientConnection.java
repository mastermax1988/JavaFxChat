package client.net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {
  public Socket socket;
  public ObjectInputStream inputStream;
  public ObjectOutputStream outputStream;
}
