package com.bitnews.bitnews.data.models;

import androidx.room.Entity;

@Entity
public class AuthToken {
    private String token;

    public String getToken() {
        return token;
    }
}
