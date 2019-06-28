package com.example.teamactivitytracker.Model;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Activity implements Serializable {

    private String aid;
    private String name;
    private String description;
    private int points;
    private String tid;
    private int limit;
    private LimitPeriod limitPeriod;

    public Activity(String aid, String name, String description, int points, String tid, int limit, LimitPeriod limitPeriod) {
        this.aid = aid;
        this.name = name;
        this.description = description;
        this.points = points;
        this.tid = tid;
        this.limit = limit;
        this.limitPeriod = limitPeriod;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public LimitPeriod getLimitPeriod() {
        return limitPeriod;
    }

    public void setLimitPeriod(LimitPeriod limitPeriod) {
        this.limitPeriod = limitPeriod;
    }
}