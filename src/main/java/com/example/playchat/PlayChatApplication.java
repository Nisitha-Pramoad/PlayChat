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
        FXMLLoader fxmlLoader = new FXMLLoader(PlayChatApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("PlayChat");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        startServer();
        launch();

    }

    static void startServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ServerSocket serverSocket = new ServerSocket(5000);
                    Server server = new Server(serverSocket);
                    System.out.println("server is connected..");
                    server.serverStart();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}