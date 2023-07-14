package com.example.playchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

public class PlayChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load the login.fxml file using FXMLLoader
        FXMLLoader fxmlLoader = new FXMLLoader(PlayChatApplication.class.getResource("view/login.fxml"));

        // Create the scene with the loaded login.fxml content
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        // Set the title of the stage
        stage.setTitle("PlayChat");

        // Set the scene on the stage
        stage.setScene(scene);

        // Display the stage
        stage.show();
    }

    public static void main(String[] args) {
        // Start the server in a separate thread
        startServer();

        // Launch the JavaFX application
        launch();
    }

    /**
     * Starts the server in a separate thread.
     */
    static void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create a ServerSocket on port 5000
                    ServerSocket serverSocket = new ServerSocket(5000);

                    // Create a Server instance with the ServerSocket
                    Server server = new Server(serverSocket);

                    System.out.println("Server is connected..");

                    // Start the server and accept client connections
                    server.serverStart();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
