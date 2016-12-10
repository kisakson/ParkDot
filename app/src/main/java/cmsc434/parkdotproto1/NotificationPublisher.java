package cmsc434.parkdotproto1;

/**
 * Created by hojinskang on 12/9/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class NotificationPublisher extends BroadcastReceiver {

    public static final int NOTIFICATION_REQUST_ID = 0;
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.orange_carpng)
                .setContentTitle("Parking Alert")
                .setContentText(intent.getStringExtra(NOTIFICATION));

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
