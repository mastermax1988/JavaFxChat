package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection {
  final Socket socket;
  final ObjectInputStream inputStream;
  final ObjectOutputStream outputStream;

  ServerConnection(Socket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream){
    this.socket = socket;
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  synchronized void sendMessage(Object msg) throws IOException {
    outputStream.reset();
    outputStream.writeObject(msg);
  }
}
