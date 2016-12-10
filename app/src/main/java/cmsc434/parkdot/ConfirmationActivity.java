package cmsc434.parkdot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.view.View.INVISIBLE;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ConfirmationActivity extends Activity {
    private TextView expirationTime, notifyTime, notifyType, notes;
    private long expMili;

    public static final boolean ENABLE_TESTING = false;
    private static final int TESTING_EXPIRATION_HOUR = 16;  // in 24h
    private static final int TESTING_EXPIRATION_MINUTE = 49;
    private static final int TESTING_NOTIF_MINUTE = 1;

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

        Calendar c = new GregorianCalendar();
        c.set(c.get(Calendar.YEAR), c.get(c.MONTH), c.get(c.DATE), expirationHour, expirationMinute, 0);

        if (ENABLE_TESTING) {
            c.set(c.get(Calendar.YEAR),
                    c.get(c.MONTH),
                    c.get(c.DATE),
                    TESTING_EXPIRATION_HOUR,
                    TESTING_EXPIRATION_MINUTE,
                    0);
        }

        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS a");
        Log.d("NotificationExpir", f.format(c.getTime()));

        expMili = c.getTimeInMillis();

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

            String notifText;

            if (bundle.getInt("notifyType") == 0) {
                notifyType.setText("IN APP ONLY");
            } else {
                notifyType.setText("IN APP and with PUSH NOTIFICATION");
                long notifyMili = notifyMinute * 60 * 1000;
                long timeMili = expMili - notifyMili;
                if (ENABLE_TESTING) {
                    notifyMili = TESTING_NOTIF_MINUTE * 60 * 1000;
                    timeMili = expMili - notifyMili;
                }

                if (!bundle.getString("notes").isEmpty()) {
                    notifText = bundle.getString("notes");
                } else {
                    notifText = "Go get your car!";
                }
                scheduleNotification(notifText, timeMili);
            }

            if (!bundle.getString("notes").isEmpty()) {
                notes.setText(bundle.getString("notes"));
            }
        }

        editor.apply();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onConfirmationConfirmClick(View v) {
        Intent resultIntent = getIntent();
        resultIntent.putExtra("result", true);
        resultIntent.putExtra("notes", notes.getText());

        setResult(Activity.RESULT_OK, resultIntent);

        finish();
    }

    private void scheduleNotification(String notification, long notifyTime) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                NotificationPublisher.NOTIFICATION_REQUST_ID,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS a");
        Log.d("NotificationTime", f.format(new Date(notifyTime)));

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent);
    }
}
