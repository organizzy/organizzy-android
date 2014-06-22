package org.organizzy.client.android.alarm;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class AlarmData implements Parcelable {

    public static final String TYPE_EVENT = "event";
    public static final String TYPE_TASK = "task";

    public int id;
    public String url;
    public String title;
    public long time;
    public String type;

    public static final Parcelable.Creator<AlarmData> CREATOR = new Parcelable.Creator<AlarmData>()
    {
        public AlarmData createFromParcel(Parcel in) {
            return new AlarmData(in);
        }

        public AlarmData[] newArray(int size) {
            return new AlarmData[size];
        }
    };

    public AlarmData(int id) {
        this.id = id;
    }

    private AlarmData(Parcel parcel) {
        id = parcel.readInt();
        type = parcel.readString();
        url = parcel.readString();
        title = parcel.readString();
        time = parcel.readLong();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(type);
        parcel.writeString(url);
        parcel.writeString(title);
        parcel.writeLong(time);
    }

    public boolean isEvent() {
        return TYPE_EVENT.equals(type);
    }

    public boolean isTask() {
        return TYPE_TASK.equals(type);
    }

    public String getDescription() {
        String description = "";
        if (isEvent()) description += "(Event) ";
        if (isTask()) description += "(Task) ";
        return description + DateFormat.getDateTimeInstance().format(new Date(time));
    }
}
