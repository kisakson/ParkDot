package cmsc434.parkdotproto1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ParkingTicketActivity extends Activity {
    ImageView parkingTicketImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_ticket);

        parkingTicketImg = (ImageView) findViewById(R.id.parking_ticket_img);
    }

    public void onParkingTicketNextButtonClick(View v) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        Intent intent2 = new Intent(ParkingTicketActivity.this, ConfirmationActivity.class);
        finish();
        intent2.putExtras(bundle);
        startActivity(intent2);
    }
}
