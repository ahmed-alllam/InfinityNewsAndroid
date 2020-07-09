package com.bitnews.bitnews.data.models;

import androidx.room.Entity;

import java.util.Objects;

@Entity
public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String profilePhoto;
    private boolean isCurrentUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(profilePhoto, user.profilePhoto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, firstName, lastName, profilePhoto);
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        isCurrentUser = currentUser;
    }
}
