package com.example.playchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

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
        String clientName = txtNewLoginClient.getText().trim();

        if (!clientName.isEmpty()) {
            // Create a new stage for the client chat
            Stage primaryStage = new Stage();

            // Load the client FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/client.fxml"));

            // Create an instance of the ClientController and set it as the controller for the FXML file
            ClientController clientController = new ClientController(clientName);
            fxmlLoader.setController(clientController);

            // Set the scene for the client chat window
            primaryStage.setScene(new Scene(fxmlLoader.load()));
            primaryStage.setTitle("Chat Room");
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();

            // Add favicon to tile bar
            Image image = new Image("com/example/playchat/images/favicon 2.png");
            primaryStage.getIcons().add(image);

            primaryStage.show();

            // Clear the login text field
            txtNewLoginClient.clear();
        }else {
            // Show an information alert if the client name is empty
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a name to join the chatroom.");
            alert.showAndWait();
        }
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
