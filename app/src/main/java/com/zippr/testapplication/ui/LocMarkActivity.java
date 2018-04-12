package com.zippr.testapplication.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.zippr.testapplication.R;
import com.zippr.testapplication.dataaccess.DataLoader;
import com.zippr.testapplication.models.SelLocDO;

import java.util.ArrayList;

import static com.zippr.testapplication.common.AppInstance.FETCH_SHOW_ALLLOC;
import static com.zippr.testapplication.dataaccess.DbCall.DbCallPref.FETCHALLLOCS;

public class LocMarkActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LoaderManager.LoaderCallbacks {

    private GoogleMap mMap;
    private boolean isMapReady = false, isFetched = false;
    private ArrayList<SelLocDO> arrList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_mark);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSupportLoaderManager().initLoader(FETCH_SHOW_ALLLOC, null, this).forceLoad();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case FETCH_SHOW_ALLLOC:
                return new DataLoader(this, bundle, FETCHALLLOCS);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case FETCH_SHOW_ALLLOC:
                if(data != null) {
                    arrList = (ArrayList<SelLocDO>) data;

                    isFetched = true;
                    showMarker();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
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

        // Add a marker in Sydney and move the camera
//        LatLng madhapur = new LatLng(17.442177, 78.391307);
//        mMap.addMarker(new MarkerOptions().position(madhapur).title("7")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//
//        mMap.addMarker(new MarkerOptions().position(new LatLng(17.472662, 78.386152)).title("4"));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(17.421705, 78.410210)).title("8"));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(17.438981, 78.446695)).title("11")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(17.497576, 78.353498)).title("1"));
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madhapur, 12f));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                mMap.setMyLocationEnabled(true);
            }
        } else
            mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        showMarker();
    }

    void showMarker() {
        if(arrList != null && arrList.size() > 0) {
            for(SelLocDO objSellocDo : arrList) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(objSellocDo.locLat, objSellocDo.locLng)).title(objSellocDo.name));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arrList.get(0).locLat, arrList.get(0).locLng), 12f));
        }
    }
}
