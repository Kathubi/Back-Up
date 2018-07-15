package com.codesndata.maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.codesndata.back_up.MainActivity;
import com.codesndata.back_up.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.codesndata.maps.backservice.BackgroundRegTask_2;
import com.codesndata.maps.models.ListLocationModel;
import com.codesndata.maps.models.LocationModel;
import com.codesndata.maps.routes.DataParser;
import com.codesndata.maps.services.ApiClient;
import com.codesndata.maps.services.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@SuppressLint("Registered")
public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    // LogCat tag
    private static final String TAG = MapsActivity.class.getSimpleName();
    public static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // Google client to interact with Google API
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private FusedLocationProviderClient locationClient;
    private Location lastLocation;
    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private TextView detailsPanel;
    private List<LocationModel> mListMarker = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate()","Maps");
        setContentView(R.layout.activity_maps);
        startLocationUpdates();
        phoneInfo();

        detailsPanel = findViewById(R.id.detailsPanel);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // Initializing
        MarkerPoints = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            //request Location
            createLocationRequest();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Already two locations
                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                MarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /*
                  For the start location, the color of marker is GREEN and
                  for the end location, the color of marker is YELLOW.
                 */
                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (MarkerPoints.size() >= 2) {
                    LatLng origin = MarkerPoints.get(0);
                    LatLng dest = MarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getUrl(origin, dest);
                    Log.d("onMapClick", url);
                    FetchUrl FetchUrl = new FetchUrl();

                    // Start downloading json data from Google Directions API
                    FetchUrl.execute(url);
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }

            }
        });
        getAllDataLocationLatLng();
    }

    /**
     * method that receives coordinates from the database
     */
    private void getAllDataLocationLatLng(){
        try {

            // Network access.
            Thread.sleep(2000);
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Loading co-ordinates from server...");
            dialog.show();

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ListLocationModel> call = apiService.getAllLocation();
            call.enqueue(new Callback<ListLocationModel>() {
                @Override
                public void onResponse(Call<ListLocationModel> call, Response<ListLocationModel> response) {
                    dialog.dismiss();
                    mListMarker = response.body().getmData();
                    initMarker();
                }

                @Override
                public void onFailure(Call<ListLocationModel> call, Throwable t) {
                    try{
                        dialog.dismiss();
                        Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.getStackTrace();
                    }

                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that iterates through the database collecting coordinate data
     */
    private void initMarker(){
        //for loop to iterate through the database
        for (int i=0; i<mListMarker.size(); i++){
            //collecting coordinates
            LatLng location = new LatLng(Double.parseDouble(mListMarker.get(i).getLatitude()), Double.parseDouble(mListMarker.get(i).getLongitude()));
            //Mapping coordinates
            //Matching markers with time
            mMap.addMarker(new MarkerOptions().position(location).title(mListMarker.get(i).getTime()));
            //Zooming the last location coordinates
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude,location.longitude), 17.0f));
        }
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    // Fetches data from url passed
    @SuppressLint("StaticFieldLeak")
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            if(result != null){
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.YELLOW);

                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                }
            }
            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","No Polylines drawn. Check Internet connection");
            }
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();

        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));

        // Assign the new location
        lastLocation = location;

            String latitude = String.valueOf(lastLocation.getLatitude());
            String longitude = String.valueOf(lastLocation.getLongitude());

            String altitude = String.valueOf(lastLocation.getAltitude());
            String speed = String.valueOf(lastLocation.getSpeed());
            String bearing = String.valueOf(lastLocation.getBearing());
            String provider = lastLocation.getProvider();
            String error_margin = String.valueOf(lastLocation.getAccuracy());
            Calendar calendar = Calendar.getInstance();
            String time = String.valueOf(calendar.getTime());
            String extra = String.valueOf(lastLocation.getExtras());
            @SuppressLint("HardwareIds") String serial = Build.SERIAL;

            Log.d(TAG, "Location Updated!");

            String function = "locationInfo";
            BackgroundRegTask_2 bgTask = new BackgroundRegTask_2();
            bgTask.execute(function, serial, latitude, longitude, altitude, speed, bearing, provider, error_margin, time, extra);

            detailsPanel.setText("Location: " + latitude + ", " + longitude + "\n " +
                    "Altitude: " + altitude + "\n Speed: " + speed + "\n Bearing: " + bearing);

    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    public void onConnected(Bundle arg0) {

        mRequestingLocationUpdates = true;

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        getFusedLocationProviderClient(this);
    }

    protected void onStart() {

        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if (locationClient != null) {
            getFusedLocationProviderClient(this);
        }
    }

    protected void onResume() {
        super.onResume();
        checkPlayServices();

        mRequestingLocationUpdates = true;

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        // Resuming the periodic location updates
        if (locationClient == null && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (locationClient != null) {
            getFusedLocationProviderClient(this);
        }
    }

    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 5000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FASTEST_INTERVAL = 100;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        int DISPLACEMENT = 20;
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        Log.d("startLocationUpdates()", "Periodic location updates started!");

        // Create the location request to start receiving updates
        @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                        displayLocation();
                    }
                },
                Looper.myLooper());

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
    }

    /**
     * Method to send device location to server
     * */

    public void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        LocationServices.getFusedLocationProviderClient(this);

        if (lastLocation != null) {
            String latitude = String.valueOf(lastLocation.getLatitude());
            String longitude = String.valueOf(lastLocation.getLongitude());

            String altitude = String.valueOf(lastLocation.getAltitude());
            String speed = String.valueOf(lastLocation.getSpeed());
            String bearing = String.valueOf(lastLocation.getBearing());
            String provider = lastLocation.getProvider();
            String error_margin = String.valueOf(lastLocation.getAccuracy());
            Calendar calendar = Calendar.getInstance();
            String time = String.valueOf(calendar.getTime());
            String extra = String.valueOf(lastLocation.getExtras());
            @SuppressLint("HardwareIds") String serial = Build.SERIAL;

            Log.d(TAG, "Location Updated!");

            String function = "locationInfo";
            BackgroundRegTask_2 bgTask = new BackgroundRegTask_2();
            bgTask.execute(function, serial, latitude, longitude, altitude, speed, bearing, provider, error_margin, time, extra);

            detailsPanel.setText("Location: " + latitude + ", " + longitude + "\n " +
                    "Altitude: " + altitude + "\n Speed: " + speed + "\n Bearing: " + bearing);
        }
    }

    private void phoneInfo(){

        String model= Build.MODEL;
        @SuppressLint("HardwareIds") String serial = Build.SERIAL;
        String device_id = Build.ID;
        String brand = Build.BRAND;
        String user = Build.USER;
        String baseband_version = String.valueOf(Build.VERSION_CODES.BASE);
        String incremental = Build.VERSION.INCREMENTAL;
        String api_version = Build.VERSION.SDK;
        String e_board = Build.BOARD;
        String kernel = Build.HOST;
        String.valueOf(Build.TIME);
        String fingerprint = Build.FINGERPRINT;
        String os_version = Build.VERSION.RELEASE;
        final String codename = Build.VERSION.CODENAME;
        String base_os = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            base_os = Build.VERSION.BASE_OS;
        }
        String securityPatch = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            securityPatch = Build.VERSION.SECURITY_PATCH;
        }
        String device_type = Build.TAGS;
        String IMEI = Build.getRadioVersion();

        String function = "phoneInfo";
        Toast.makeText(getApplicationContext(), "Phone Info Posted!",
                Toast.LENGTH_SHORT).show();
        BackgroundRegTask_2 bgTask = new BackgroundRegTask_2();
        bgTask.execute(function, serial, brand, model, os_version, api_version, incremental,
                device_id, kernel, e_board, fingerprint, user,
                codename, base_os, securityPatch, device_type, IMEI, baseband_version);
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent register = new Intent(this, com.codesndata.maps.RegistrationActivity.class);
            startActivity(register);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
