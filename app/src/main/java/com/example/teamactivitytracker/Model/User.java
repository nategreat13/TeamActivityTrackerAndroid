package com.example.teamactivitytracker.Model;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class User implements Serializable {

    private String uid;
    private String email;
    private String firstName;
    private String lastName;
    private HashMap<String, String> players;
    private HashMap<String, String> coaches;

    public User(String uid, String email, String firstName, String lastName) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.players = new HashMap<>();
        this.coaches = new HashMap<>();
    }

    public User(String uid, String email, String firstName, String lastName, HashMap<String, String> players, HashMap<String, String> coaches) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.players = players;
        this.coaches = coaches;
    }

    public User() {
        this.uid = "";
        this.email = "";
        this.firstName = "";
        this.lastName = "";
        this.players = new HashMap<>();
        this.coaches = new HashMap<>();
    }

    public void addPlayer(String pid, String playerName) {
        players.put(pid, playerName);
    }

    public void addCoach(String cid, String coachName) {
        coaches.put(cid, coachName);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public HashMap<String, String> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, String> players) {
        this.players = players;
    }

    public HashMap<String, String> getCoaches() {
        return coaches;
    }

    public void setCoaches(HashMap<String, String> coaches) {
        this.coaches = coaches;
    }
}
