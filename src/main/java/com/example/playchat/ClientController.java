package com.example.playchat;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private Button button_send;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vBox_message;

    @FXML
    private Label lblUserName;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBox_message.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            sp_main.setVvalue(newValue.doubleValue());
        });

        button_send.setOnAction(event -> {
            String messageToSend = tf_message.getText().trim();
            if (!messageToSend.isEmpty()) {
                sendMessage(name + " : " + messageToSend);
                addMessageToChat(name, messageToSend, true); // Add sender's message on the right side
                tf_message.clear();
            }
        });

        setUserName();
        startClient();
    }

    public ClientController(String name) {
        this.name = name;
    }

    private void startClient() {
        try {
            socket = new Socket("localhost", 5000);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            sendMessage(name);
            readMessages();
        } catch (IOException e) {
            closeAll();
            e.printStackTrace();
        }
    }

    private void readMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = bufferedReader.readLine()) != null) {
                    final String finalMessage = message;
                    Platform.runLater(() -> {
                        String senderName = finalMessage.substring(0, finalMessage.indexOf(" : "));
                        String messageContent = finalMessage.substring(finalMessage.indexOf(" : ") + 3);
                        addMessageToChat(senderName, messageContent, false); // Add received message on the left side
                    });
                }
            } catch (IOException e) {
                closeAll();
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeAll();
            e.printStackTrace();
        }
    }

    public void addMessageToChat(String sender, String messageContent, boolean isSender) {
        HBox hBox = new HBox();
        Text senderText = new Text(sender + ": ");
        Text messageText = new Text(messageContent);
        TextFlow textFlow = new TextFlow(senderText, messageText);

        textFlow.setStyle("-fx-background-color: rgb(15,125,242);" +
                "-fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));

        hBox.getChildren().add(textFlow);

        // Set alignment based on sender or receiver
        if (isSender) {
            hBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            hBox.setAlignment(Pos.CENTER_LEFT);
        }

        Platform.runLater(() -> {
            vBox_message.getChildren().add(hBox);
        });
    }

    private void setUserName() {
        lblUserName.setText(name);
    }

    private void closeAll() {
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
