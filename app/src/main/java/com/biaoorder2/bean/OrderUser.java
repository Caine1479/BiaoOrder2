package com.biaoorder2.bean;

public class OrderUser {
    private String id;
    private String password;
    private String headIcon;
    private String username;
    private String post;

    private int online;

    public OrderUser() {
    }

    public OrderUser(String headIcon, String username, String post,int online) {
        this.headIcon = headIcon;
        this.username = username;
        this.post = post;
        this.online = online;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
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

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }
}