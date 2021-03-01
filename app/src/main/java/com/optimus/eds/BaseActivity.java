package com.optimus.eds;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;


import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


/**
 * Created by sidhu on 4/10/2019.
 */

public abstract class BaseActivity extends AppCompatActivity implements Constant{

    public abstract int getID();
    public abstract void created(Bundle savedInstanceState);
    public abstract void showProgress();
    public abstract void hideProgress();
    private KProgressHUD progressHUD;
    protected Boolean enableMerchandise=true; // change this to enable/disable merchandise

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getID());
        created(savedInstanceState);

    }

    public void setToolbar(String title) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(title);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     */
    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_dialog_title);
        builder.setMessage(R.string.location_dialog_message);
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    protected void showSettingsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    // navigating user to app settings
    public void openLocationSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**** show progress *******************/

    public void showProgressD(Context context,boolean cancelable) {
        if(context ==null)
            return;
        if(progressHUD !=null) {
            if(progressHUD.isShowing())
                return;
        }
        try{
            progressHUD=  KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    //.setDetailsLabel("Downloading data")
                    .setCancellable(cancelable)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f);
            progressHUD.show();
        }catch (Exception e){
            Log.e("KProgressHUD","Context where dialog is showing may be null");
        }

    }

    public void hideProgressD(){
        try {
            if(progressHUD!=null && progressHUD.isShowing())
                progressHUD.dismiss();
        }catch (Exception e){
            Log.e("KProgressHUD","Activity may not be available while hiding this, window leak!");
        }

    }

    /**
     * Retrieves Outlet availability status.
     * @param statusString
     * @return
     */
    public int getOutletPopCode(String statusString) {
        String[] statusArray = getResources().getStringArray(R.array.pop_array);
        for (int i = 0; i < statusArray.length; i++)
        {
            if(statusArray[i].equalsIgnoreCase(statusString))
                return ++i;
        }
        return 1;

    }


}
