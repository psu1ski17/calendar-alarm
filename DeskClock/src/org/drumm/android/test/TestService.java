package org.drumm.android.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.drumm.android.alarms.AlarmManagerThread;

import java.io.IOException;

/**
 * Created by kdrumm on 11/13/2014.
 */
public class TestService extends Service{

    private final long runTimeMillis;
    public TestService(){
        this.runTimeMillis=200000;
    }
    public TestService(long runTimeMillis){
        this.runTimeMillis=runTimeMillis;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test","Test Service Started");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d("test","Test Service Created");
    }

    @Override
    public void onDestroy() {
        Log.d("test","Test Service Destroyed");
    }
}
