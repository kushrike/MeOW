//package com.example.meow;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.FragmentActivity;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//import android.widget.Imag eView;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.libraries.places.api.net.PlacesClient;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback {
//
//    private GoogleMap mMap;
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
//    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
//    private Boolean mLocationPermissionsGranted = false;
//    private static final String TAG = "MapActivity";
//    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION};
//    private FusedLocationProviderClient mFusedLocationProviderClient;
//    private static final float DEFAULT_ZOOM = 15f;
//    private EditText mSearchText;
//    private ImageView mGps;
//
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        mSearchText = (EditText) findViewById(R.id.input_search);
//        mGps=(ImageView)findViewById(R.id.ic_gps);
//        Places.initialize(getApplicationContext(), apiKey);
//        PlacesClient placesClient = Places.createClient(this);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        getLocationPermission();
//        init();
//    }
//
//    private void init(){
//        Log.d(TAG, "init: initializing");
//
//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        ||keyEvent.getAction() ==KeyEvent.KEYCODE_SHIFT_LEFT
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    Log.d(TAG, "init: initializing2");
//                    //execute our method for searching
//                    geoLocate();
//                }
//
//                return false;
//            }
//        });
//        mGps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "oClick clicked GPS icon");
//                getDeviceLocation();
//            }
//        });
//    }
//    private void getLocationPermission(){
//        Log.d(TAG, "getLocationPermission: getting location permissions");
//
//
//        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                mLocationPermissionsGranted = true;
//            }else{
//                ActivityCompat.requestPermissions(this,
//                        permissions,
//                        LOCATION_PERMISSION_REQUEST_CODE);
//            }
//        }else{
//            ActivityCompat.requestPermissions(this,
//                    permissions,
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        }
//    }
//
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "onMapReady: map is ready");
//        mMap = googleMap;
//        if (mLocationPermissionsGranted) {
//            getDeviceLocation();
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(false);
////            init();
//        }
//    }
//    private void geoLocate(){
//        Log.d(TAG, "geoLocate: geolocating");
//
//        String searchString = mSearchText.getText().toString();
//
//        Geocoder geocoder = new Geocoder(MapsActivity.this);
//        List<Address> list = new ArrayList<>();
//        try{
//            list = geocoder.getFromLocationName(searchString, 1);
//        }catch (IOException e){
//            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
//        }
//
//        if(list.size() > 0){
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found a location: " + address.toString());
//            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                    address.getAddressLine(0));
//
//        }
//    }
//    private void getDeviceLocation(){
//        Log.d(TAG, "getDeviceLocation: getting the devices current location");
//
//        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        try{
//            if(mLocationPermissionsGranted){
//
//                final Task location = mFusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if(task.isSuccessful()){
//                            Log.d(TAG, "onComplete: found location!");
//                            Location currentLocation = (Location) task.getResult();
//
//                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
//                                    DEFAULT_ZOOM,"My Location");
//
//                        }else{
//                            Log.d(TAG, "onComplete: current location is null");
//                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }catch (SecurityException e){
//            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
//        }
//    }
//    private void moveCamera(LatLng latLng, float zoom, String title){
//        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        if(!title.equals("My Location")){
//            MarkerOptions options = new MarkerOptions()
//                    .position(latLng)
//                    .title(title);
//            mMap.addMarker(options);
//        }
//
//        hideSoftKeyboard();
//    }
//
//    //onMyLocationButtonClick() hai next
//
////    @Override
////    public boolean onMyLocationButtonClick() {
////        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
////        // Return false so that we don't consume the event and the default behavior still occurs
////        // (the camera animates to the user's current position).
////        return false;
////    }
////
////    @Override
////    public void onMyLocationClick(@NonNull Location location) {
////        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
////    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called.");
//        mLocationPermissionsGranted = false;
//
//        switch(requestCode){
//            case LOCATION_PERMISSION_REQUEST_CODE:{
//                if(grantResults.length > 0){
//                    for(int i = 0; i < grantResults.length; i++){
//                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
//                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
//                            return;
//                        }
//                    }
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
//                    mLocationPermissionsGranted = true;
//                    //initialize our map
//
//                }
//            }
//        }
//    }
////    @Override
////    protected void onResumeFragments() {
////        super.onResumeFragments();
////        if (mPermissionDenied) {
////            // Permission was not granted, display error dialog.
////            showMissingPermissionError();
////            mPermissionDenied = false;
////        }
////    }
//private void hideSoftKeyboard(){
//    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//}
//
//
//
//}
