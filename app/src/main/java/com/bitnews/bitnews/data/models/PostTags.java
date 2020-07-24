package com.bitnews.bitnews.data.models;

import androidx.room.Entity;

@Entity(primaryKeys = {"postSlug", "tag"})
public class PostTags {
    private String postSlug;
    private String tag;
}
