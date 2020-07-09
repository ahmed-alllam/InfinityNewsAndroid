package com.bitnews.bitnews.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AuthToken {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
