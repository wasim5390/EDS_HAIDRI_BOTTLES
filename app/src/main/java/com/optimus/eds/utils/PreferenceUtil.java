package com.optimus.eds.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.optimus.eds.model.AppUpdateModel;
import com.optimus.eds.model.Configuration;
import com.optimus.eds.model.WorkStatus;
import com.optimus.eds.ui.home.User;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by sidhu on 6/8/2018.
 */

public class PreferenceUtil {

    public static final String KEY_IS_SIGN_IN = "is_sign_in";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_SYNC_DATE = "sync_date";
    public static final String KEY_USER = "user";
    public static final String KEY_CONFIG = "config";
    public static final String KEY_DIST_ID = "dist_id";

    private static final String PREFERENCE_NAME = "send_signal_preference";
    private static final String KEY_APP_MODE = "app_mode";
    private static final String KEY_FIREBASE_TOKEN = "firebase_token";
    private static final String KEY_APK_UPDATE = "apk_update";
    private static final String TARGET_ACHIEVEMENT = "target_achievement";
    private static final String HIDE_CUSTOMER_INFO = "hide_customer_info";
    private static final String PUNCH_ORDER_IN_UNITS = "punch_order";
    private static final String ASSETS_SCANNED = "assets_scanned";
    private static final String ASSETS_SCANNED_IN_LAST_MONTH = "assets_scanned_in_last_month";
    private static final String DELIVERY_DATE = "delivery_date";
    private static final String VISIT_TIME = "visit_time";
    private String defaultAppMode = "Production";

    private static PreferenceUtil instance;

    private SharedPreferences sPref;

    private PreferenceUtil(Context context) {
        sPref = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
    }

    public static PreferenceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceUtil(context);
        }
        return instance;
    }

    public boolean isSignIn() {
        return sPref.getBoolean(KEY_IS_SIGN_IN, false);
    }

    public void setSignIn(boolean value) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(KEY_IS_SIGN_IN, value);
        editor.apply();
    }

    public Long getVisitTime() {
        return sPref.getLong(VISIT_TIME, 0L);
    }

    public void setVisitTime(Long value) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putLong(VISIT_TIME, value);
        editor.apply();
    }

    public Long getDeliveryDate() {
        return sPref.getLong(DELIVERY_DATE, -1);
    }

    public void setDeliveryDate(Long value) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putLong(DELIVERY_DATE, value);
        editor.apply();
    }


    public void setTargetAchievement(String targetAchievement) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(TARGET_ACHIEVEMENT, targetAchievement);
        editor.apply();
    }

    public String getTargetAchievement() {
        return sPref.getString(TARGET_ACHIEVEMENT, null);
    }

    public void setHideCustomerInfo(Boolean customerInfo) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(HIDE_CUSTOMER_INFO, customerInfo);
        editor.apply();
    }

    public Boolean getHideCustomerInfo() {
        return sPref.getBoolean(HIDE_CUSTOMER_INFO, false);

    }

    public void setAssetsScanned(Boolean customerInfo) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(ASSETS_SCANNED, customerInfo);
        editor.apply();
    }

    public Boolean getAssetsScanned() {
        return sPref.getBoolean(ASSETS_SCANNED, true);

    }


    public void setAssetsScannedInLastMonth(Boolean assetsScannedInLastMonth) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(ASSETS_SCANNED_IN_LAST_MONTH, assetsScannedInLastMonth);
        editor.apply();
    }

    public Boolean getAssetScannedInLastMonth() {
        return sPref.getBoolean(ASSETS_SCANNED_IN_LAST_MONTH, false);

    }

    public void setPunchOrderInUnits(Boolean customerInfo) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(PUNCH_ORDER_IN_UNITS, customerInfo);
        editor.apply();
    }

    public Boolean getPunchOrder() {
        return sPref.getBoolean(PUNCH_ORDER_IN_UNITS, false);

    }

    public String getUsername() {
        return sPref.getString(KEY_USERNAME, "");
    }

    public String getPassword() {
        return sPref.getString(KEY_PASSWORD, "");
    }

    public String getToken() {
        return sPref.getString(KEY_TOKEN, "");
    }

    public Integer getDistributionId() {
        int distId = sPref.getInt(KEY_DIST_ID, -1);
        return distId == -1 ? null : distId;
    }

    public WorkStatus getWorkSyncData() {
        String workStatus = sPref.getString(KEY_SYNC_DATE, "");
        if (workStatus.isEmpty())
            return new WorkStatus(0);
        Gson gson = new Gson();
        WorkStatus status = gson.fromJson(workStatus, WorkStatus.class);
        return status;
    }


    public String getAppMode() {
        return sPref.getString(KEY_APP_MODE, defaultAppMode);
    }

    public void saveUserName(String username) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public void saveDistributionId(Integer distId) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(KEY_DIST_ID, distId);
        editor.apply();
    }

    public void savePassword(String password) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sPref.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public void clearCredentials() {
        SharedPreferences.Editor editor = sPref.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_PASSWORD);
        editor.apply();
    }


    public void saveWorkSyncData(WorkStatus status) {
        Gson gson = new Gson();
        String statusString = gson.toJson(status);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(KEY_SYNC_DATE, statusString);
        editor.apply();
    }

    public void saveUpdatedApkData(AppUpdateModel updateModel) {
        Gson gson = new Gson();
        String statusString = gson.toJson(updateModel);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(KEY_APK_UPDATE, statusString);
        editor.apply();
    }

    public AppUpdateModel getUpdatedVersion() {
        String update = sPref.getString(KEY_APK_UPDATE, "");
        if (update.isEmpty())
            return null;
        Gson gson = new Gson();
        AppUpdateModel data = gson.fromJson(update, AppUpdateModel.class);
        return data;
    }

    public void saveAppMode(String mode) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(KEY_APP_MODE, mode);
        editor.apply();
    }

    public void saveAccount(User user) {
        Gson gson = new Gson();
        String str = gson.toJson(user);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(KEY_USER, str);
        editor.apply();
    }

    public User getAccount() {
        Gson gson = new Gson();
        User user = gson.fromJson(sPref.getString(KEY_USER, ""), User.class);

        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void saveConfig(Configuration configuration) {
        Gson gson = new Gson();
        String str = gson.toJson(configuration);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(KEY_CONFIG, str);
        editor.apply();
    }

    public Configuration getConfig() {
        Gson gson = new Gson();
        Configuration configuration = gson.fromJson(sPref.getString(KEY_CONFIG, ""), Configuration.class);

        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    public void savePreference(String key, String value) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void savePreference(String key, Boolean value) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public String getPreference(String key) {
        return sPref.getString(key, "");
    }

    public String getColorPreference(String key, String defaultColor) {
        return sPref.getString(key, defaultColor);
    }

    public Boolean getBooleanPreference(String key, boolean defaultVal) {
        return sPref.getBoolean(key, defaultVal);
    }

    public void clearAllPreferences() {
        SharedPreferences.Editor editor = sPref.edit();
        editor.clear();
        editor.commit();
    }

}
