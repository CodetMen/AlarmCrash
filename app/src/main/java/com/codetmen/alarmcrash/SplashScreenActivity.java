package com.codetmen.alarmcrash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // redirect to the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);

        logFirebaseAnalytic();
    }

    private void logFirebaseAnalytic() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "SplashScreenActivity");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
