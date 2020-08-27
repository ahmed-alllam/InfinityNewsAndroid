package com.infinitynews.infinitynews.callbacks;

public interface OnUserAuthRequestListener {

    void onRequestSuccessful();

    void onRequestPending();

    void onRequestFinished();
}
