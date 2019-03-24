package com.codetmen.alarmcrash.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {

    private int id;
    private String time, date, message, title;

    public Alarm(){
    }

    public Alarm(int id, String time, String date, String message, String title) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.message = message;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.time);
        dest.writeString(this.date);
        dest.writeString(this.message);
        dest.writeString(this.title);
    }

    protected Alarm(Parcel in) {
        this.id = in.readInt();
        this.time = in.readString();
        this.date = in.readString();
        this.message = in.readString();
        this.title = in.readString();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel source) {
            return new Alarm(source);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };
}
