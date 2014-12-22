package org.drumm.android.calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.drumm.android.alarms.AlarmManagerThread;

import java.io.IOException;

public class CalendarMonitorServiceThread extends Service {
    private static final long DEFAULT_INTERVAL_SECS = 300;
    private AlarmManagerThread thread;
    private long mIntervalSecs = DEFAULT_INTERVAL_SECS;

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
        thread = new AlarmManagerThread(this, null, appender);
        thread.setName("CalendarMonitor");
        thread.setRepeat(true);
        thread.setIntervalTime(300);
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "created service", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "destroyed service", Toast.LENGTH_SHORT).show();
        thread.setRepeat(false);
    }

    public static void startCalendarMonitorService(Context context) {
        Intent serviceIntent = new Intent(context, CalendarMonitorServiceThread.class);
        Toast.makeText(context, CalendarMonitorServiceThread.class.toString(),
                Toast.LENGTH_SHORT).show();
        context.startService(serviceIntent);
    }
}
