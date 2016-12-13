package cmsc434.parkdot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TimePicker;

/**
 * ExpirationTimeActivity allows the user to input the type, frequency, and event of their parking
 * reminder. The options for notification times are intervals of 5 minutes up to an hour before the
 * parking ticket expires. There are also two options as to whether the user wants to be notified
 * at all, either via in-app notifications or push notifications AND in-app notifications.
 * The layout for this class is in layout/expiration_time.xml.
 *
 * Created by hojinskang on 11/22/16.
 */
public class ExpirationTimeActivity extends Activity {
    private TimePicker expirationTime;
    private NumberPicker notifyTime, notifyMinutes;
    private CheckBox expiration, notified;

    private static final int EXPIRATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expiration_time);

        expirationTime = (TimePicker) findViewById(R.id.exp_time);
        expiration = (CheckBox) findViewById(R.id.exp_checkbox);
        notifyTime = (NumberPicker) findViewById(R.id.notify_time);
        notifyMinutes = (NumberPicker) findViewById(R.id.notify_minutes);
        notified = (CheckBox) findViewById(R.id.notify_checkbox);

        /* Initialize notification times */
        notifyTime.setMinValue(0);
        notifyTime.setMaxValue(11);
        notifyTime.setDisplayedValues( new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" } );
        notifyTime.setValue(5);

        /* Limit notification time to minutes */
        notifyMinutes.setMinValue(0);
        notifyMinutes.setMaxValue(0);
        notifyMinutes.setDisplayedValues( new String[] { "minutes prior" } );

        // If no expiration time, then no notification
        expiration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    notified.setChecked(true);
                    notified.setEnabled(false);
                }
            }
        });
    }

    /**
     * Save expiration and notification information
     * @param v
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void onExpirationTimeNextButtonClick(View v) {
        Intent intent;
        if (expiration.isChecked() || notified.isChecked()) { // skip to confirmation if user does not want to be notified
            intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), ParkingNoteActivity.class);
        }

        // save expiration
        Bundle bundle = new Bundle();
        bundle.putInt("expirationHour", expirationTime.getHour());
        bundle.putInt("expirationMinute", expirationTime.getMinute());
        bundle.putInt("notifyTime", notifyTime.getValue());
        if (expiration.isChecked()) {
            bundle.putString("expiration", "no");
        } else {
            bundle.putString("expiration", "yes");
        }
        if (notified.isChecked()) {
            bundle.putString("notified", "no");
        } else {
            bundle.putString("notified", "yes");
        }
        intent.putExtras(bundle);

        startActivityForResult(intent, EXPIRATION_REQUEST_CODE);
    }

    /**
     * Send request code
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case(EXPIRATION_REQUEST_CODE) : {
                if (resultCode == RESULT_OK) {
                    setResult(resultCode, data);
                    finish();
                }
            }
        }
    }
}
