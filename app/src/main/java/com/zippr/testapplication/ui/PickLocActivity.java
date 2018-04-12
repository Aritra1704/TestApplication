package com.zippr.testapplication.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Network;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arpaul.gpslibrary.fetchAddressGeoCode.AddressConstants;
import com.arpaul.gpslibrary.fetchAddressGeoCode.AddressDO;
import com.arpaul.gpslibrary.fetchAddressGeoCode.FetchAddressLoader;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.NetworkUtility;
import com.arpaul.utilitieslib.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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
import static com.zippr.testapplication.common.AppInstance.LOADER_FETCH_ADDRESS;
import static com.zippr.testapplication.common.AppInstance.LOADER_SAVE_LOC;
import static com.zippr.testapplication.dataaccess.DbCall.DbCallPref.SAVELOC;
import static com.zippr.testapplication.common.AppConstant.INTENT_REFRESH_LIST;

public class PickLocActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener,
        LocationListener {

    private final String LOG_TAG = "PickLocActivity";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private boolean isGpsEnabled = false;
    private LatLng currentLatLng;
    private ImageView ivMarker;
    private EditText edtAddress;
    private boolean isMapReady = false, isConnected = false, isUserInteracted = false, ispermissionGranted = false;
    private Button btnSelectLoc;
    private LatLng geoPosi;
    private ProgressBar pbLoad;
    private LocationRequest mLocationRequest;

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
                ispermissionGranted = true;
            }
        } else {
            buildGoogleApiClient();
            ispermissionGranted = true;
        }

        btnSelectLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Projection projection = mMap.getProjection();
                int[] locations = new int[2];
                int x = ivMarker.getLeft() + (ivMarker.getRight() - ivMarker.getLeft())/2;
                int y = ivMarker.getBottom();
                geoPosi = projection.fromScreenLocation(new Point(x, y));

                if(!TextUtils.isEmpty(edtAddress.getText().toString())) {
                    showLoader(true);
                    saveLocation(edtAddress.getText().toString());
                } else {
                    if(!NetworkUtility.isConnectionAvailable(PickLocActivity.this))
                        saveLocation("Offline saved");
                    Toast.makeText(PickLocActivity.this, "Unable to find your address", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void saveLocation(final String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int count = new LocationDL().fetchLocsCount(PickLocActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        SelLocDO objSelLoc = new SelLocDO("" + (count + 1), address, 1, geoPosi.latitude, geoPosi.longitude);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BUNDLE_LOC, objSelLoc);

                        getSupportLoaderManager().initLoader(LOADER_SAVE_LOC, bundle, PickLocActivity.this).forceLoad();
                    }
                });
            }
        }).start();
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
            startIntentService();
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000); // Update location every second

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
                ispermissionGranted = true;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        isMapReady = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                mMap.setMyLocationEnabled(true);
            }
        } else
            mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(isGpsEnabled) {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    showCurrentLatlng();
                    mMap.setOnCameraMoveStartedListener(PickLocActivity.this);
                    mMap.setOnCameraIdleListener(PickLocActivity.this);
                }
            }, 500);
        }
        else if(ispermissionGranted) {
            Toast.makeText(this, "GPS is not enabled.", Toast.LENGTH_SHORT).show();
        }

        showCurrentLatlng();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOADER_SAVE_LOC:
                return new DataLoader(this, bundle, SAVELOC);

            case LOADER_FETCH_ADDRESS:
                return new FetchAddressLoader(this, currentLatLng);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(final Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_SAVE_LOC:
                    sendBroadcast(new Intent(INTENT_REFRESH_LIST));
                    showLoader(false);
                    finish();
                break;

            case LOADER_FETCH_ADDRESS:
                if(data instanceof AddressDO){
                    final AddressDO objAddressDO = (AddressDO) data;
                    if(objAddressDO.code == AddressConstants.SUCCESS_RESULT) {

                        setAddress(objAddressDO.message);
                    } else if(objAddressDO.code == AddressConstants.FAILURE_RESULT) {
                        Toast.makeText(PickLocActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                break;
        }

        showLoader(false);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        isUserInteracted = true;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onCameraIdle() {
        // Cleaning all the markers.
        if (mMap != null) {
            mMap.clear();
        }

        currentLatLng = mMap.getCameraPosition().target;
//        mZoom = mMap.getCameraPosition().zoom;

        if(NetworkUtility.isConnectionAvailable(PickLocActivity.this))
            startIntentService();
        else
            setAddress("");

    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtils.infoLog(LOG_TAG, location.toString());

        if(!isUserInteracted) {
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            showCurrentLatlng();
            startIntentService();
        }

//        Toast.makeText(LocationSearchActivity.this, "Lat: "+currentLatLng.latitude+" Lon: "+currentLatLng.longitude, Toast.LENGTH_SHORT).show();
    }

    protected void startIntentService() {
        if(getSupportLoaderManager().getLoader(LOADER_FETCH_ADDRESS) == null)
            getSupportLoaderManager().initLoader(LOADER_FETCH_ADDRESS, null, this).forceLoad();
        else
            getSupportLoaderManager().restartLoader(LOADER_FETCH_ADDRESS, null, this).forceLoad();
    }

    void showCurrentLatlng() {
        if(isMapReady && isConnected) {
//            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
        }
    }

    private void setAddress(String message){
        if(message.contains("\n"))
            message = message.replace("\n", " ");
        edtAddress.setText(message);
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

    private void showLoader(boolean show) {
        if(show)
            pbLoad.setVisibility(View.VISIBLE);
        else
            pbLoad.setVisibility(View.INVISIBLE);
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
        pbLoad = (ProgressBar) findViewById(R.id.pbLoad);

        edtAddress = (EditText) findViewById(R.id.edtAddress);

        showLoader(false);
    }
}
