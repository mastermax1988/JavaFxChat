package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import messagetypes.ChatMessageRec;
import messagetypes.ChatMessageSend;
import messagetypes.EnterMsg;
import messagetypes.RegisterMessage;

public class Server {

  private int port;
  private ServerSocket serverSocket;
  private Set<ServerConnection> connections;

  public Server(int port) {
    this.port = port;
    connections = new HashSet<>();
  }

  public void startServer() {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("Server running");
    } catch (IOException e) {
      System.out.println("Can't start server;");
      e.printStackTrace();
      return;
    }
    Thread t = new Thread(this::awaitConnections);
    t.start();
  }

  private void awaitConnections() {
    while (!serverSocket.isClosed()) {
      try {
        System.out.println("waiting...");
        Socket clientSocket = serverSocket.accept();
        ServerConnection serverConnection =
            new ServerConnection(
                clientSocket,
                new ObjectInputStream(clientSocket.getInputStream()),
                new ObjectOutputStream(clientSocket.getOutputStream()));
        connections.add(serverConnection);
        Thread t = new Thread(() -> handleIncomingMessage(serverConnection));
        t.start();
      } catch (IOException e) {
        System.out.println("Server died.");
        break; // shuts down the server
      }
    }
  }

  private void handleIncomingMessage(ServerConnection serverConnection) {
    try {
      while (serverConnection.socket.isConnected()) {
        Object msg = serverConnection.inputStream.readObject();
        if (msg instanceof RegisterMessage) {
          serverConnection.clientName = ((RegisterMessage) msg).name;
          for (ServerConnection c : connections) {
            c.sendMessage(new EnterMsg(serverConnection.clientName));
          }
        } else if (msg instanceof ChatMessageSend) {
          for (ServerConnection c : connections) {
            c.sendMessage(
                new ChatMessageRec(serverConnection.clientName, ((ChatMessageSend) msg).msg));
          }
        }
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      connections.remove(serverConnection);
    }
  }
}
