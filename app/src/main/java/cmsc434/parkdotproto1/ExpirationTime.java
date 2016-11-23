package cmsc434.parkdotproto1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ExpirationTime extends Activity {
    private TimePicker expirationTime;
    private NumberPicker notifyTime, notifyMinutes;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expiration_time);

        expirationTime = (TimePicker) findViewById(R.id.exp_time);
        notifyTime = (NumberPicker) findViewById(R.id.notify_time);
        notifyMinutes = (NumberPicker) findViewById(R.id.notify_minutes);
        nextButton = (Button) findViewById(R.id.next_button);

        /* Initialize notification times */
        notifyTime.setMinValue(0);
        notifyTime.setMaxValue(11);
        notifyTime.setDisplayedValues( new String[] { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60" } );
        notifyTime.setValue(5);

        notifyMinutes.setMinValue(0);
        notifyMinutes.setMaxValue(0);
        notifyMinutes.setDisplayedValues( new String[] { "minutes" } );
    }

    public void onExpirationTimeNextButtonClick(View v) {
        Intent intent = new Intent(ExpirationTime.this, ParkingTicket.class);
        startActivity(intent);
    }
}
