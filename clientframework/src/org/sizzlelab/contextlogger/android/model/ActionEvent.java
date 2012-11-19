package org.sizzlelab.contextlogger.android.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ActionEvent implements Parcelable{
	
	private static final String TIME_FORMAT_ALL = "dd.MM.yyyy HH:mm:ss";
	
	private long mStartTimestamp = -1L;
	private long mBreakTimestamp = -1L;
	private String mEventName = "";
	private boolean mHistory = false;
	private EventState mState = EventState.UNKNOWN;

	public ActionEvent(final String name){
		mEventName = name;
		mStartTimestamp = System.currentTimeMillis();
	}

	public final String getActionEventName(){
		return mEventName;
	}
	
	public void setState(EventState es){
		mState = es;
	}
	
	public final String getEventState(){
		return mState.toString();
	}
	
	public final String getMessagePayload(){
		String msg = "";
		try {
			msg = getActionEventName().replace(" ", "_");
			msg += "_" + getEventState();
		}catch(NullPointerException e){
			return msg;
		}
		return msg.toUpperCase();
	}
	
	public final long getStartTimestamp(){
		return mStartTimestamp;
	}
	
	public void confirmBreakTimestamp(){
		mBreakTimestamp = System.currentTimeMillis();
		mHistory = true;
	}
	
	public final long getBreakTimestamp(){
		return mBreakTimestamp;
	}
	
	public final String getStartTime(){
		return new String (new SimpleDateFormat(TIME_FORMAT_ALL).format(new Date(mStartTimestamp)));
	}
	
	public final boolean isHistory(){
		return mHistory;
	}
	
	public final long getEventDuration(){
		if(mBreakTimestamp > 0){
			return (mBreakTimestamp - mStartTimestamp);
		} else {
			return (System.currentTimeMillis() - mStartTimestamp);
		}
	}

	public final String getDuration(){
		int days = 0;
		int hours = 0;
		int mins = 0;
		final long eventDuration = getEventDuration();
		days = (int)(eventDuration / (24 * 60 * 60 * 1000));
		hours = (int) ((eventDuration - days * (24 * 60 * 60 * 1000)) / (60 * 60 * 1000));
		mins = (int) ((eventDuration - days * (24 * 60 * 60 * 1000) - hours * (60 * 60 * 1000)) / (60 * 1000));
		if((days == 0) && (hours == 0) && (mins == 0)){
			return "less than one minute";
		}
		return new String(days + "d, " + hours + "h, " + mins + "m");
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mEventName);
		dest.writeLong(mStartTimestamp);
		dest.writeLong(mBreakTimestamp);
		dest.writeLong(mHistory ? 1 : 0);
		dest.writeSerializable(mState);
	}
	
	public static final Parcelable.Creator<ActionEvent> CREATOR = new Parcelable.Creator<ActionEvent>() {
		public ActionEvent createFromParcel(Parcel in) {
			return new ActionEvent(in);
		}

		public ActionEvent[] newArray(int size) {
			return new ActionEvent[size];
		}
	};
	
	private ActionEvent(Parcel in){
		mEventName = in.readString();
		mStartTimestamp = in.readLong();
		mBreakTimestamp = in.readLong();
		mHistory = (in.readLong() == 1);
		mState = (EventState) in.readSerializable();
	}
}
