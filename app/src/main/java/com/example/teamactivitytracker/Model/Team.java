package com.example.teamactivitytracker.Model;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Team implements Serializable {

    private String tid;
    private String name;
    private HashMap<String, String> players;
    private HashMap<String, String> coaches;
    private HashMap<String, Integer> playerPoints;
    private HashMap<String, Integer> playerPeriodPoints;
    private Activity[] activities;

    // New team
    public Team(String tid, String name, String cid, String coachName) {
        this.tid = tid;
        this.name = name;
        this.players = new HashMap<>();
        this.coaches = new HashMap<>();
        coaches.put(cid, coachName);
        this.playerPoints = new HashMap<String, Integer>();
        this.playerPeriodPoints = new HashMap<String, Integer>();
        this.activities = new Activity[0];
    }

    // Load team without activities
    public Team(String tid, String name, HashMap<String, String> players, HashMap<String, String> coaches, HashMap<String, Integer> playerPoints, HashMap<String, Integer> playerPeriodPoints) {
        this.tid = tid;
        this.name = name;
        this.players = players;
        this.coaches = coaches;
        this.playerPoints = playerPoints;
        this.playerPeriodPoints = playerPeriodPoints;
        this.activities = new Activity[0];
    }

    // Load team with activities
    public Team(String tid, String name, HashMap<String, String> players, HashMap<String, String> coaches, HashMap<String, Integer> playerPoints, HashMap<String, Integer> playerPeriodPoints, Activity[] activities) {
        this.tid = tid;
        this.name = name;
        this.players = players;
        this.coaches = coaches;
        this.playerPoints = playerPoints;
        this.playerPeriodPoints = playerPeriodPoints;
        this.activities = activities;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public HashMap<String, Integer> getPlayerPoints() {
        return playerPoints;
    }

    public void setPlayerPoints(HashMap<String, Integer> playerPoints) {
        this.playerPoints = playerPoints;
    }

    public HashMap<String, Integer> getPlayerPeriodPoints() {
        return playerPeriodPoints;
    }

    public void setPlayerPeriodPoints(HashMap<String, Integer> playerPeriodPoints) {
        this.playerPeriodPoints = playerPeriodPoints;
    }

    public Activity[] getActivities() {
        return activities;
    }

    public void setActivities(Activity[] activities) {
        this.activities = activities;
    }
}
