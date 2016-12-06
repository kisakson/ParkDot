package cmsc434.parkdotproto1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

/**
 * Created by hojinskang on 11/23/16.
 */

public class ParkingNoteActivity extends Activity {
    private EditText notes;
    private NumberPicker notifyType;

    final private int PARKING_NOTE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_note);

        notifyType = (NumberPicker) findViewById(R.id.notify_type);
        notifyType.setMinValue(0);
        notifyType.setMaxValue(1);
        notifyType.setDisplayedValues( new String[] { "IN APP ONLY" , "IN APP AND WITH PUSH NOTIFICATION" } );

        notes = (EditText) findViewById(R.id.notes);
    }

    public void onParkingNoteNextButtonClick(View v) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bundle.putInt("notifyType", notifyType.getValue());
        bundle.putString("notes", notes.getText().toString());

        Intent intent2 = new Intent(ParkingNoteActivity.this, ConfirmationActivity.class);
        //finish();
        intent2.putExtras(bundle);
        startActivityForResult(intent2, PARKING_NOTE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Testing Parking Note");
        System.out.println("Parking Note: " + requestCode);
        System.out.println("Parking Note: " + resultCode);

        switch(requestCode) {
            case(PARKING_NOTE_REQUEST_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    setResult(resultCode, data);
                    finish();
                }
            }
        }
    }
}
