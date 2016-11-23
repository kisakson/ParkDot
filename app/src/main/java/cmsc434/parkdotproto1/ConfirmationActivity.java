package cmsc434.parkdotproto1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ConfirmationActivity extends Activity {
    TextView expirationTime, notifyTime;
    ImageView parkingTicketImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        expirationTime = (TextView) findViewById(R.id.exp_time_text);
        int expirationHour = bundle.getInt("expirationHour");
        int expirationMinute = bundle.getInt("expirationMinute");
        expirationTime.setText(Integer.toString(expirationHour) + ":" + Integer.toString(expirationMinute));

        notifyTime = (TextView) findViewById(R.id.notify_time_text);
        int notifyMinute = bundle.getInt("notifyTime") * 5 + 5;
        notifyTime.setText(Integer.toString(notifyMinute) + " minutes prior");

    }
}
