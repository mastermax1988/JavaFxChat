package client.console;

import client.net.NetworkManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import messagetypes.ChatMessageSend;
import messagetypes.RegisterMessage;

public class ShellUI {
  private final NetworkManager networkManager;
  private String name;
  private BufferedReader in;

  public ShellUI() {
    networkManager = NetworkManager.getInstance();
  }

  void start() {
    networkManager.connect();
    in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    try {
      System.out.println("[Client]: Enter your name");
      name = in.readLine();
      System.out.println("Type !quit to quit.");
      networkManager
          .messagesProperty()
          .addListener(
              (property, oldValue, newValue) -> {
                String[] lines = newValue.split("\n");
                System.out.println(lines[lines.length - 1]);
              });
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
          networkManager.shutdown();
          return;
        }
        networkManager.sendMessage(new ChatMessageSend(msg));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
