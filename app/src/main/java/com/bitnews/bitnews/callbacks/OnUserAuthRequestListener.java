package com.bitnews.bitnews.callbacks;

public interface OnUserAuthRequestListener {

    void onRequestSuccessful();

    void onRequestPending();

    void onRequestFinished();
}
