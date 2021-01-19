package com.optimus.eds.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import com.optimus.eds.Constant;
import com.optimus.eds.R;
import com.optimus.eds.ui.cash_memo.CashMemoActivity;
import com.optimus.eds.ui.route.outlet.detail.OutletDetailActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtil {

    private static NotificationUtil instance;
    private String channelId="Eds_Order_Booking";
    private int notificationId = 10;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notification =null;
    int progressMax = 100;
    private Context context;


    public static NotificationUtil getInstance(Context context){
        if(instance==null)
            instance = new NotificationUtil(context);
        return instance;
    }

    public NotificationUtil(Context context) {
        this.context = context;
        notificationManager = NotificationManagerCompat.from(context);
    }

    public void showNotification(){
        String contentText = "Uploading Orders in Progress...";


        notification = new NotificationCompat.Builder(context,channelId)
                .setContentTitle("Uploading")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_cloud_upload)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setProgress(progressMax,0,false)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setOngoing(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "EDS Order Booking",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notification.setChannelId(channelId);
        }

        notificationManager.notify(notificationId,notification.build());
    }

    /**
     * Update the progress of Survey Saving on server
     * @param count
     */
    public void updateNotificationProgress(float count,int notificationId){
        if(count<=0)
            return;
        int progress = (int) (progressMax * count);
        if(notification!=null) {
            notification.setProgress(progressMax, progressMax-progress, false);
            notificationManager.notify(notificationId,notification.build());
        }
    }

    public void finishUpload(int notificationId){
        if(notification!=null)
            notification.setProgress(0,0,false);
        notification.setContentText("Uploaded Successfully");
        notification.setOngoing(false);
        notificationManager.notify(notificationId,notification.build());
    }

    public void cancelUpload(int notificationId,Long outletId,int status){
        if(outletId==null) return;
        if(notification!=null )
            notification.setProgress(0,0,false);
        notification.setContentText("Sorry! Data could not be saved");
        notification.setOngoing(false);

        Intent resultIntent = new Intent(context, status >= Constant.STATUS_PENDING_TO_SYNC ?CashMemoActivity.class: OutletDetailActivity.class);
        resultIntent.putExtra("OutletId",outletId);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId,notification.build());
    }


}
