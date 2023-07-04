package com.example.playchat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = bufferedReader.readLine();
            clientHandlers.add(this);
            boradcastMessage("SERVER" + name + "has entered in the room");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }

    }


    @Override
    public void run() {
        String messageFromClient;

        try {
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();
                boradcastMessage(messageFromClient);
            }
        }catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
            break;
        }
    }

    public void boradcastMessage(String messageToSend) {
        for (ClientHandler clientHandler: clientHandlers){
            try {
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

    public void removeClientHandler() {
        clientHandlers.remove(this);
        boradcastMessage("server " + name + "has gone");
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){

        removeClientHandler();
        try {
            if (bufferedReader!= null) {
                bufferedReader.close();
            }
            if (bufferedWriter!=null){
                bufferedWriter.close();
            }
            if (socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}
