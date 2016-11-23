package cmsc434.parkdotproto1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by hojinskang on 11/22/16.
 */

public class ParkingTicket extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_ticket);
    }

    public void onParkingTicketNextButtonClick(View v) {
        Intent intent = new Intent(ParkingTicket.this, Confirmation.class);
        startActivity(intent);
    }
}
