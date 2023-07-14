package com.example.playchat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    // List of all client handlers
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    /**
     * Constructor for the ClientHandler class.
     *
     * @param socket The socket associated with the client.
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = bufferedReader.readLine();
            clientHandlers.add(this);
            boradcastMessage("SERVER: " + name + " has entered the room");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                // Read the message from the client
                messageFromClient = bufferedReader.readLine();
                // Broadcast the message to all connected clients
                boradcastMessage(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param messageToSend The message to be broadcasted.
     */
    public void boradcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // Send the message to all clients except the sender
                if (!clientHandler.name.equals(name)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * Removes the client handler from the list and broadcasts a message about the client leaving.
     */
    public void removeClientHandler() {
        clientHandlers.remove(this);
        boradcastMessage("SERVER: " + name + " has left");
    }

    /**
     * Closes the socket, reader, and writer associated with the client handler.
     *
     * @param socket          The socket to be closed.
     * @param bufferedReader  The reader to be closed.
     * @param bufferedWriter  The writer to be closed.
     */
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}
