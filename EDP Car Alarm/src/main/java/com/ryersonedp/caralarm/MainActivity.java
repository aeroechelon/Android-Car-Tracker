package com.ryersonedp.caralarm;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.ryersonedp.caralarm.util.DialogManager;
import com.ryersonedp.caralarm.util.QuickToast;
import com.ryersonedp.caralarm.util.SystemUiHider;
import com.parse.Parse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.ryersonedp.caralarm.R.string.parse_applicationID;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements GooglePlayServicesClient.OnConnectionFailedListener, GooglePlayServicesClient.ConnectionCallbacks, GoogleMap.CancelableCallback, View.OnClickListener{


    private static final String TAG = "MainActivity.java";

    private static final float CITY_ZOOM = 10.0f;
    private static final float REGION_ZOOM = 15.8f;
    private static final float STREET_ZOOM = 18.0f;

    private static final float RYERSON_LAT = 43.657689f;
    private static final float RYERSON_LNG = -79.378233f;

    private static final LatLng ryersonLatitudeAndLongitude = new LatLng(RYERSON_LAT, RYERSON_LNG);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd HH:mm:ss");

    private static final int CAMERA_ANIMATE_DURATION_SLOW = 2000;

    private GoogleMap mMap;
    private Location mLocation;
    private LatLng mCurrentLatLng;
    private LocationClient mLocationClient;

    private ParseObject mCarStatus;

    private static final Animation feedBackAnimation = new AlphaAnimation(0.4f, 1.0f);


    @Override
    public void onConnected(Bundle bundle) {

        LatLng currentLatitudeAndLongitude;

        // Get the last mLocation from Google Play Services
        mLocation = mLocationClient.getLastLocation();
        currentLatitudeAndLongitude = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        // Animate the camera to zoom at the indicated position by LatLng
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatitudeAndLongitude, REGION_ZOOM), CAMERA_ANIMATE_DURATION_SLOW, this);
        mMap.addMarker(new MarkerOptions()
                .position(currentLatitudeAndLongitude)
                .title(getResources().getString(R.string.your_location))
                .snippet("Last update: " + dateFormat.format(new Date()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                .showInfoWindow();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.activity_main_menu_about:

                DialogManager alert = new DialogManager();
                // Internet Connection is not present
                alert.showAlertDialog(this, getResources().getString(R.string.activity_main_about_title),getResources().getString(R.string.about), getResources().getString(R.string.activity_main_about_confirmation), false);
                break;
        }

        return true;
    }

    public void onClickAlarmButton(View view){
        this.onClick(view);

        ParseCloud.callFunctionInBackground("alarm", new HashMap<String, Object>(), new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e != null) {
                    QuickToast.makeToast(MainActivity.this, "Sending silence alarm request ...");
                }
            }
        });
    }

    public void onClickFindMyLocationButton(View view){
        this.onClick(view);

        mLocation = mLocationClient.getLastLocation();
        mCurrentLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());

        // Animate the camera to zoom at the indicated position by LatLng
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, REGION_ZOOM), CAMERA_ANIMATE_DURATION_SLOW, this);
        mMap.addMarker(new MarkerOptions()
                .position(mCurrentLatLng)
                .title(getResources().getString(R.string.your_location))
                .snippet("Last updated " + dateFormat.format(new Date()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                .showInfoWindow();
    }

    public void onClickFindCarLocationButton(View view){
        this.onClick(view);

        GetCallback<ParseObject> result;


        // Querying the Status class in the background, sorting by ascending and retrieving the first result
        // This will essentially retrieve the most recent result from Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Status");
        query.orderByDescending("updatedAt");
        query.getFirstInBackground(result = new GetCallback<ParseObject>() {

            public void done(ParseObject status, ParseException e) {
                if (status == null) {
                    Log.wtf(TAG, "Parse object is null");
                    QuickToast.makeToast(MainActivity.this, getResources().getString(R.string.error_no_internet_connection));

                } else {
                    Log.wtf(TAG, "Retrieved the object.");
                    String latitude = status.getString("Latitude");
                    String longitude = status.getString("Longitude");
                    Date date = status.getUpdatedAt();

                    LatLng carLatitudeAndLongitude;

                    try{
                        carLatitudeAndLongitude = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    }catch (NullPointerException npe){
                        npe.printStackTrace();

                        carLatitudeAndLongitude = ryersonLatitudeAndLongitude;
                    }

                    // Animate the camera to zoom at the indicated position by LatLng
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ryersonLatitudeAndLongitude, REGION_ZOOM), CAMERA_ANIMATE_DURATION_SLOW, MainActivity.this);

                    try {

                        mMap.addMarker(new MarkerOptions()
                                .position(carLatitudeAndLongitude)
                                .title("Car's current location")
                                .snippet("Last update " + dateFormat.format(date))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                                .showInfoWindow();
                    }catch (NullPointerException dateThrownException){
                        mMap.addMarker(new MarkerOptions()
                                .position(carLatitudeAndLongitude)
                                .title("Car last seen here")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                                .showInfoWindow();
                    }
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        // Initializing Parse
        try{
            Parse.initialize(this, getResources().getString(parse_applicationID), getResources().getString(R.string.parse_clientID));
        }catch (NetworkOnMainThreadException e){
            // upon resume or recent Parse.initialize exceptions this exception is thrown
            // Temporarily supressing it right now
            Log.e(TAG, "Parse initialize failed during onResume()");
            e.printStackTrace();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieving an instance of Google Map
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.activity_main_google_map)).getMap();

        // Disables zoom in / out buttons at the bottom of the map
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setIndoorEnabled(true);

        // Initiating a mLocationClient reference using GooglePlayServicesClient
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();

        // Initializing Parse
        try{
            Parse.initialize(this, getResources().getString(parse_applicationID), getResources().getString(R.string.parse_clientID));
        }catch (NetworkOnMainThreadException e){
            // upon resume or recent Parse.initialize exceptions this exception is thrown
            // Temporarily supressing it right now
            e.printStackTrace();

        }

        // Obtaining Parse object reference
        mCarStatus = new ParseObject("Status");

        // Setting default callback
        PushService.setDefaultPushCallback(this, MainActivity.class);

        // When users indicate they are Giants fans, we subscribe them to that channel.
        PushService.subscribe(this, "Giants", MainActivity.class);



        // Setting up analytics
        ParseAnalytics.trackAppOpened(getIntent());

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public void onClick(View view) {
        Log.wtf(TAG, "View was clicked!");
        feedBackAnimation.setDuration(200);
        view.startAnimation(feedBackAnimation);

    }
}
