package com.example.ocksangyun.map_fix;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSION_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private GpsInfo gps;

    static final String Tag = "PlaceAutocomplete";
    ListView listView = null;
    DrawerLayout drawerLayout;
    ImageButton gps_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        CardView cardView = (CardView)findViewById(R.id.cardView);
        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams)cardView.getLayoutParams();
        params1.topMargin = getStatusBarheight() + 30;
        cardView.setLayoutParams(params1);

        gps_button = (ImageButton)findViewById(R.id.gps_button);
        final String[] items = {"알람 설정", "앱 설정", "기본 경로 설정"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listView = (ListView) findViewById(R.id.drawer_menulist);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) listView.getLayoutParams();
        params.topMargin = getStatusBarheight();
        listView.setLayoutParams(params);

        listView.setAdapter(adapter);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent alarm_intent = new Intent(MapsActivity.this, Alarm.class);
                        startActivity(alarm_intent);

                        break;
                    case 1:
                        Intent setting_intent = new Intent(MapsActivity.this, Setting.class);
                        startActivity(setting_intent);
                        break;
                    case 2:
                        Intent location_set_intent = new Intent(MapsActivity.this, MapsActivity.class);
                        startActivity(location_set_intent);
                        break;
                }

                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPermission){
                    callPermission();
                    return;
                }
             buttonView();
            }
        });
        callPermission();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                mMap.clear();
                Double latitude = latLng.latitude;
                Double longtitude = latLng.longitude;
                moveLocation(latitude, longtitude, place.getName().toString());
            }

            @Override
            public void onError(Status status) {
                Log.i(Tag, "An error occured: " + status);
            }
        });
        mapFragment.getMapAsync(this);

    }

    public void openDrawer() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Double latitude = latLng.latitude;
                Double longtitude = latLng.longitude;
                moveLocation(latitude, longtitude, null);

            }
        });
        mainView();
    }

    private void moveLocation(final double lat, final double lnt, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("기본 목적지로 설정");
        markerOptions.snippet(title);
        markerOptions.position(new LatLng(lat, lnt));
        LatLng curPoint = new LatLng(lat, lnt);
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 18));
        mMap.addMarker(markerOptions).showInfoWindow();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
               saveLocation(lat,lnt);
            }
        });

    }
    private void fastmoveCamera(final double lat, final double lnt, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("기본 목적지로 설정");
        markerOptions.snippet(title);
        markerOptions.position(new LatLng(lat, lnt));
        LatLng curPoint = new LatLng(lat, lnt);
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 18));
        mMap.addMarker(markerOptions).showInfoWindow();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                saveLocation(lat,lnt);
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION) {
            if(requestCode == PERMISSIONS_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isAccessFineLocation = true;
            }
        } else if (requestCode == PERMISSION_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isAccessCoarseLocation = true;
        }
        if(isAccessFineLocation && isAccessCoarseLocation){
            isPermission = true;
        }
    }
    private void callPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_ACCESS_FINE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }
    private void buttonView() {
        gps = new GpsInfo(MapsActivity.this);
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longtitude = gps.getLongtitude();
            moveLocation(latitude, longtitude, null);
        } else {
            gps.showSettingsAlert();

        }
    }
    private void mainView() {
        gps = new GpsInfo(MapsActivity.this);
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longtitude = gps.getLongtitude();
            fastmoveCamera(latitude, longtitude, null);
        } else {
            gps.showSettingsAlert();

        }
    }

public int getStatusBarheight()
{
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if(resourceId > 0)
    {
        result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
}

public void saveLocation(final double lat, final double lnt){
    SharedPreferences preferences = getSharedPreferences("default_location", MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString("default_lat", Double.toString(lat));
    editor.putString("default_lnt", Double.toString(lnt));
    editor.commit();
    Toast.makeText(getApplicationContext(), "기본 목적지로 설정되었습니다.", Toast.LENGTH_LONG).show();
}
}






