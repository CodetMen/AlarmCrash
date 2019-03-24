package com.codetmen.alarmcrash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codetmen.alarmcrash.services.TtsAlarmService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionAlarmActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "extra_message";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_TIME = "extra_time";
    private static final String loadAdsId = "ca-app-pub-1674500150931673~4373149659";

    @BindView(R.id.tv_display_time)
    TextView tvDisplayTime;
    @BindView(R.id.tv_display_date)
    TextView tvDisplayDate;
    @BindView(R.id.tv_display_title)
    TextView tvDisplayTitle;
    @BindView(R.id.tv_display_message)
    TextView tvDisplayMessage;
    @BindView(R.id.btn_STOP)
    ImageButton btnSTOP;
    @BindView(R.id.adview_banner)
    AdView mAdView;

    private String title, message, date, time;

    Activity activity;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // letting the activity open when sleeping
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set layout and cast id layout
        setContentView(R.layout.activity_action_alarm);
        ButterKnife.bind(this);

        // instance firebase
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // initial dat where will get from alarmreceiver
        initData();

        // custom view component
        viewComponent();

        // btn stop to perform end sound and play text to speech
        btnSTOP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endSoundAlarm();
            }
        });

        // set max volume
        setMavVolume();

        // play message
        playMessage(message);

        loadAdBanner();

        // log firebase
        logFirebaseAnalytic();
    }

    private void logFirebaseAnalytic() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "ActionAlarmActivity");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void loadAdBanner() {
        MobileAds.initialize(this, loadAdsId);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setMavVolume() {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        while (volume < maxVolume){
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
        }
    }

    // initial field
    private void initData() {
        // getting data sending by AlarmReceiver
        title = getIntent().getStringExtra(EXTRA_TITLE);
        message = getIntent().getStringExtra(EXTRA_MESSAGE);
        date = getIntent().getStringExtra(EXTRA_DATE);
        time = getIntent().getStringExtra(EXTRA_TIME);
    }

    // component on layout
    private void viewComponent() {
        tvDisplayTime.setText(time);
        tvDisplayDate.setText(date);
        tvDisplayTitle.setText(title);
        tvDisplayMessage.setText(message);
        tvDisplayMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
    }

    // to stop everything happenning
    private void endSoundAlarm() {
        Intent intentTts = new Intent(ActionAlarmActivity.this, TtsAlarmService.class);
        stopService(intentTts);
        finish();
        System.exit(0);
    }

    private void playMessage(String msg) {
        Intent intentTts = new Intent(ActionAlarmActivity.this, TtsAlarmService.class);
        intentTts.putExtra(TtsAlarmService.EXTRA_MSG, msg);
        startService(intentTts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}