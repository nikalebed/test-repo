package com.veronikalebedyuk.dialogforbetter.classes;

import java.util.Date;
import java.util.List;

public class User {
    //private int id;
    private String name;
    private String email;
    private Date dateOfBirth;
    private List<String> criterias;
    private List<Message> messages;
    static int ID_NUMBER;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public List<String> getCriterias() {
        return criterias;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
