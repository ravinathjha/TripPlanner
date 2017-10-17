package com.example.anirban.tripplanner.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.anirban.tripplanner.R;
import com.example.anirban.tripplanner.helper.PreferenceHelper;
import com.example.anirban.tripplanner.map_helper.DirectionFinder;
import com.example.anirban.tripplanner.map_helper.DirectionFinderListener;
import com.example.anirban.tripplanner.map_helper.Route;
import com.example.anirban.tripplanner.prelogin.LogInActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HomeMapActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener, android.location.LocationListener {

    private static final int MY_PERMISSION_REQUEST_LOCATION = 99;

    private GoogleMap mMap;
    private EditText mChooseSource;
    private EditText mChooseDestination;
    private ProgressDialog progressDialog;
    private Marker startMarker, endMarker;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateLisener;

    private List<Polyline> polylinePaths = new ArrayList<>();
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private boolean firstTimeCheckForSource;
    private boolean firstTimeCheckForDestination;
    private DrawerLayout drawerLayout;
    private ListView mList;
    private String[] item;
    private ActionBarDrawerToggle drawerListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceHelper.getInstance().putString(R.string.pref_id, "0");
        setContentView(R.layout.activity_home_map);
        getSupportActionBar().setTitle("TripPlanner");
        mChooseSource = (EditText) findViewById(R.id.choose_start);
        mChooseDestination = (EditText) findViewById(R.id.choose_destination);
        mChooseSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (firstTimeCheckForSource) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(HomeMapActivity.this);
                        startActivityForResult(intent, 90);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    }
                }
            }
        });
        mChooseSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTimeCheckForSource = true;
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(HomeMapActivity.this);
                    startActivityForResult(intent, 90);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                }

            }
        });


        mChooseDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTimeCheckForDestination = true;
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(HomeMapActivity.this);

                    startActivityForResult(intent, 91);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                }

            }
        });

        mChooseDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(HomeMapActivity.this);

                    startActivityForResult(intent, 91);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateLisener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent logInIntent = new Intent(HomeMapActivity.this, LogInActivity.class);
                    logInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logInIntent);
                }
            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Navigation Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerListener = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                Toast.makeText(HomeMapActivity.this, "Drawer Open", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Toast.makeText(HomeMapActivity.this, "Drawer Close", Toast.LENGTH_SHORT).show();
            }
        };
        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        item = getResources().getStringArray(R.array.navigation);
        mList = (ListView) findViewById(R.id.list);
        mList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, item));
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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = getLastKnownLocation();
        if (location != null)
            goToLocationZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 90) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mChooseSource.setText(place.getName());
                startMarker = addMarkerToMap(place.getName().toString(), place.getLatLng());
            } else if (requestCode == 91) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mChooseDestination.setText(place.getName());
                endMarker = addMarkerToMap(place.getName().toString(), place.getLatLng());
                sendRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Marker addMarkerToMap(String placeName, LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
        goToLocationZoom(latLng, 15);
        return marker;
    }

    private void goToLocationZoom(LatLng latLng, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(cameraUpdate);
    }

    private void sendRequest() {
        String origin = mChooseSource.getText().toString();
        String destination = mChooseDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (startMarker != null) {
            startMarker.remove();
        }

        if (endMarker != null) {
            endMarker.remove();
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();


        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 15));


            mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation));
            mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
            polylinePaths.add(mMap.addPolyline(polylineOptions));


        }
    }

    boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.location_title)
                        .setMessage(R.string.location_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HomeMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSION_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , MY_PERMISSION_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request Location Update
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                2000L,
                                10f, this);
                        mMap.setMyLocationEnabled(true);

                    }
                } else {
                    // If denied to turn on
                }
                return;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                // Request Location Update
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        2000L,
                        10f, this);


            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        mAuth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateLisener);
    }


    @Override
    public void onLocationChanged(Location location) {
        goToLocationZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
