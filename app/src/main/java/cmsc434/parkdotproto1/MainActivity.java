package cmsc434.parkdotproto1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button addParkingSpotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addParkingSpotButton = (Button) findViewById(R.id.add_parking_spot_button);
    }

    public void onAddParkingSpotClick(View v) {
        Intent intent = new Intent(MainActivity.this, ExpirationTimeActivity.class);
        startActivity(intent);
    }
}
