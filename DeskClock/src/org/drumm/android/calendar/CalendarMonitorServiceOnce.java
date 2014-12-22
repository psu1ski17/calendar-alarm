package org.drumm.android.calendar;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import org.drumm.android.alarms.AlarmManagerThread;

import java.io.IOException;

import static android.app.AlarmManager.RTC_WAKEUP;

public class CalendarMonitorServiceOnce extends Service {
    public static final long DEFAULT_INTERVAL_MILLIS = 1000*60*5;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "started service", Toast.LENGTH_SHORT).show();
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
        AlarmManagerThread thread = new AlarmManagerThread(this, null, appender);
        thread.updateAlarmsFromCalendar();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "created service", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "destroyed service", Toast.LENGTH_SHORT).show();
        //thread.setRepeat(false);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void startCalendarMonitorService(Context context, long intervalMillis) {

        //new AlarmManagerThread(context,null,null ).updateAlarmsFromCalendar();

        Intent serviceIntent = new Intent(context, CalendarMonitorServiceOnce.class);
        AlarmManager mgr= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.setInexactRepeating(RTC_WAKEUP, 0, intervalMillis, PendingIntent.getService(context, 1, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
