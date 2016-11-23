package cmsc434.parkdotproto1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import static android.view.View.INVISIBLE;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ConfirmationActivity extends Activity {
    private TextView expirationTime, notifyTime, notifyType, notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        expirationTime = (TextView) findViewById(R.id.exp_time_text);
        int expirationHour = bundle.getInt("expirationHour");
        String meridiem = "AM";
        if (expirationHour > 12) {
            expirationHour = expirationHour - 12;
            meridiem = "PM";
        }
        int expirationMinute = bundle.getInt("expirationMinute");
        expirationTime.setText(Integer.toString(expirationHour) + ":" + Integer.toString(expirationMinute) + " " + meridiem);

        notifyTime = (TextView) findViewById(R.id.notify_time_text);
        notifyType = (TextView) findViewById(R.id.notify_type_text);
        notes = (TextView) findViewById(R.id.note_text);

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
            }

            if (!bundle.getString("notes").isEmpty()) {
                notes.setText(bundle.getString("notes"));
            }
        }
    }
}
