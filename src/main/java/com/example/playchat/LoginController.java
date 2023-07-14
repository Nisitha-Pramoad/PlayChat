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

    /**
     * Handles the action event when the Login button is clicked.
     * Retrieves the client name from the text field, initializes the client UI,
     * and opens a new window for the client chat.
     *
     * @param event The action event triggered by the Login button.
     * @throws IOException If an I/O error occurs while loading the client FXML file.
     */
    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        // Retrieve the client name from the text field
        String clientName = txtNewLoginClient.getText();

        // Create a new stage for the client chat
        Stage primaryStage = new Stage();

        // Load the client FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/client.fxml"));

        // Create an instance of the ClientController and set it as the controller for the FXML file
        ClientController clientController = new ClientController(clientName);
        fxmlLoader.setController(clientController);

        // Set the scene for the client chat window
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.setTitle(clientName);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();

        // Clear the login text field
        txtNewLoginClient.clear();
    }

    /**
     * Handles the action event when the Enter key is pressed in the login text field.
     * Calls the btnLoginOnAction method to handle the event.
     *
     * @param event The action event triggered by pressing Enter in the text field.
     * @throws IOException If an I/O error occurs while loading the client FXML file.
     */
    @FXML
    void txtLoginFieldOnAction(ActionEvent event) throws IOException {
        btnLoginOnAction(event);
    }
}
