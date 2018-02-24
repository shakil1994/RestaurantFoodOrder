package com.example.shakil.restaurantfoodorder.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.shakil.restaurantfoodorder.R;

/**
 * Created by shakil on 1/26/2018.
 */

public class NotificationHelper extends ContextWrapper {

    private static final String CHANEL_ID = "com.example.shakil.restaurantfoodorder.FoodApp";
    private static final String CHANEL_NAME = "Food Order";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        //Only working this function if API is 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getFoodAppChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri){
        return new Notification.Builder(getApplicationContext(), CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

}
