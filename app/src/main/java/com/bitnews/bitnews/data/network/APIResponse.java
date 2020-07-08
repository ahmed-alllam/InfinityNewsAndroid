package com.bitnews.bitnews.data.network;

import java.util.List;

public class APIResponse<T> {
    private List<T> items;
    private Throwable error;

    public APIResponse(List<T> items) {
        this.items = items;
        this.error = null;
    }

    public APIResponse(Throwable error) {
        this.error = error;
        this.items = null;
    }

    public List<T> getitems() {
        return items;
    }

    public void setitems(List<T> items) {
        this.items = items;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
