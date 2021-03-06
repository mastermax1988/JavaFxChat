package client.console;

import client.net.ClientConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import messagetypes.ChatMessageRec;
import messagetypes.ChatMessageSend;
import messagetypes.EnterMsg;
import messagetypes.LeaveMsg;
import messagetypes.RegisterMessage;

public class ShellUI {
  private BufferedReader in;
  private ClientConnection clientConnection;
  private String name;

  public ShellUI() {
    try {
      in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
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
  }

  void start() {
    try {
      System.out.println("[Client]: Enter your name");
      name = in.readLine();
      System.out.println("Type !quit to quit.");
      Thread serverListener = new Thread(this::serverListener);
      serverListener.start();
      clientConnection.outputStream.writeObject(new RegisterMessage(name));
      Thread consoleListener = new Thread(this::consoleListener);
      consoleListener.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void serverListener() {
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

  private void consoleListener() {
    try {
      while (true) {
        String msg = in.readLine();
        if(msg.startsWith("!quit")){
          clientConnection.outputStream.close();
          return;
        }
        clientConnection.outputStream.writeObject(new ChatMessageSend(msg));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
