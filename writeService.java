package com.uasdtu.tript.uas;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class writeService extends Service implements SensorEventListener{


    SensorManager mSensorManager;
    private Sensor gpsSensor, bearingSensor;

    MainActivity get;

    WriteFile writer;

    GPSTracker gps;
    float value;

    String alt_text, bearing_text, lat1, long1, currentDateTimeString;

    public writeService() {
        writer = new WriteFile();
    }

    private static final String TAG = "Background Service";
    private final Handler handler = new Handler();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Background Service Stopped", Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(this);
        handler.removeCallbacks(sendUpdatesToUI);
    }
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {

            currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

            gps = new GPSTracker(writeService.this);

            String bearing, alt;

            if(gps.canGetLocation()){
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                lat1 = Double.toString(latitude);
                long1 = Double.toString(longitude);
                bearing=bearing_text;


                writer.write(currentDateTimeString+" ",lat1+" ",long1+" ",alt_text+" ", bearing_text+" ");
            }

            handler.postDelayed(this, 1000); // 1 seconds
        }
    };
    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Background Service Started", Toast.LENGTH_SHORT).show();

        mSensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gpsSensor  = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        bearingSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mSensorManager.registerListener(this, gpsSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, bearingSensor, SensorManager.SENSOR_DELAY_NORMAL);
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000);//1 second
        Log.d(TAG, "onStart");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_PRESSURE) {
            value = event.values[0];
            //baro.setText("" + value);
            float alt = mSensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, value) - mSensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, get.setReference());
            alt_text = Float.toString(alt);
        } else {
            int value = Math.round(event.values[0]);
            bearing_text = Integer.toString(value);
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
