package org.drumm.android.alarms;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.deskclock.Alarm;
import com.android.deskclock.Alarms;

import org.drumm.android.calendar.CalendarProviderManager;
import org.drumm.android.calendar.Event;
import org.drumm.android.calendar.ICalendarProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AlarmManagerThread extends Thread {
	private static final boolean VIBRATE_DEFAULT = true;
	private static final int MANUAL_REFRESH_DISPLAY_MILLIS = 1500;
	// private Cursor mCursor;
	private Appendable mAppender;
	private ProgressBar mProgressBar;
	private ContentResolver mContentResolver;
	private Context mContext;
	private boolean mRepeat;
	private long mIntervalTime;

	public AlarmManagerThread(Context context, ProgressBar progressBar,
			Appendable appender) {
		mContext = context;
		mContentResolver = context.getContentResolver();

		mProgressBar = progressBar;
		mAppender = appender;
        if (mAppender==null){
            mAppender=getDefaultAppender();
        }
		mRepeat = false;
		mIntervalTime = 60*3;
		if (progressBar != null) {
			progressBar.setIndeterminate(false);
		}
	}

	@Override
	public void run() {
		Looper.prepare();
		Log.v("AlarmManagerThread","running with " + mRepeat + " " + mIntervalTime);
		do {
            updateAlarmsFromCalendar();
		} while (mRepeat);
		if (mContext instanceof Activity) {
			try {
				Thread.sleep(MANUAL_REFRESH_DISPLAY_MILLIS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((Activity) mContext).finish();
		}
	}

    public void updateAlarmsFromCalendar() {
        int progress = 0;

        setMaxProgress(4);
        setProgress(progress);

        List<Alarm> calendarAlarms = getAlarmsFromCalendar();
        setProgress(++progress);
        List<Alarm> internalAlarms = getInternalAlarms();
        setProgress(++progress);

        int alarmNum = 1;
        Iterator<Alarm> internalItr = internalAlarms.iterator();
        while (internalItr.hasNext()) {
            Alarm alarm = internalItr.next();
            appendText("Alarm #" + alarmNum + "\n");
            String minutes = (alarm.minutes < 10 ? "0" + alarm.minutes
                    : Integer.toString(alarm.minutes));
            String alarmStr = alarm.label + " " + alarm.enabled + " "
                    + alarm.hour + ":" + minutes + "\n";
            appendText(alarmStr);
            boolean delete = true;
            Iterator<Alarm> calendarItr = calendarAlarms.iterator();
            while (calendarItr.hasNext()) {
                Alarm ca = calendarItr.next();
                if (ca.label == null) {
                    ca.label = "";
                }
                if (ca.hour == alarm.hour && ca.minutes == alarm.minutes
                        && ca.label.equals(alarm.label)) {
                    appendText(" found in calendar");
                    delete = false;
                    if (!alarm.enabled) {
                        Alarms.enableAlarm(mContext, alarm.id, true);
                    }
                    calendarItr.remove();
                }
            }
            if (delete && !ActiveAlarmManager.isActive(alarm.id)) {
                internalItr.remove();
                Alarms.deleteAlarm(mContext, alarm.id);
                appendText(" deleted");
            }
            appendText("\n");
            alarmNum++;
        }
        setProgress(++progress);
        alarmNum = 1;
        for (Alarm alarm : calendarAlarms) {
            appendText("Calendar Alarm #" + alarmNum + "\n");
            String minutes = (alarm.minutes < 10 ? "0" + alarm.minutes
                    : Integer.toString(alarm.minutes));
            String alarmStr = alarm.label + " " + alarm.enabled + " "
                    + alarm.hour + ":" + minutes + "\n";
            alarm.silent=false;
            appendText(alarmStr);
            Long newId = Alarms.addAlarm(mContext,alarm);
            Alarms.setAlarm(mContext, alarm);
            String text="created new alarm '"+alarm.label+"'";
            Toast.makeText(mContext,text, Toast.LENGTH_LONG);
            //Alarms.setAlarm(mContext, newId, true, alarm.hour,
            //		alarm.minutes, new DaysOfWeek(0), VIBRATE_DEFAULT,
            //		alarm.label, alarm.label);
            alarmNum++;
        }
        setProgress(++progress);
        if (mRepeat) {
            try {
                Thread.sleep(mIntervalTime * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public long getIntervalTime() {
		return mIntervalTime;
	}

	public void setIntervalTime(long intervalTime) {
		this.mIntervalTime = intervalTime;
	}

	private List<Alarm> getInternalAlarms() {
		Cursor cursor = Alarms.getAlarmsCursor(mContentResolver);
		List<Alarm> a = new ArrayList<Alarm>();
		try {
			while (cursor.moveToNext()) {
				Alarm alarm = new Alarm(cursor);
				a.add(alarm);
			}
		} finally {
			cursor.close();
		}
		return a;
	}

	private List<Alarm> getAlarmsFromCalendar() {
		
		ICalendarProvider prov = new CalendarProviderManager().getCalendarProvider(mContext);

		org.drumm.android.calendar.Calendar cal = prov
				.getCalendarByName("Alarms");
		if (cal == null) {
			return new ArrayList<Alarm>();
		}

		long now = new Date().getTime();
		Collection<Event> events = prov.getEventsFromCalendar(cal, now-DateUtils.HOUR_IN_MILLIS, now
				+ DateUtils.DAY_IN_MILLIS);

		List<Alarm> alarms = new ArrayList<Alarm>();
		for (Event e : events) {
			//if (e.getBegin().after(new Date())) { // if start date has already
													// occurred, don't use this
				appendText("Real Gcal event: " + e.getTitle() + " starts at "
						+ e.getBegin().toLocaleString() + "\n");


                    Alarm alarm=new Alarm(e.getTitle(), e.getBegin().getHours(), e
                        .getBegin().getMinutes());
				alarms.add(alarm);
			//} else {
			//	appendText("Real Gcal event: " + e.getTitle()
			//			+ " starts in past - not using\n");
			//}
		}

		return alarms;
	}

    public void setRepeat(boolean repeat) {
        mRepeat = repeat;
    }

	public boolean getRepeat() {
		return mRepeat;
	}

	private void setProgress(int progress) {
		if (mProgressBar != null) {
			mProgressBar.setProgress(progress);
		}
	}

	private void setMaxProgress(int maxProgress) {
		if (mProgressBar != null) {
			mProgressBar.setMax(maxProgress);
		}
	}

	private void appendText(String string) {
        Log.d("appender",string);
		if (mAppender != null) {
			try {
				mAppender.append(string);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

    public void stopThread() throws InterruptedException {
        mRepeat=false;
        this.wait();
    }

    private Appendable getDefaultAppender(){
        Appendable appender = new Appendable() {

            @Override
            public Appendable append(CharSequence csq, int start, int end) {
                Log.v("TEST",csq.subSequence(start, end).toString());
                return this;
            }

            @Override
            public Appendable append(CharSequence csq) throws IOException {
                Log.v("TEST",csq.toString());
                return this;
            }

            @Override
            public Appendable append(char c) throws IOException {
                Log.v("TEST",Character.toString(c));
                return this;
            }
        };
        return appender;
    }
}
