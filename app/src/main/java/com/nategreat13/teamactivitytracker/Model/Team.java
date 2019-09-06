package com.nategreat13.teamactivitytracker.Model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("serial")
public class Team implements Serializable {

    private String tid;
    private String name;
    private HashMap<String, String> players;
    private HashMap<String, String> coaches;
    private HashMap<String, Long> playerPoints;
    private HashMap<String, Long> playerPeriodPoints;
    private Activity[] activities;
    private Boolean showLeaderboard;

    // New team
    public Team(String tid, String name, String cid, String coachName, Boolean showLeaderboard) {
        this.tid = tid;
        this.name = name;
        this.players = new HashMap<>();
        this.coaches = new HashMap<>();
        this.coaches.put(cid, coachName);
        this.playerPoints = new HashMap<>();
        this.playerPeriodPoints = new HashMap<>();
        this.activities = new Activity[0];
        this.showLeaderboard = showLeaderboard;
    }

    // Load team without activities
    public Team(String tid, String name, HashMap<String, String> players, HashMap<String, String> coaches, HashMap<String, Long> playerPoints, HashMap<String, Long> playerPeriodPoints, Boolean showLeaderboard) {
        this.tid = tid;
        this.name = name;
        this.players = players;
        this.coaches = coaches;
        this.playerPoints = playerPoints;
        this.playerPeriodPoints = playerPeriodPoints;
        this.activities = new Activity[0];
        this.showLeaderboard = showLeaderboard;
    }

    // Load team with activities
    public Team(String tid, String name, HashMap<String, String> players, HashMap<String, String> coaches, HashMap<String, Long> playerPoints, HashMap<String, Long> playerPeriodPoints, Activity[] activities, Boolean showLeaderboard) {
        this.tid = tid;
        this.name = name;
        this.players = players;
        this.coaches = coaches;
        this.playerPoints = playerPoints;
        this.playerPeriodPoints = playerPeriodPoints;
        this.activities = activities;
        this.showLeaderboard = showLeaderboard;
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

    public HashMap<String, Long> getPlayerPoints() {
        return playerPoints;
    }

    public void setPlayerPoints(HashMap<String, Long> playerPoints) {
        this.playerPoints = playerPoints;
    }

    public HashMap<String, Long> getPlayerPeriodPoints() {
        return playerPeriodPoints;
    }

    public void setPlayerPeriodPoints(HashMap<String, Long> playerPeriodPoints) {
        this.playerPeriodPoints = playerPeriodPoints;
    }

    public Activity[] getActivities() {
        return activities;
    }

    public void setActivities(Activity[] activities) {
        this.activities = activities;
    }

    public void addActivity(Activity activity) {
        Activity[] newActivities = new Activity[activities.length + 1];
        for (int i = 0; i < activities.length; i++) {
            newActivities[i] = activities[i];
        }
        newActivities[activities.length] = activity;
        activities = newActivities;
    }

    public void editAdtivity(Activity activity, int index) {
        activities[index] = activity;
    }

    public Boolean getShowLeaderboard() {
        return showLeaderboard;
    }

    public void setShowLeaderboard(Boolean showLeaderboard) {
        this.showLeaderboard = showLeaderboard;
    }

}
