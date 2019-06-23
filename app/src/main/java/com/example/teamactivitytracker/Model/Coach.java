package com.example.teamactivitytracker.Model;

import java.util.HashMap;

public class Coach {

    private String cid;
    private String firstName;
    private String lastName;
    private HashMap<String, String> teams;

    public Coach(String cid, String firstName, String lastName) {
        this.cid = cid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teams = new HashMap<>();
    }

    public Coach(String cid, String firstName, String lastName, String tid, String teamName) {
        this.cid = cid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teams = new HashMap<>();
        this.teams.put(tid, teamName);
    }

    public Coach(String cid, String firstName, String lastName, HashMap<String, String> teams) {
        this.cid = cid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teams = teams;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
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

    public HashMap<String, String> getTeams() {
        return teams;
    }

    public void setTeams(HashMap<String, String> teams) {
        this.teams = teams;
    }
}