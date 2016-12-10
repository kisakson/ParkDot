package cmsc434.parkdotproto1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static android.view.View.INVISIBLE;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ConfirmationActivity extends Activity {
    private TextView expirationTime, notifyTime, notifyType, notes;
    public static final int MAP_ACTIVITY_REQUEST_CODE = 203;
    private long expMili;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        expirationTime = (TextView) findViewById(R.id.exp_time_text);
        int expirationHour = bundle.getInt("expirationHour");
        int expirationMinute = bundle.getInt("expirationMinute");
        String meridiem = "AM";
        expMili = (expirationHour * 60 + expirationMinute) * 60000L;

        if (expirationHour > 12) {
            expirationHour = expirationHour - 12;
            meridiem = "PM";
        }
        String hour = Integer.toString(expirationHour);
        String minute = Integer.toString(expirationMinute);

        // save expiration information for pop-up notification
        SharedPreferences sharedpreferences = this.getSharedPreferences("expTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("expHour", hour);
        editor.putString("expMinute", minute);
        editor.putString("meridiem", meridiem);

        // format time
        if (expirationHour < 10) {
            hour = "0" + hour;
        }
        if (expirationMinute < 10) {
            minute = "0" + minute;
        }
        expirationTime.setText(hour + ":" + minute + " " + meridiem);

        notifyTime = (TextView) findViewById(R.id.notify_time_text);
        notifyType = (TextView) findViewById(R.id.notify_type_text);
        notes = (TextView) findViewById(R.id.note_text);

        // skip notification section if user does not want to be notified
        if (bundle.getString("notified").equals("no")) {
            notifyTime.setText("None");

            TextView notifyLabel = (TextView) findViewById(R.id.notify_me_with_label);
            notifyLabel.setVisibility(INVISIBLE);

            notifyType.setVisibility(INVISIBLE);

            notes.setVisibility(INVISIBLE);

        } else {
            int notifyMinute = bundle.getInt("notifyTime") * 5 + 5;
            notifyTime.setText(Integer.toString(notifyMinute) + " minutes prior");

            if (bundle.getInt("notifyType") == 0) {
                notifyType.setText("IN APP ONLY");
            } else {
                notifyType.setText("IN APP and with PUSH NOTIFICATION");
                long notifyMili = notifyMinute * 60000L;
                long timeMili = expMili - notifyMili;
                scheduleNotification(getNotification("Go get your car!"), timeMili);
            }

            if (!bundle.getString("notes").isEmpty()) {
                notes.setText(bundle.getString("notes"));
            }
        }

        editor.commit();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onConfirmationConfirmClick(View v) {
        Intent resultIntent = getIntent();
        resultIntent.putExtra("result", true);
        resultIntent.putExtra("notes", notes.getText());
        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }

    private void scheduleNotification(Notification notification, long notifyTime) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d("NotificationSchedule", String.valueOf(notifyTime));
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, notifyTime, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Parking Alert");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.orange_carpng);
        return builder.build();
    }
}
