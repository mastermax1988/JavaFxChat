package client.javafx.view;

import client.net.NetworkManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import messagetypes.ChatMessageSend;
import messagetypes.RegisterMessage;

/** FX controller class for main window. */
public class MainWindowFxController {

  NetworkManager networkManager;

  @FXML Label chatMessagesLabel;

  @FXML TextField chatInputField;

  public MainWindowFxController() {
    networkManager = NetworkManager.getInstance();
    networkManager.setJavaFxEnabled(true);
    networkManager.connect();
    networkManager.sendMessage(new RegisterMessage("Hans"));
  }

  @FXML
  void initialize() {
    chatMessagesLabel.textProperty().bind(networkManager.messagesProperty());
  }

  @FXML
  void sendMessage() {
    networkManager.sendMessage(new ChatMessageSend(chatInputField.getText().trim()));
    chatInputField.clear();
    chatInputField.requestFocus();
  }
}
