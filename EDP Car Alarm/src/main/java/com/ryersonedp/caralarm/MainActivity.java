package com.ryersonedp.caralarm;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.ryersonedp.caralarm.util.SystemUiHider;
import com.parse.Parse;
import com.parse.ParseAnalytics;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements GooglePlayServicesClient.OnConnectionFailedListener, GooglePlayServicesClient.ConnectionCallbacks, GoogleMap.CancelableCallback{


    private static final String TAG = "MainActivity.java";

    private static final float CITY_ZOOM = 10.0f;
    private static final float REGION_ZOOM = 15.0f;
    private static final float STREET_ZOOM = 18.0f;

    private static final float RYERSON_LAT = 43.657689f;
    private static final float RYERSON_LNG = -79.378233f;

    private static final int CAMERA_ANIMATE_DURATION_SLOW = 2000;

    private GoogleMap map;
    private Location location;
    private LocationClient locationClient;


    @Override
    public void onConnected(Bundle bundle) {

        LatLng currentLatitudeAndLongitude;

        // Get the last location from Google Play Services
        location = locationClient.getLastLocation();
        currentLatitudeAndLongitude = new LatLng(location.getLatitude(), location.getLongitude());

        // Animate the camera to zoom at the indicated position by LatLng
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatitudeAndLongitude, STREET_ZOOM), CAMERA_ANIMATE_DURATION_SLOW, this);
        map.addMarker(new MarkerOptions()
                .position(currentLatitudeAndLongitude)
                .title(getResources().getString(R.string.your_location)));

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // Used when animation zoom is interrupted
    @Override
    public void onFinish() {
        // do nothing on finish
    }

    @Override
    public void onCancel() {
        // do nothing on cancel
    }

    public void onClickFindMyLocationButton(View view){

        location = locationClient.getLastLocation();

        // Animate the camera to zoom at the indicated position by LatLng
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), STREET_ZOOM), CAMERA_ANIMATE_DURATION_SLOW, this);
    }

    public void onClickFindCarLocationButton(View view){

        LatLng ryersonLatitudeAndLongitude = new LatLng(RYERSON_LAT, RYERSON_LNG);

        // Animate the camera to zoom at the indicated position by LatLng
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ryersonLatitudeAndLongitude, STREET_ZOOM), CAMERA_ANIMATE_DURATION_SLOW, this);
        map.addMarker(new MarkerOptions()
                .position(ryersonLatitudeAndLongitude)
                .title(getResources().getString(R.string.car_location)));
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Obtaining reference upon resume
        Parse.initialize(this, getResources().getString(R.string.parse_applicationID), getResources().getString(R.string.parse_clientID));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieving an instance of Google Map
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.activity_main_google_map)).getMap();

        // Disables zoom in / out buttons at the bottom of the map
        map.getUiSettings().setZoomControlsEnabled(false);

        // Initiating a locationClient reference using GooglePlayServicesClient
        locationClient= new LocationClient(this, this, this);
        locationClient.connect();

        // Initializing Parse
        Parse.initialize(this, getResources().getString(R.string.parse_applicationID), getResources().getString(R.string.parse_clientID));

        // Obtaining a Parse reference
        ParseObject parse = new ParseObject("TestObject");
        parse.put("foo", "bar");
        parse.saveInBackground();

        }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
