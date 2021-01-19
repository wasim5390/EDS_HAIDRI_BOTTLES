package com.optimus.eds.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.optimus.eds.BuildConfig;
import com.optimus.eds.model.AppUpdateModel;

import java.io.File;
import java.util.List;



import androidx.core.content.FileProvider;

import static com.optimus.eds.EdsApplication.getContext;

public class AppUpdater {
    private static final AppUpdater ourInstance = new AppUpdater();

    public static AppUpdater getInstance() {
        return ourInstance;
    }

    private AppUpdater() {
    }



    public void installApk(Context context,File apkFile) {

        if (context != null) {
            Log.i("APK_SIZE",apkFile.length()+"");
            Log.i("APK_PATH",apkFile.getAbsolutePath());
            if(!apkFile.exists())
                return;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory("android.intent.category.DEFAULT");
            String str = "application/vnd.android.package-archive";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".provider",apkFile));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), str);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                context.startActivity(intent);
            }

        }
    }

    public void deleteInstalledApkFromDownloads(String fileName){
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName);
            if(file.exists()){
                file.delete();
            }
        }catch (Exception e){
            Log.e("AppUpdater: ",e.getMessage());
        }

    }

    public boolean apkChanged(AppUpdateModel updateModel){
        PackageInfo installedApp = null;
        long currentVersion;
        List<PackageInfo> packages = getContext().getPackageManager().getInstalledPackages(0);
        for(PackageInfo packageInfo:packages){
            if(packageInfo.packageName.equals( BuildConfig.APPLICATION_ID)){
                installedApp = packageInfo;
                break;
            }
        }
        if(installedApp==null)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            currentVersion = installedApp.getLongVersionCode();
        }else {
            currentVersion = installedApp.versionCode;
        }
        return currentVersion<updateModel.getVersion();
    }


}
