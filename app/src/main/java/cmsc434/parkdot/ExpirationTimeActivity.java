package cmsc434.parkdot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TimePicker;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ExpirationTimeActivity extends Activity {
    private TimePicker expirationTime;
    private NumberPicker notifyTime, notifyMinutes;
    private CheckBox notified;

    private static final int EXPIRATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expiration_time);

        expirationTime = (TimePicker) findViewById(R.id.exp_time);
        notifyTime = (NumberPicker) findViewById(R.id.notify_time);
        notifyMinutes = (NumberPicker) findViewById(R.id.notify_minutes);
        notified = (CheckBox) findViewById(R.id.notify_checkbox);

        /* Initialize notification times */
        notifyTime.setMinValue(0);
        notifyTime.setMaxValue(11);
        notifyTime.setDisplayedValues( new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" } );
        notifyTime.setValue(5);

        notifyMinutes.setMinValue(0);
        notifyMinutes.setMaxValue(0);
        notifyMinutes.setDisplayedValues( new String[] { "minutes" } );
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onExpirationTimeNextButtonClick(View v) {
        Intent intent;
        if (notified.isChecked()) {
            intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), ParkingNoteActivity.class);
        }

        Bundle bundle = new Bundle();
        bundle.putInt("expirationHour", expirationTime.getHour());
        bundle.putInt("expirationMinute", expirationTime.getMinute());
        bundle.putInt("notifyTime", notifyTime.getValue());
        if (notified.isChecked()) {
            bundle.putString("notified", "no");
        } else {
            bundle.putString("notified", "yes");
        }
        intent.putExtras(bundle);

        startActivityForResult(intent, EXPIRATION_REQUEST_CODE);
    }

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
