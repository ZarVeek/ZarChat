package com.example.ZarChat.Server;

import com.example.ZarChat.Client.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {

    private final Map<String, ClientHandler> client;
    private final AuthService authService;

    public ChatServer() {
        client = new HashMap<>();
        authService = new InMemoryAuthService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                System.out.println("waiting for client");
                final Socket socket = serverSocket.accept();
                new ClientHandler(socket, this);
                System.out.println("client's connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickBusy(String nick) {
        return client.containsKey(nick);
    }

    public void subscribe(ClientHandler client) {
        this.client.put(client.getNick(), client);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler client) {
        this.client.remove(client.getNick(), client);
        broadcastClientList();
    }

    public void broadCast(String message) {
        for (ClientHandler client : client.values()) {
            client.sendMessage(message);

        }
    }

    public void sendMessageToClient(ClientHandler nickFrom, String nickTo, String message) {
        final ClientHandler clientTo = client.get(nickTo);
        if (clientTo != null) {
            clientTo.sendMessage("From " + nickFrom.getNick() + ": " + message);
            nickFrom.sendMessage("Участнику " + nickTo + ": " + message);
            return;
        }
        nickFrom.sendMessage("Участника с ником " + nickTo + " нет в чате");
    }
    public void broadcastClientList(){
        final StringBuilder message = new StringBuilder("");
        client.values().forEach(clients -> message.append(clients.getNick()).append(" "));
        broadcast(Command.CLIENTS, String.valueOf(message));
    }

    public void broadcast(Command command,String message) {
        for (ClientHandler client : client.values()) {
            client.sendCommandAndMessage(command, message);
        }
    }



    public boolean incorrectLoginOrPass(String login, String password) {
        return false;
    }
}
