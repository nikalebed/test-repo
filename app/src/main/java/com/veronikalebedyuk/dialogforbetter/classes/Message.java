package com.veronikalebedyuk.dialogforbetter.classes;

import java.util.Date;

public class Message {
    public String username;
    public String messageText;
    public long messageTime;
    public String function;

    public Message(){};

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public Message(String username, String messageText, String function) {
        this.username = username;
        this.messageText = messageText;
        this.messageTime = new Date().getTime();
        this.function = function;
    }
}
