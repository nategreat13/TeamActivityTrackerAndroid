package com.nategreat13.teamactivitytracker.Model;

import java.util.Date;

public class CompletedActivity {

    private String caid;
    private Activity activity;
    private String pid;
    private String tid;
    private int quantity;
    private int totalPoints;
    private String playerName;
    private Date date;

    public CompletedActivity(String caid, Activity activity, String pid, String tid, int quantity, int totalPoints, String playerName, Date date) {
        this.caid = caid;
        this.activity = activity;
        this.pid = pid;
        this.tid = tid;
        this.quantity = quantity;
        this.totalPoints = totalPoints;
        this.playerName = playerName;
        this.date = date;
    }

    public String getCaid() {
        return caid;
    }

    public void setCaid(String caid) {
        this.caid = caid;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}