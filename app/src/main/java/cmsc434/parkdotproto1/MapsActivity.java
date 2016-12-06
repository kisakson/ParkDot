package cmsc434.parkdotproto1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /*  Help with creating and importing the Google Maps API comes from online API documentation
        and these Github tutorials:
        https://github.com/googlemaps/android-samples/blob/master/tutorials/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlaces.java
        https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/BasicMapDemoActivity.java
     */
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private GoogleApiClient mGoogleApiClient; // Used for getting device location instead of Location Manager
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mCurrentLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static final int ADD_PARKING_SPOT_REQUEST_CODE = 103;


    Button addParkingSpotButton;
    Button getDirectionsButton;
    Button clearMarkerButton;

    // Create a Marker object that will store vehicle location
    private Marker mSavedLocation;
    private boolean mRunOnce = false;

    // Create SharedPreferences to store Marker location.
    // Information gathered from: https://developer.android.com/training/basics/data-storage/shared-preferences.html
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Build the API client
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        addParkingSpotButton = (Button) findViewById(R.id.add_parking_spot_button);
        getDirectionsButton = (Button) findViewById(R.id.get_directions_button);
        clearMarkerButton = (Button) findViewById(R.id.clear_marker_button);

        // Initialize the SharedPreferences objects
        mSharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();

    }

    // Function called when map is ready after onCreate
    @Override
    public void onMapReady(GoogleMap inMap) {
        mMap = inMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        updateLocationUI();
        // Add sample marker
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Update map location
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) { // cameraPosition null
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        } else { // cameraPosition and cameraLocation null
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        // Get saved marker location, if stored
        // This should only run once.
        if (mMap != null && !mRunOnce) {
            String savedLoc = mSharedPref.getString(getString(R.string.saved_marker_location), "");
            String savedNotes = mSharedPref.getString(getString(R.string.saved_marker_notes), "No notes");

            if (!savedLoc.equals("")) {
                LatLng loc = new LatLng(Double.parseDouble(savedLoc.split(",")[0]),
                        Double.parseDouble(savedLoc.split(",")[1]));

                mSavedLocation = mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .title("Saved Parking Location")
                        .snippet(loc.toString())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car)));

                addParkingSpotButton.setVisibility(View.INVISIBLE);
                getDirectionsButton.setVisibility(View.VISIBLE);
                clearMarkerButton.setVisibility(View.VISIBLE);
            }

            if (!savedNotes.equals("No notes")) {
                TextView notes_view = (TextView) findViewById(R.id.note_text);
                notes_view.setText(savedNotes);
                notes_view.setVisibility(View.VISIBLE);

                mEditor.putString(getString(R.string.saved_marker_notes), savedNotes);
            }

            mRunOnce = true;
        }
    }

    // Get device lodation after app is resumed
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
    }

    // Stop location updates when app is paused to save battery
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    // Saves an instance state when the app is paused
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
        }
    }

    // Gets device's current location and builds map
    @Override
    public void onConnected(Bundle connectionHint) {
        getDeviceLocation();
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Handles failure to connect to Google Play services
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    // Handles case when Google Play connection is suspended
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    // Called when location is changed
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        createLocationRequest();
    }

    // Creates a location request
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets how fast the application receives location updates
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Gets location of device
    private void getDeviceLocation() {
        // Request permissions to get location data
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // Request the latest location data. Sometimes it may be null so no changes would be made.
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    // Updates boolean saying if location permission is granted by user.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    // Updates UI map based on whether user gave location permission or not.
    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
        }
    }

    public void onAddParkingSpotClick(View v) {
        Intent intent = new Intent(MapsActivity.this, ExpirationTimeActivity.class);
        startActivityForResult(intent, ADD_PARKING_SPOT_REQUEST_CODE);
    }

    // Opens Google Maps app to give walking navigation to parking location from current position
    // Information given from Google documentation
    // https://developers.google.com/maps/documentation/android-api/intents
    public void onGetDirectionsClick(View v) {
        LatLng markerPos = mSavedLocation.getPosition();
        String link = "google.navigation:q=" + markerPos.latitude + "," + markerPos.longitude + "&mode=w";
        Uri gmmIntentUri = Uri.parse(link);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    // Give an alert to the user, asking if they want to clear their parking information
    // If yes, parking information is cleared.
    public void onClearMarkerClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.clear_alert_title)
                .setMessage(R.string.clear_alert_message)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton(R.string.clear_okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addParkingSpotButton.setVisibility(View.VISIBLE);
                getDirectionsButton.setVisibility(View.INVISIBLE);
                clearMarkerButton.setVisibility(View.INVISIBLE);
                mEditor.clear();
                mEditor.commit();
                mSavedLocation.remove();

                TextView notes_view = (TextView) findViewById(R.id.note_text);
                notes_view.setText("");
                notes_view.setVisibility(View.INVISIBLE);
            }
        });

        builder.setNegativeButton(R.string.clear_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });

        AlertDialog clearAlert = builder.create();
        clearAlert.show();
    }

    // Get the result from adding a parking spot
    // Ensures that a parking spot was successfully added to the map.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (ADD_PARKING_SPOT_REQUEST_CODE): {
                if (resultCode == RESULT_OK) {
                    addParkingSpotButton.setVisibility(View.INVISIBLE);
                    getDirectionsButton.setVisibility(View.VISIBLE);
                    clearMarkerButton.setVisibility(View.VISIBLE);

                    LatLng loc = new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude());

                    String locString = loc.latitude + "," + loc.longitude;

                    mEditor.putString(getString(R.string.saved_marker_location), locString);

                    mSavedLocation = mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title("Saved Parking Location")
                            .snippet(loc.toString())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car)));

                    // Rayna's code here
                    Bundle bundle = data.getExtras();
                    String notes = bundle.getString("notes");
                    if (!notes.equals("No notes")) {
                        TextView notes_view = (TextView) findViewById(R.id.note_text);
                        notes_view.setText(notes);
                        notes_view.setVisibility(View.VISIBLE);

                        mEditor.putString(getString(R.string.saved_marker_notes), notes);
                    }
                    mEditor.commit();
                }
            }
        }
    }
}
