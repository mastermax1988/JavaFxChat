package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import messagetypes.ChatMessageRec;
import messagetypes.EnterMsg;
import messagetypes.LeaveMsg;
import messagetypes.Message;

public class NetworkManager {

  private static NetworkManager instance;

  private ClientConnection clientConnection;


  private NetworkManager() {

  }

  public static NetworkManager getInstance() {
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
          System.out.println("[SERVER]: " + ((EnterMsg) msg).name + " entered the chat.");
        } else if (msg instanceof LeaveMsg) {
          System.out.println("[SERVER]: " + ((LeaveMsg) msg).name + " left.");
        } else if (msg instanceof ChatMessageRec) {
          ChatMessageRec c = (ChatMessageRec) msg;
          System.out.println("[SERVER:" + c.name + "]: " + c.msg);
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
}
