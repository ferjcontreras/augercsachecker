package com.example.fcontreras.augercsachecker;

import android.app.NotificationChannel;
//import android.app.NotificationManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFBReceivingService extends FirebaseMessagingService {

    private static final String TAG = "MTOKEN";
    public static final String CHANNEL_ID = "channel1";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("Mensage", "Mensage recibido "+remoteMessage.getData().get("type"));
        showNotification(remoteMessage.getData().get("type"), remoteMessage.getData().get("message"), remoteMessage.getData().get("status"), remoteMessage.getData().get("title"), remoteMessage.getData().get("ID"), remoteMessage.getData().get("parent"), remoteMessage.getData().get("longtext"), remoteMessage.getData().get("p"));
    }

    private void showNotification(String type, String message, String status, String title, String ID, String parent, String longText, String p) {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        Intent i = new Intent(this, MainActivity.class);
        if (p != null) i.putExtra("PARENT", Integer.parseInt(p));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder;
        Bitmap logoAuger = BitmapFactory.decodeResource(getResources(), R.mipmap.logo_auger);

        NotificationManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            manager = getSystemService(NotificationManager.class);
        }
        else {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SharedPreferences preferences = getBaseContext().getSharedPreferences("general", MODE_PRIVATE);


        // Patch for Android Oreo devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Auger CSA Checker", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications from Server");
            manager.createNotificationChannel(channel);
        }



        switch (type) {
            case "Valid":
            case "System":
                builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setAutoCancel(true)
                        .setContentTitle("Auger CSA Check - " + title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.logo_auger_transparente)
                        .setContentIntent(pendingIntent)
                        .setSound(soundUri)
                        .setLargeIcon(logoAuger)
                        .setStyle( new NotificationCompat.BigTextStyle().bigText(longText).setSummaryText(message))
                        .setVibrate(new long[]{1000, 1000, 1000});
                manager.notify(12000, builder.build());



                Log.i("Mensage", "Debug - Type: "+type);

                if (type.equals("Valid")) {
                    Log.i("Mensage", "Mensage de validación de usuario");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("Valid", true);
                    editor.putString("RW", status);
                    editor.apply();
                }

                break;

            case "Monit":
                // Getting data from config files

                SharedPreferences monitor = getBaseContext().getSharedPreferences("monitor", MODE_PRIVATE);
                boolean showNotifications = preferences.getBoolean("showNot", true);
                boolean Sounds = preferences.getBoolean("Sounds", true);
                boolean Vibrate = preferences.getBoolean("Vibrate", true);

                if (showNotifications && monitor.getBoolean("item"+parent, false) && !(monitor.getBoolean("silent"+ID, false))) {

                    //Define sound URI
                    builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setAutoCancel(true)
                            .setContentTitle("Auger CSA Check - " + title)
                            .setContentText(status + " " +message)
                            .setSmallIcon(R.drawable.logo_auger_transparente)
                            .setLargeIcon(logoAuger)
                            .setStyle( new NotificationCompat.BigTextStyle().bigText(longText).setSummaryText(message))
                            .setContentIntent(pendingIntent);


                    if (Sounds) builder.setSound(soundUri);
                    if (Vibrate) builder.setVibrate(new long[]{1000, 1000, 1000});
                    //manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(Integer.parseInt(ID), builder.build());
                    break;
            }
        }
    }
}
