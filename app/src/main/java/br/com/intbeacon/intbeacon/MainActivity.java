package br.com.intbeacon.intbeacon;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

public class MainActivity<T> extends AppCompatActivity {

    long time = 10000;
    Class<T> clazz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clazz = (Class<T>) FirstBeaconActivity.class;
        notification(clazz, 001, "Vai curintia");
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clazz = (Class<T>) SecondBeaconActivity.class;
        notification(clazz, 002, "Vixxxiii");
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clazz = (Class<T>) ThirdyBeaconActivity.class;
        notification(clazz, 003, "Vai curintia");
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void notification(Class<T> activity, int id, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("IntBeacon"+ id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(text);

        Intent intent = new Intent(MainActivity.this, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }
}
