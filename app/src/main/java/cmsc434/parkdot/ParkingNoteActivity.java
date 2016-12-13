package cmsc434.parkdot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * ParkingNoteActivity implements the note text box. This class passes the input notes to the
 * ConfirmationActivity screen where the user sees all of their selections before hitting "Confirm".
 * This class also passes the input text to the ConfirmationActivity class as a bundle extra,
 * allowing notes to be displayed over the MapActivity screen.
 * The layout file for this is in res/layout/parking_note.xml.
 *
 * Created by hojinskang on 11/23/16.
 */
public class ParkingNoteActivity extends Activity {
    private EditText notes;
    private NumberPicker notifyType;

    private static final int PARKING_NOTE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_note);

        notifyType = (NumberPicker) findViewById(R.id.notify_type);
        notifyType.setMinValue(0);
        notifyType.setMaxValue(1);
        notifyType.setDisplayedValues( new String[] { "NO" , "YES" } );

        notes = (EditText) findViewById(R.id.notes);
    }

    /**
     * Send notification and notes information to confirmation page
     * @param v
     */
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
            case(PARKING_NOTE_REQUEST_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    setResult(resultCode, data);
                    finish();
                }
            }
        }
    }

    /* Credit goes to
    http://stackoverflow.com/questions/7092961/
    edittext-maxlines-not-working-user-can-still-input-more-lines-than-set */
    public void onEditNotesText(View view) {
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String notes = ((EditText) v).getText().toString();
                    int editTextRowCount = notes.split("\\n").length;

                    // Rayna's code here
                    if (editTextRowCount >= 3) {
                        int lastBreakIndex = notes.lastIndexOf("\n");
                        String limitedNotes = notes.substring(0, lastBreakIndex);
                        ((EditText) v).setText("");
                        ((EditText) v).append(limitedNotes);
                    }
                }
                return false;
            }
        });

    }
}
