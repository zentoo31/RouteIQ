package com.zentoodevs.login.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zentoodevs.login.repository.UserRepository;
import com.zentoodevs.login.repository.callbacks.UserRepositoryCallback;
import android.util.Log;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> userLiveData = new MutableLiveData<>();

    public UserViewModel(){
        userRepository = new UserRepository();
    }
    public void loadUsers(){
        userRepository.getUsers(new UserRepositoryCallback() {
            @Override
            public void onUserLoaded(String user) {
                userLiveData.setValue(user);
            }

            @Override
            public void onError(String message) {
                Log.e("UserViewModel", "Error al cargar usuarios desde la API: " + message);
            }
        });

    }

    public LiveData<String> getUsersLiveData(){
        return userLiveData;
    }
}
