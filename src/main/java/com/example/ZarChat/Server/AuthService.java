package com.example.ZarChat.Server;

public interface AuthService {
    String getNickByLoginAndPassword(String login, String password);
}
