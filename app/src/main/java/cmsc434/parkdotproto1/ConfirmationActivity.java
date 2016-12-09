package cmsc434.parkdotproto1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static android.view.View.INVISIBLE;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ConfirmationActivity extends Activity {
    private TextView expirationTime, notifyTime, notifyType, notes;
    public static final int MAP_ACTIVITY_REQUEST_CODE = 203;

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
                editor.putLong("expMili", 0L);
            } else {
                notifyType.setText("IN APP and with PUSH NOTIFICATION");
                long expirationMili = notifyMinute * 60000L;
                editor.putLong("expMili", expirationMili);
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
}
