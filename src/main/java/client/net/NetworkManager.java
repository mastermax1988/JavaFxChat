package client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import messagetypes.ChatMessageRec;
import messagetypes.EnterMsg;
import messagetypes.LeaveMsg;
import messagetypes.Message;

public class NetworkManager {

  private static NetworkManager instance;
  private boolean javaFxEnabled = false;

  private ClientConnection clientConnection;
  private StringProperty messages;

  private NetworkManager() {
    messages = new SimpleStringProperty("");
  }

  public static synchronized NetworkManager getInstance() {
    if (instance == null) {
      instance = new NetworkManager();
    }
    return instance;
  }

  public void connect() {
    try {
      clientConnection = new ClientConnection();
      clientConnection.socket = new Socket();
      clientConnection.socket.connect(new InetSocketAddress("127.0.0.1", 8080));
      clientConnection.outputStream =
          new ObjectOutputStream(clientConnection.socket.getOutputStream());
      clientConnection.inputStream =
          new ObjectInputStream(clientConnection.socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    new Thread(this::listenForMessages).start();
  }

  private void listenForMessages() {
    try {
      while (true) {
        Object msg = clientConnection.inputStream.readObject();
        if (msg instanceof EnterMsg) {
          addMessage("[SERVER]: " + ((EnterMsg) msg).name + " entered the chat.");
        } else if (msg instanceof LeaveMsg) {
          addMessage("[SERVER]: " + ((LeaveMsg) msg).name + " left.");
        } else if (msg instanceof ChatMessageRec) {
          ChatMessageRec c = (ChatMessageRec) msg;
          addMessage("[SERVER:" + c.name + "]: " + c.msg);
        }
      }
    } catch (IOException e) {
      System.out.println("Connection lost");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void addMessage(String messageString) {
    String newValue = messages.getValue() + "\n" + messageString;
    if (javaFxEnabled) {
      Platform.runLater(() -> messages.set(newValue));
    } else {
      messages.set(newValue);
    }
  }

  public void sendMessage(Message message) {
    try {
      clientConnection.outputStream.writeObject(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void shutdown() {
    try {
      clientConnection.outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public StringProperty messagesProperty() {
    return messages;
  }

  public void setJavaFxEnabled(boolean javaFxEnabled) {
    this.javaFxEnabled = javaFxEnabled;
  }
}
