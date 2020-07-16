package com.bitnews.bitnews.callbacks;

public interface UserAuthFragmentListener {

    void sendAuthRequest();

    void setErrorMessage(int messageID);

    void setErrorMessageInvisible();
}
