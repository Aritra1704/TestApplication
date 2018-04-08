package com.zippr.testapplication.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.arpaul.utilitieslib.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zippr.testapplication.R;
import com.zippr.testapplication.dataaccess.DataLoader;
import com.zippr.testapplication.datalayers.LocationDL;
import com.zippr.testapplication.models.SelLocDO;

import static com.zippr.testapplication.common.AppConstant.BUNDLE_LOC;
import static com.zippr.testapplication.common.AppInstance.LOADER_SAVE_LOC;
import static com.zippr.testapplication.dataaccess.DbCall.DbCallPref.SAVELOC;
import static com.zippr.testapplication.common.AppConstant.INTENT_REFRESH_LIST;

public class PickLocActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private boolean isGpsEnabled = false;
    private LatLng currentLatLng;
    private ImageView ivMarker;
    private boolean isMapReady = false, isConnected = false;
    private Button btnSelectLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_loc);

        initialiseUiControls();

        bindControls();
    }

    void bindControls() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(new PermissionUtils().checkPermission(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}) != PackageManager.PERMISSION_GRANTED){
                new PermissionUtils().requestPermission(this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            } else {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }

        btnSelectLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Projection projection = mMap.getProjection();
                int[] locations = new int[2];
                int x = ivMarker.getLeft() + (ivMarker.getRight() - ivMarker.getLeft())/2;
                int y = ivMarker.getBottom();
                final LatLng geoPosi = projection.fromScreenLocation(new Point(x, y));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final int count = new LocationDL().fetchLocsCount(PickLocActivity.this);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SelLocDO objSelLoc = new SelLocDO("" + (count + 1), "sample", 1, geoPosi.latitude, geoPosi.longitude);

                                Bundle bundle = new Bundle();
                                bundle.putSerializable(BUNDLE_LOC, objSelLoc);

                                getSupportLoaderManager().initLoader(LOADER_SAVE_LOC, bundle, PickLocActivity.this).forceLoad();
                            }
                        });
                    }
                }).start();
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Location location = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
        } else
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(location != null){
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            isConnected = true;
            showCurrentLatlng();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
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

        isMapReady = true;
        showCurrentLatlng();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOADER_SAVE_LOC:
                return new DataLoader(this, bundle, SAVELOC);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_SAVE_LOC:
                    sendBroadcast(new Intent(INTENT_REFRESH_LIST));
                    finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    void showCurrentLatlng() {
        if(isMapReady && isConnected) {
//            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(isGpsEnabled()) {
            isGpsEnabled = true;
        } else {
            isGpsEnabled = false;
            Toast.makeText(this, "GPS not enabled", Toast.LENGTH_SHORT).show();
        }

        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    private boolean isGpsEnabled(){
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsProviderEnabled;
    }

    void initialiseUiControls() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ivMarker = (ImageView) findViewById(R.id.ivMarker);
        btnSelectLoc = (Button) findViewById(R.id.btnSelectLoc);
    }
}
