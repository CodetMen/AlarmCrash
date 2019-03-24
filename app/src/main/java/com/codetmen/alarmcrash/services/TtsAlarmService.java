package com.codetmen.alarmcrash.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TtsAlarmService extends Service implements TextToSpeech.OnInitListener {

    public static final String EXTRA_MSG = "extra_message";
    private TextToSpeech tts;
    private String message;
    private static final String TAG = "TTS_SERVICE";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(getBaseContext(), this);
        tts.setSpeechRate(0.5f);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        message = intent.getStringExtra(TtsAlarmService.EXTRA_MSG);
        sayingSomething(message);
        Log.v(TAG, "Running on start "+ message);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.stop();
        tts.shutdown();
    }

    @Override
    public void onInit(int status) {
        Log.v(TAG, "oninit");
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            } else {
                sayingSomething(message);
            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private void sayingSomething(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
        Log.v(TAG, "SAYING");
    }
}
