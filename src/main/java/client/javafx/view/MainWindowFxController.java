package client.javafx.view;

import client.net.NetworkManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import messagetypes.ChatMessageRec;
import messagetypes.ChatMessageSend;
import messagetypes.RegisterMessage;

/** FX controller class for main window. */
public class MainWindowFxController implements PropertyChangeListener {

  NetworkManager networkManager;

  @FXML ListView<String> messagesListView;
  @FXML TextField chatInputField;
  private ObservableList<String> messagesList;

  public MainWindowFxController() {}

  @FXML
  void initialize() {
    messagesList = FXCollections.observableArrayList();
    messagesListView.setItems(messagesList);
    networkManager = NetworkManager.getInstance();
    networkManager.addPropertyChangeListener(this);
    networkManager.connect();

    // Asks for the name
    TextInputDialog nameInputDialog = new TextInputDialog("Enter your name!");
    nameInputDialog.setTitle("Name chooser");
    nameInputDialog.setContentText("Chat name: ");
    nameInputDialog.setHeaderText("Choose a name!");
    while (nameInputDialog.getEditor().getText().isEmpty()
        || nameInputDialog.getEditor().getText().equals(nameInputDialog.getDefaultValue())) {
      nameInputDialog.showAndWait();
    }

    networkManager.sendMessage(new RegisterMessage(nameInputDialog.getEditor().getText()));
    // shuts down network connection if window is about to be closed
    Platform.runLater(
        () ->
            messagesListView
                .getScene()
                .getWindow()
                .setOnCloseRequest((event) -> networkManager.shutdown()));
    Platform.runLater(() -> chatInputField.requestFocus());
  }

  @FXML
  void sendMessage() {
    networkManager.sendMessage(new ChatMessageSend(chatInputField.getText().trim()));
    chatInputField.clear();
    chatInputField.requestFocus();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    switch (evt.getPropertyName()) {
      case NetworkManager.USER_ENTERED:
        addMessage("[Server]: New user " + evt.getNewValue() + " entered.");
        break;
      case NetworkManager.USER_LEFT:
        addMessage("[Server]: User " + evt.getNewValue() + " left.");
        break;
      case NetworkManager.USER_MESSAGE:
        ChatMessageRec msg = (ChatMessageRec) evt.getNewValue();
        addMessage("User " + msg.name + " wrote: " + msg.msg);
        break;
    }
  }

  private void addMessage(String messageString) {
    Platform.runLater(() -> {
      messagesList.add(messageString);
      messagesListView.scrollTo(messagesListView.getItems().size());
    });
  }
}
