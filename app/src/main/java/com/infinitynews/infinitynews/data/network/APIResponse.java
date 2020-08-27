package com.infinitynews.infinitynews.data.network;


public class APIResponse<T> {
    private T item;
    private Throwable error;
    private Status status;

    public static <T> APIResponse<T> success(T item) {
        APIResponse<T> response = new APIResponse<>();
        response.status = Status.SUCCESFUL;
        response.item = item;
        return response;
    }

    public static <T> APIResponse<T> failed(Throwable t) {
        APIResponse<T> response = new APIResponse<>();
        response.status = Status.NETWORK_FAILED;
        response.error = t;
        return response;
    }

    public static <T> APIResponse<T> invalid(Throwable t) {
        APIResponse<T> response = new APIResponse<>();
        response.status = Status.BAD_REQUEST;
        response.error = t;
        return response;
    }

    public T getitem() {
        return item;
    }

    public Throwable getError() {
        return error;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {SUCCESFUL, NETWORK_FAILED, BAD_REQUEST}
}
