package com.bitnews.bitnews.data.network;


public class APIResponse<T> {
    private T item;
    private Throwable error;
    private Status status = Status.SUCCESFUL;

    public APIResponse() {
    }

    public APIResponse(T item) {
        this.item = item;
    }

    public APIResponse(Throwable error) {
        this.error = error;
    }

    public T getitem() {
        return item;
    }

    public void setitem(T item) {
        this.item = item;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {SUCCESFUL, NETWORK_FAILED, BAD_REQUEST}
}
