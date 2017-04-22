package com.uasdtu.tript.uas;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView date_time, Lat, Long, baro, bearing;

    GPSTracker gps;

    //WriteFile writeFile;

    Button reference;

    private Sensor gpsSensor, bearingSensor;
    private SensorManager mSensorManager;

    String  alt_text, bearing_text;
    String lat1,long1;

    float value, ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        date_time = (TextView) findViewById(R.id.DateTime);
        Lat = (TextView) findViewById(R.id.Lat);
        Long = (TextView) findViewById(R.id.Long);
        baro = (TextView) findViewById(R.id.Baro);
        bearing = (TextView) findViewById(R.id.Bearing);

        reference = (Button) findViewById(R.id.set_reference);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mSensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gpsSensor  = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        bearingSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);



        /*Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                // textView is the TextView view that should display it
                date_time.setText(currentDateTimeString);

                gps = new GPSTracker(MainActivity.this);

                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    String lat1 = Double.toString(latitude);
                    Lat.setText(lat1);
                    String long1 = Double.toString(longitude);
                    Long.setText(long1);

                }

                write(currentDateTimeString+" ",Lat.getText().toString()+" ",Long.getText().toString()+" ",baro.getText().toString());
                }
        }, 0, 1000);
        */

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                // textView is the TextView view that should display it
                                date_time.setText(currentDateTimeString);

                                gps = new GPSTracker(MainActivity.this);

                                if(gps.canGetLocation()){
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();

                                    lat1 = Double.toString(latitude);
                                    Lat.setText(lat1);
                                    long1 = Double.toString(longitude);
                                    Long.setText(long1);

                                }

                                reference.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ref = value;
                                    }
                                });

                                baro.setText(alt_text);
                                bearing.setText(bearing_text);

                                //writeFile = new WriteFile();

                                //writeFile.write(currentDateTimeString+" ",lat1+" ",long1+" ",alt_text+" ", bearing_text+" ");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    public float  setReference(){
        return ref;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if(sensor.getType() == Sensor.TYPE_PRESSURE) {
            value = event.values[0];
            //baro.setText("" + value);
            float alt = mSensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, value) - mSensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, ref);
            alt_text = Float.toString(alt);
        }
        else{
            int value = Math.round(event.values[0]);
            bearing_text = Integer.toString(value);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, gpsSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, bearingSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }

    /*public void write(String str1,String str2,String str3,String str4,String str5) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write(str1.getBytes());
            fileOutputStream.write(str2.getBytes());
            fileOutputStream.write(str3.getBytes());
            fileOutputStream.write(str4.getBytes());
            fileOutputStream.write(str5.getBytes());
            fileOutputStream.write("\n".getBytes());

            try {
                fileOutputStream.close();
            }catch (IOException e) {e.printStackTrace();}
            //Toast.makeText(getApplicationContext(), "Message Saved",Toast.LENGTH_SHORT ).show();

        }catch (IOException e){e.printStackTrace();
            Log.e("error","1");}


    }*/

    public void startIt(View view){
        startService(new Intent(getBaseContext(),writeService.class));
    }

    public void stopIt(View view){
        stopService(new Intent(getBaseContext(),writeService.class));
    }
}