package com.zippr.testapplication.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.arpaul.utilitieslib.StringUtils;
import com.zippr.testapplication.R;
import com.zippr.testapplication.dataaccess.DataLoader;
import com.zippr.testapplication.models.SelLocDO;

import java.util.ArrayList;

import static com.zippr.testapplication.common.AppConstant.INTENT_REFRESH_LIST;
import static com.zippr.testapplication.common.AppInstance.FETCH_SHOW_ALLLOC;
import static com.zippr.testapplication.common.AppInstance.LOADER_SAVE_LOC;
import static com.zippr.testapplication.dataaccess.CPConstants.T_SelLoc.LOCLAT;
import static com.zippr.testapplication.dataaccess.CPConstants.T_SelLoc.LOCLNG;
import static com.zippr.testapplication.dataaccess.CPConstants.T_SelLoc.LOC_ID;
import static com.zippr.testapplication.dataaccess.CPConstants.T_SelLoc.NAME;
import static com.zippr.testapplication.dataaccess.CPConstants.T_SelLoc.PARCELCOUNT;
import static com.zippr.testapplication.dataaccess.DbCall.DbCallPref.FETCHALLLOCS;
import static com.zippr.testapplication.dataaccess.DbCall.DbCallPref.SAVELOC;

public class LocationSelcActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private RecyclerView rvList;
    private FloatingActionButton fabAdd;
    private Button btnNavigate;
    private LocationAdapter adapter;

    private ArrayList<SelLocDO> arrLoc = new ArrayList<SelLocDO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selc);

        rvList = (RecyclerView) findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocationAdapter(this, new ArrayList<SelLocDO>());
        rvList.setAdapter(adapter);

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        btnNavigate = (Button) findViewById(R.id.btnNavigate);

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LocationSelcActivity.this, LocMarkActivity.class));
//                startActivityForResult(new Intent(LocationSelcActivity.this, PickLocActivity.class), 1011);
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LocationSelcActivity.this, PickLocActivity.class), 1011);
            }
        });

        loadData();
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
                    ArrayList<SelLocDO> arrList = (ArrayList<SelLocDO>) data;

                    adapter.refresh(arrList);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    void loadData() {
        getSupportLoaderManager().initLoader(FETCH_SHOW_ALLLOC, null, this).forceLoad();
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(brRefresh, new IntentFilter(INTENT_REFRESH_LIST));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(brRefresh);
    }

    BroadcastReceiver brRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }
    };
}
