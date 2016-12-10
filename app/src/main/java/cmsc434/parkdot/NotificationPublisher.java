package cmsc434.parkdot;

/**
 * Created by hojinskang on 12/9/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationPublisher extends BroadcastReceiver {

    public static final int NOTIFICATION_REQUST_ID = 0;
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {

        // Help setting vibrate, lights, and sound from a Stack Overflow posts:
        // http://stackoverflow.com/questions/18253482/vibrate-and-sound-defaults-on-notification
        // http://stackoverflow.com/questions/15809399/android-notification-sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.orange_carpng)
                .setContentTitle("Parking Alert")
                .setContentText(intent.getStringExtra(NOTIFICATION))
                .setVibrate(new long[] { 500, 500, 500, 500, 500 })
                .setLights(Color.CYAN, 1000, 1000)
                .setSound(alarmSound)
        ;

        Intent resultIntent = new Intent(context, MapsActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MapsActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(NOTIFICATION_REQUST_ID, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int id = intent.getIntExtra(NOTIFICATION_ID, NOTIFICATION_REQUST_ID);
        notificationManager.notify(id, builder.build());
    }
}
