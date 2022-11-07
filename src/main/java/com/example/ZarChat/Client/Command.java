package com.example.ZarChat.Client;

import java.util.Arrays;

public enum Command {
    AUTHOK("/authok"),
    AUTH("/auth"),
    PRIVATE_MESSAGE("/w"),
    END("/end"),
    CLIENTS("/clients "),
    SIMPLE_MESSAGE(""),
    FILe ("/file");

    private String command;

    public String getCommand() {
        return command;
    }

    Command(String command) {
        this.command = command;
    }

    public static Command getCommandByText(String text) {
        System.out.println(text);
        return Arrays.stream(values())
                .filter(cmd -> text.startsWith(cmd.getCommand()))
                .findAny().orElseThrow(() -> new RuntimeException("Несуществующая команда"));
    }
    public static String getCommandPrefix(){
        return "/";
    }
}


