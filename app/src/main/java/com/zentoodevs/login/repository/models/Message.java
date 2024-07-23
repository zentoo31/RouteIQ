package com.zentoodevs.login.repository.models;

public class Message {
    private final String content;
    private final String role; // "user" or "model"

    public Message(String content, String role) {
        this.content = content;
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public String getRole() {
        return role;
    }
}