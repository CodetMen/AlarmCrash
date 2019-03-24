package com.codetmen.alarmcrash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.codetmen.alarmcrash.adapter.AlarmAdapter;
import com.codetmen.alarmcrash.database.AlarmHelper;
import com.codetmen.alarmcrash.model.Alarm;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_STORAGE = 502;
    private static final int PERMISSION_REQUEST_INTERNET = 401;
    private static final String loadAdsId = "ca-app-pub-1674500150931673~4373149659";
    private static final String loadIntersitialId = "ca-app-pub-1674500150931673/3036140960";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_item_view_alarms)
    RecyclerView rvItemViewAlarms;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tv_warn_empty_data)
    TextView tvWarnData;

    private LinkedList<Alarm> list;
    private AlarmAdapter alarmAdapter;
    private AlarmHelper alarmHelper;
    private FirebaseAnalytics firebaseAnalytics;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Instance admob intersitial
        MobileAds.initialize(this, loadAdsId);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(loadIntersitialId);
        interstitialAd.loadAd(new AdRequest.Builder().build());

        // obtain firebaseAnalytic instance
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // open database
        alarmHelper = new AlarmHelper(MainActivity.this);
        alarmHelper.open();

        // permission internet check
        permitInternetCheck();

        // btn add alarm and check for storage permission
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check permission request
                permitStorageCheck();
            }
        });

        // LOAD ALARM DATA
        list = new LinkedList<>();

        alarmAdapter = new AlarmAdapter(MainActivity.this);
        alarmAdapter.setAlarms(list);
        rvItemViewAlarms.setAdapter(alarmAdapter);

        new LoadAlarmDataAsync().execute();

        // SetLayoutManager recyclerview
        rvItemViewAlarms.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rvItemViewAlarms.setHasFixedSize(true);

        // log event firebaseAnalytics
        logFirebaseAnalytic();
    }

    private void logFirebaseAnalytic() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void permitInternetCheck() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

        } else {
            requestInternet();
        }
    }

    private void requestInternet() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET)){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_INTERNET);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_INTERNET);
        }
    }

    // permission access storage check
    private  void permitStorageCheck(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            // if request available go to the another activity
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivityForResult(intent, AddEditActivity.REQUEST_ADD_ALARM);
        } else {
            requestReadStorage();
        }
    }

    // permission to read storage
    private void requestReadStorage() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Request the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // condition display after add alarm data
        if (requestCode == AddEditActivity.REQUEST_ADD_ALARM){
            if (resultCode == AddEditActivity.RESULT_ADD_ALARM){
                new LoadAlarmDataAsync().execute();
                Snackbar.make(rvItemViewAlarms, "One item successfully added", Snackbar.LENGTH_LONG).show();
            }
        }

        // update and delete have same request code but different result code
        // ToDO repair this code for result_code
        else if (requestCode == AddEditActivity.REQUEST_EDIT_ALARM){
            if (resultCode == AddEditActivity.RESULT_EDIT_ALARM){
                new LoadAlarmDataAsync().execute();
                int position = data.getIntExtra(AddEditActivity.EXTRA_POSITION, 0);
                rvItemViewAlarms.getLayoutManager().smoothScrollToPosition(rvItemViewAlarms, new RecyclerView.State(), position);
                Snackbar.make(rvItemViewAlarms, "One item successfully edited", Snackbar.LENGTH_LONG).show();
            }
            else if (resultCode == AddEditActivity.RESULT_DELETE_ALARM){
                new LoadAlarmDataAsync().execute();
                Snackbar.make(rvItemViewAlarms, "One item successfully deleted", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        interstitialAd.show();
    }

    // LOAD ALARM DATA
    private class LoadAlarmDataAsync extends AsyncTask<Void, Void, ArrayList<Alarm>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (list.size() > 0){
                list.clear();
            }
        }

        @Override
        protected ArrayList<Alarm> doInBackground(Void... voids) {
            return alarmHelper.query();
        }

        @Override
        protected void onPostExecute(ArrayList<Alarm> alarms) {
            super.onPostExecute(alarms);

            // sort alarm data by date
            Collections.sort(alarms, new Comparator<Alarm>() {
                @Override
                public int compare(Alarm o1, Alarm o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });

            list.addAll(alarms);
            if (list.size() == 0){
                tvWarnData.setVisibility(View.VISIBLE);
            } else {
                tvWarnData.setVisibility(View.GONE);
            }

            alarmAdapter.setAlarms(list);
            alarmAdapter.notifyDataSetChanged();
        }
    }
}
