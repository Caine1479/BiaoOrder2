package com.biaoorder2.bean;

public class EnrollInformation {
    private String id;
    private String password;
    private String username;
    private String headIcon;
    private String eventTime;
    private String eventIsDone;

    public EnrollInformation(String id, String password, String username, String headIcon, String eventTime, String eventIsDone) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.headIcon = headIcon;
        this.eventTime = eventTime;
        this.eventIsDone = eventIsDone;
    }

    public String getEventIsDone() {
        return eventIsDone;
    }

    public void setEventIsDone(String eventIsDone) {
        this.eventIsDone = eventIsDone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
