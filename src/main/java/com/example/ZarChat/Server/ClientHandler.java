package com.example.ZarChat.Server;

import com.example.ZarChat.Client.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final static String COMMAND_PREFIX = "/";
    private final Socket socket;
    private final ChatServer chatServer;
    private final DataInputStream in;
    private final DataOutputStream out;

    private String nick;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        try {
            this.nick = "";
            this.socket = socket;
            this.chatServer = chatServer;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                chatServer.unsubscribe(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMessage() {
        try {
            while (true) {
                final String message = in.readUTF();
                if (message.startsWith(Command.getCommandPrefix())) {
                    if (Command.getCommandByText(message) == Command.END) {
                        break;
                    }
                    if (Command.getCommandByText(message) == Command.PRIVATE_MESSAGE) {
                        final String[] split = message.split(" ");
                        final String nickTo = split[1];
                        chatServer.sendMessageToClient(this, nickTo, message.substring(Command.PRIVATE_MESSAGE.getCommand().length() + 2 + nickTo.length()));
                    }
                    continue;
                }
                    chatServer.broadCast(nick + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void authenticate() {
        while (true) {
            try {
                final String message = in.readUTF();
                if (Command.getCommandByText(message) == Command.AUTH) {
                    try {
                        final String[] split = message.split(" ");
                        final String login = split[1];
                        final String password = split[2];
                        nick = chatServer.getAuthService().getNickByLoginAndPassword(login, password);
                        if (nick != null) {
                            if (chatServer.isNickBusy(nick)) {
                                sendMessage("user is already logged in");
                                continue;
                            }
                            if (chatServer.incorrectLoginOrPass(login, password)) {
                                sendMessage("Incorrect Login or Pass");
                                continue;
                            }
                            sendCommandAndMessage(Command.AUTHOK, nick);
                            this.nick = nick;
                            chatServer.broadCast("User " + nick + " enter chat");
                            chatServer.subscribe(this);
                            break;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        sendMessage("Something was wrong. Example: /auth login pass");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendCommandAndMessage(Command command, String message) {
        try {
            out.writeUTF(command.getCommand() + " " + message + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getNick() {
        return nick;
    }
}
