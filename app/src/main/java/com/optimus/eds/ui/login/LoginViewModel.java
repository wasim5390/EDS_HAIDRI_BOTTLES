package com.optimus.eds.ui.login;

import android.app.Application;

import com.optimus.eds.model.BaseResponse;
import com.optimus.eds.source.TokenResponse;
import com.optimus.eds.utils.PreferenceUtil;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class LoginViewModel extends AndroidViewModel {

    private final LoginRepository repository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new LoginRepository(application);
    }

    public LiveData<TokenResponse> login(String username, String password){
       return repository.login(username,password);
    }

    public LiveData<BaseResponse> saveFirebaseToken(String token, String imei){
        return repository.postFirebaseToken(token,imei);
    }


    public LiveData<String> getMsg() {
        return repository.getError();
    }
}
