package com.zentoodevs.login.repository.callbacks;

public interface UserRepositoryCallback {
    void onUserLoaded(String user);
    void onError(String message);
}
