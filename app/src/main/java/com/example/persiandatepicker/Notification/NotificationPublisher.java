package com.example.persiandatepicker.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.persiandatepicker.MainActivity;
import com.example.persiandatepicker.R;

/**
 * this class represents notification for app when you set alarm
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        String txt = intent.getStringExtra("txt");
        String time = intent.getStringExtra("time");
        int noteId = intent.getIntExtra("id" , 0);
        Intent activityIntent = new Intent(context , MainActivity.class); // if u click on notification this activity will appear
        PendingIntent pendingIntent = PendingIntent.getActivity(context , 0 , activityIntent , PendingIntent.FLAG_ONE_SHOT);

        String channelId = "channel_id";
        CharSequence channelName = "channel_name";
        String description = "description";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(channelId ,channelName , NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_bell)
                .setTicker("your calendar reminder")
                .setAutoCancel(true)
                .setContentText(txt)
                .setContentTitle(time)
                .setDeleteIntent(pendingIntent)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(noteId , notification);
    }
}