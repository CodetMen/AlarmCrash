package com.codetmen.alarmcrash.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codetmen.alarmcrash.AddEditActivity;
import com.codetmen.alarmcrash.R;
import com.codetmen.alarmcrash.model.Alarm;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolderAlarm> {

    private String time, date, title, message;

    private LinkedList<Alarm> alarms;
    Activity activity;

    public AlarmAdapter(Activity activity) {
        this.activity = activity;
    }

    public LinkedList<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(LinkedList<Alarm> alarms) {
        this.alarms = alarms;
    }

    @NonNull
    @Override
    public ViewHolderAlarm onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View alarmLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_view_alarm, viewGroup, false);
        ViewHolderAlarm holder = new ViewHolderAlarm(alarmLayout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAlarm viewHolderAlarm, final int position) {
        viewHolderAlarm.tvItemTitle.setText(alarms.get(position).getTitle());
        viewHolderAlarm.tvItemAlarmTime.setText(alarms.get(position).getTime());
        viewHolderAlarm.tvItemAlarmDate.setText(alarms.get(position).getDate());

        // btnShare alarm action
        viewHolderAlarm.ibItemAlarmShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = getAlarms().get(position).getTime();
                date = getAlarms().get(position).getDate();
                title = getAlarms().get(position).getTitle();
                message = getAlarms().get(position).getMessage();

                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // add data to intent, the receiving app will decide
                // what to do with it
                intentShare.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT: " + title);
                intentShare.putExtra(Intent.EXTRA_TEXT, "Extra Text: " + message + "-" + time + "-" +date);
                activity.startActivity(Intent.createChooser(intentShare, "Title in startActivity method"));
            }
        });

        // btnEdit alarm action
        viewHolderAlarm.ibItemAlarmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEdit = new Intent(activity, AddEditActivity.class);
                intentEdit.putExtra(AddEditActivity.EXTRA_DATA, getAlarms().get(position));
                intentEdit.putExtra(AddEditActivity.EXTRA_POSITION, position);
                activity.startActivityForResult(intentEdit, AddEditActivity.REQUEST_EDIT_ALARM );
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    class ViewHolderAlarm extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_alarm_icon)
        ImageView ivAlarmIcon;
        @BindView(R.id.tv_item_alarmTime)
        TextView tvItemAlarmTime;
        @BindView(R.id.tv_item_alarmDate)
        TextView tvItemAlarmDate;
        @BindView(R.id.ib_item_alarmEdit)
        ImageButton ibItemAlarmEdit;
        @BindView(R.id.line_horizontal)
        View lineHorizontal;
        @BindView(R.id.ib_item_alarmShare)
        ImageButton ibItemAlarmShare;
        @BindView(R.id.tv_item_title)
        TextView tvItemTitle;

        public ViewHolderAlarm(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
