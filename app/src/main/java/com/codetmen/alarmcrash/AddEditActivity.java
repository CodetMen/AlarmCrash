package com.codetmen.alarmcrash;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.codetmen.alarmcrash.database.AlarmHelper;
import com.codetmen.alarmcrash.model.Alarm;
import com.codetmen.alarmcrash.receiver.AlarmReceiver;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditActivity extends AppCompatActivity {

    @BindView(R.id.btn_appbar_cancel)
    ImageButton btnCancelBar;
    @BindView(R.id.btn_appbar_ok)
    ImageButton btnSaveBar;
    @BindView(R.id.appbar_addedit_activity)
    Toolbar toolbar;
    @BindView(R.id.btn_time)
    Button btnSetTime;
    @BindView(R.id.btn_date)
    Button btnSetDate;
    @BindView(R.id.et_messages)
    EditText etMessages;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.btn_delete)
    Button btnDelete;

    private boolean isEmptyInput = false;
    private boolean isEditInput = false;
    public static final String EXTRA_DATA = "data";
    public static final int REQUEST_ADD_ALARM = 101;
    public static final int REQUEST_EDIT_ALARM = 102;
    public static final int RESULT_ADD_ALARM = 501;
    public static final int RESULT_EDIT_ALARM = 502;
    public static final int RESULT_DELETE_ALARM = 503;
    public static String EXTRA_POSITION = "Extra_Position";

    private String dataTime, dataDate, dataMessages, dataTitle;
    private Alarm alarm;
    private int position, reqCodeAlarm;
    private AlarmHelper alarmHelper;
    private AlarmReceiver alarmReceiver;
    private Calendar calendar;
    private FirebaseAnalytics firebaseAnalytics;

    private ArrayList<Alarm> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // open database
        alarmHelper = new AlarmHelper(AddEditActivity.this);
        alarmHelper.open();

        // instance alarmreceiver
        alarmReceiver = new AlarmReceiver();

        // instance firebaseAnalytic
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // calender instance
        calendar = Calendar.getInstance();

        // ToDo : Check this code
        // check alarm have data or not if that have data so isEdit is true otherwise
        alarm = getIntent().getParcelableExtra(EXTRA_DATA);

        if (alarm != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEditInput = true;
        }

        // if there have data so displaying the data from database and btn delete
        if (isEditInput) {
            btnSetTime.setText(alarm.getTime());
            btnSetDate.setText(alarm.getDate());
            etTitle.setText(alarm.getTitle());
            etMessages.setText(alarm.getMessage());
            btnDelete.setVisibility(View.VISIBLE);
        }

        // button cancel bar action
        btnCancelBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        // button set time action
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        // button set date action
        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        // button set alarm data and action
        btnSaveBar.setOnClickListener(new View.OnClickListener() {
            @Override///
            public void onClick(View v) {
                saveAlarmData();
            }
        });

        // button delete alarm action
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlarmAction();
            }
        });

        // log firebase
        logFirebaseAnalytic();
    }

    private void logFirebaseAnalytic() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "AddEditActivity");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    // method delete alarm
    private void deleteAlarmAction() {
        alarmHelper.delete(alarm.getId());
        setResult(AddEditActivity.RESULT_DELETE_ALARM);
        finish();
    }

    // save all data input to database
    private void saveAlarmData() {
        // get every input
        dataTime = btnSetTime.getText().toString();
        dataDate = btnSetDate.getText().toString();
        dataTitle = etTitle.getText().toString();
        dataMessages = etMessages.getText().toString();

        // check data input is empty or not
        if (TextUtils.isEmpty(dataTime)) {
            isEmptyInput = true;
            btnSetTime.setError("This item is empty");
        }
        if (TextUtils.isEmpty(dataDate)) {
            isEmptyInput = true;
            btnSetDate.setError("This item is empty");
        }
        if (TextUtils.isEmpty(dataMessages)) {
            isEmptyInput = true;
            etMessages.setError("This item is empty");
        }
        if (TextUtils.isEmpty(dataTitle)){
            isEmptyInput = true;
            etTitle.setError("This item is empty");
        }

        // check for empty input
        if (!isEmptyInput) {

            Alarm newAlarm = new Alarm();
            newAlarm.setTime(dataTime);
            newAlarm.setDate(dataDate);
            newAlarm.setMessage(dataMessages);
            newAlarm.setTitle(dataTitle);

            Intent intent = new Intent();

            // check alarm bring data (edit) or not
            // ToDo: Implement AlarmReceiver
            if (isEditInput) {
                reqCodeAlarm = alarm.getId();
                newAlarm.setId(reqCodeAlarm);
                alarmHelper.update(newAlarm);
                alarmReceiver.setAlarmSchedule(AddEditActivity.this, dataDate, dataTime, dataTitle, dataMessages, reqCodeAlarm);
                intent.putExtra(EXTRA_POSITION, position);
                setResult(AddEditActivity.RESULT_EDIT_ALARM, intent);
                finish();
            } else {
                // ToDo: still error if you want add more new alarm
                // count data in database and plus one for adding new one
                listData = alarmHelper.query();
                int dataCount = listData.size();
                dataCount = dataCount + 1;
                reqCodeAlarm = dataCount;

                alarmHelper.insert(newAlarm);
                alarmReceiver.setAlarmSchedule(AddEditActivity.this, dataDate, dataTime, dataTitle, dataMessages, reqCodeAlarm);
                setResult(AddEditActivity.RESULT_ADD_ALARM);
                finish();
            }
        }
    }

    // set time action
    private void setTime() {
        // get current time
        final Calendar currentDate = Calendar.getInstance();
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                btnSetTime.setText(dateFormat.format(calendar.getTime()));
            }
        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
    }

    // set date action
    private void setDate() {
        // get current date
        final Calendar currentDate = Calendar.getInstance();
        new DatePickerDialog(AddEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                btnSetDate.setText(dateFormat.format(calendar.getTime()));
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    public void onBackPressed() {
        showAlertDialog();
    }

    // function alert dialog will show if user press onBack or cancel button
    private void showAlertDialog() {
        String dialogTitle = "Close";
        String dialogMessage = "Are you sure want to close this session ?";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddEditActivity.this);

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder.setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
