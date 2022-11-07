package com.example.ZarChat.Client;

import com.example.ZarChat.ClientController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final ClientController controller;

    public ChatClient(ClientController controller) {
        this.controller = controller;
        openConnection();
    }

    private void openConnection() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream((socket.getOutputStream()));
            new Thread(() -> {
                try {
                    while (true) {
                        String authMsg = in.readUTF();
                        if(Command.getCommandByText(authMsg) == Command.AUTHOK){
                            final String nick = authMsg.split(" ")[1];
                            controller.addMessage("successful auth with nick " + nick + "\n");
                            controller.setAuth(true);
                            break;
                        }
                    }
                    while (true){
                        final String message = in.readUTF();
                        if(message.startsWith(String.valueOf(Command.END))){
                            controller.setAuth(false);
                            break;
                        }
                        if (Command.getCommandByText(message) == Command.CLIENTS) {
                            final String[] clients = message.replace(Command.CLIENTS.getCommand() + " ", "").split(" ");
                            controller.updateClientsList(clients);
                        }
                        controller.addMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
