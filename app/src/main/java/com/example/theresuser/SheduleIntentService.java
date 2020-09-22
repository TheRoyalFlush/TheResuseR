package com.example.theresuser;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//Scheduling the reminder services
    public class SheduleIntentService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"noti");
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle("Bin collection reminder.");
        builder.setContentText("Tomorrow is your bin collection. Please keep your bins on the Kerb.");
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(001,builder.build());
    }



    public void createNotificationChannel(Context context){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            CharSequence name = "PNoti";
            NotificationChannel notificationChannel = new NotificationChannel("noti",name,NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Desc");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}
