package client.net;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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


  public static final String USER_ENTERED = "userEntered";
  public static final String USER_LEFT = "userLeft";
  public static final String USER_MESSAGE = "userMessage";
  private static NetworkManager instance;
  private PropertyChangeSupport propertyChangeSupport;

  private ClientConnection clientConnection;
  private StringProperty messages;

  private NetworkManager() {
    propertyChangeSupport = new PropertyChangeSupport(this);
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
          propertyChangeSupport.firePropertyChange(USER_ENTERED, null, ((EnterMsg) msg).name);
        } else if (msg instanceof LeaveMsg) {
          propertyChangeSupport.firePropertyChange(USER_LEFT, null, ((LeaveMsg) msg).name);
        } else if (msg instanceof ChatMessageRec) {
          ChatMessageRec c = (ChatMessageRec) msg;
          propertyChangeSupport.firePropertyChange(USER_MESSAGE, null, msg);
        }
      }
    } catch (IOException e) {
      System.out.println("Connection lost");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
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

  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
  }
}
