package com.bitnews.bitnews.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseList<T> {
    @SerializedName("results")
    private List<T> items;
    private int count;
    private String next;
    private String previous;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
