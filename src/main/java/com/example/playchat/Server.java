package com.example.playchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    /**
     * Constructs a Server object with the specified ServerSocket.
     *
     * @param serverSocket The ServerSocket object used for accepting client connections.
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Starts the server and accepts incoming client connections.
     * Creates a ClientHandler for each connected client and starts a new thread to handle each client.
     */
    public void serverStart() {
        try {
            while (!serverSocket.isClosed()) {
                // Accept incoming client connection
                Socket socket = serverSocket.accept();
                System.out.println("Client socket accepted! New Friend Connected: " + socket.toString());

                // Create a ClientHandler for the connected client
                ClientHandler clientHandler = new ClientHandler(socket);

                // Start a new thread to handle the client's communication
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in serverStart");
        }
    }

    /**
     * Closes the server by closing the ServerSocket.
     */
    public void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in closeServer");
        }
    }

    /**
     * The main method to start the server.
     * Creates a ServerSocket on the specified port and starts the server.
     *
     * @param args Command-line arguments (not used in this implementation).
     * @throws IOException If an I/O error occurs while creating the ServerSocket.
     */
    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.serverStart();
    }
}

