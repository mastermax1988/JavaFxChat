package server;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import messageTypes.RegisterMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerTest {

  private Socket socket;
  private ObjectOutputStream outputStream;
  private ObjectInputStream inputStream;

  @BeforeAll
  static void startServer() {
    Server server = new Server(8080);
    server.startServer();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @BeforeEach
  void setUp() {
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress("127.0.0.1", 8080));
      outputStream = new ObjectOutputStream(socket.getOutputStream());
      inputStream = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      fail(e);
    }
  }

  @AfterEach
  void tearDown() {
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testRegister() {
    try {
      outputStream.writeObject(new RegisterMessage());
    } catch (IOException e) {
      fail(e);
    }
  }
}
