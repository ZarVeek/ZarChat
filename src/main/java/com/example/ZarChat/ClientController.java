package com.example.ZarChat;

import com.example.ZarChat.Client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


public class ClientController {
    @FXML
    private Button closeButton;
    @FXML
    private HBox loginBox;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private HBox messageBox;
    @FXML
    private ListView<String> clientList;
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageField;

    final ChatClient client;

    public ClientController() {
        client = new ChatClient(this);
    }

    public void addMessage(String message) {
        messageArea.appendText(message + "\n");
    }

    public void onClickSendButton(ActionEvent actionEvent) {
        final String message = messageField.getText();
        if (message != null && !message.isEmpty()) {
            client.sendMessage(message);
            messageField.clear();
            messageField.requestFocus();
        }
    }

    public void btnAuthClick(ActionEvent actionEvent) {
        client.sendMessage("/auth" + " " + loginField.getText() + " " + passwordField.getText());
    }

    public void selectClient(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            final String message = messageField.getText();
            final String client = clientList.getSelectionModel().getSelectedItem();
            messageField.setText("/w " + client + " " + message);
            messageField.requestFocus();
            messageField.selectEnd();
        }
    }

    public void setAuth(Boolean isAuthSuccess) {
        loginBox.setVisible(!isAuthSuccess);
        messageBox.setVisible(isAuthSuccess);
    }

    public void updateClientsList(String[] clients) {
        clientList.getItems().clear();
        clientList.getItems().addAll(clients);
    }
}