package com.example.playchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoginController {

    @FXML
    private TextField txtNewLoginClient;

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {


        String clientName = txtNewLoginClient.getText();

        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/client.fxml"));

        ClientController clientController = new ClientController(clientName);
        fxmlLoader.setController(clientController);

        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.setTitle(clientName);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();

        //startServer();

    }




}
