package client.console;

import client.net.NetworkManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import messagetypes.ChatMessageRec;
import messagetypes.ChatMessageSend;
import messagetypes.RegisterMessage;

public class ShellUI implements PropertyChangeListener {
  private final NetworkManager networkManager;
  private String name;
  private BufferedReader in;

  public ShellUI() {
    networkManager = NetworkManager.getInstance();
    networkManager.addPropertyChangeListener(this);
  }

  void start() {
    networkManager.connect();
    in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    try {
      System.out.println("[Client]: Enter your name");
      name = in.readLine();
      System.out.println("Type !quit to quit.");
      networkManager.sendMessage(new RegisterMessage(name));
      Thread consoleListener = new Thread(this::consoleListener);
      consoleListener.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void consoleListener() {
    try {
      while (true) {
        String msg = in.readLine();
        if (msg.startsWith("!quit")) {
          networkManager.removePropertyChangeListener(this);
          networkManager.shutdown();
          return;
        }
        networkManager.sendMessage(new ChatMessageSend(msg));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case NetworkManager.USER_ENTERED:
        System.out.println("[Server]: New user " + evt.getNewValue() + " entered.");
        break;
      case NetworkManager.USER_LEFT:
        System.out.println("[Server]: User " + evt.getNewValue() + " left.");
        break;
      case NetworkManager.USER_MESSAGE:
        ChatMessageRec msg = (ChatMessageRec) evt.getNewValue();
        System.out.println("User " + msg.name + " wrote: " + msg.msg);
        break;
      default:
    }
  }
}
