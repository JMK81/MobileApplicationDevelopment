package com.example.mobileapplicationdevelopment;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverHelper extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public String TAG = "ReceiverHelper";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //need or set data probably not necessary for this project
            Log.d(TAG, "boot completed");
        } else {*/
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = intent.getParcelableExtra(NOTIFICATION);
            int id = intent.getIntExtra(NOTIFICATION_ID, 0);
            notificationManager.notify(id, notification);
            Log.d(TAG, "this is from Receiver Helper should not be printing");
        //}
    }
}