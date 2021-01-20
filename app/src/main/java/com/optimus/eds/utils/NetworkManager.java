package com.optimus.eds.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import android.telephony.TelephonyManager;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NetworkManager {
    private static final NetworkManager ourInstance = new NetworkManager();

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    private NetworkManager() {
    }

    public Single<Boolean> isOnline() {
        return Single.fromCallable(() -> {
            try {
                // Connect to Google DNS to check for connection
                int timeoutMs = 1500;
                Socket socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);

                socket.connect(socketAddress, timeoutMs);
                socket.close();

                return true;
            } catch (IOException e) {
                return false;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<String> findIMEI(Context context) {
        MutableLiveData<String> imei = new MutableLiveData<>();
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= 26) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                    PermissionUtil.requestPermission((Activity) context, Manifest.permission.READ_PHONE_STATE, new PermissionUtil.PermissionCallback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onPermissionsGranted(String permission) {
                            imei.postValue(telephonyManager.getImei());
                        }

                        @Override
                        public void onPermissionsGranted() {

                        }

                        @Override
                        public void onPermissionDenied() {

                        }
                    });
                } else
                    imei.postValue(telephonyManager.getImei());

            } else {
                imei.postValue(telephonyManager.getDeviceId());
            }
        }catch (Exception e){
            e.printStackTrace();
            imei.postValue(null);
        }

        return imei;
    }

}
