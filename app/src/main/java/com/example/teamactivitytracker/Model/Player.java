package com.example.teamactivitytracker.Model;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Player implements Serializable {

    private String pid;
    private String firstName;
    private String lastName;
    private HashMap<String, String> teams;

    public Player(String pid, String firstName, String lastName) {
        this.pid = pid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teams = new HashMap<>();
    }

    public Player(String pid, String firstName, String lastName, String tid, String teamName) {
        this.pid = pid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teams = new HashMap<>();
        this.teams.put(tid, teamName);
    }

    public Player(String pid, String firstName, String lastName, HashMap<String, String> teams) {
        this.pid = pid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teams = teams;
    }

    public void addTeam(String tid, String teamName) {
        teams.put(tid, teamName);
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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