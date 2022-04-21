package com.example.movementandgps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    TextView locView;
    ToggleButton toggleBut;
    BroadcastReceiver receiver;

    TextView moveView;
    Sensor sensor;
    SensorManager manager;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locView = findViewById(R.id.TextView1);
        moveView = findViewById(R.id.TextView2);
        toggleBut = findViewById(R.id.toggleButton);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //ACCELEROMETER
        manager.registerListener(sensorEL, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        //GPS
        //SET CHECK FOR PERMISSION (ONLY ASK IF NEWLY INSTALLED AND UNTIL YOU ACCEPT)

        toggleBut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

                    onResume();
                }
            }
        });




    }

    //ACCELEROMETER
    SensorEventListener sensorEL= new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
                moveView.setText("X: " + event.values[    0] + "\n"+
                        "Y: " + event.values[1] + "\n"+
                        "Z: " + event.values[2] + "\n");

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null){
            unregisterReceiver(receiver);
        }
        Intent intent = new Intent(getBaseContext(), GPSService.class);
        if(getSystemService(GPSService.class) != null){
            stopService(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getBaseContext(), GPSService.class);
        if(getSystemService(GPSService.class) == null){
            startService(intent);
        }

        if(receiver == null){
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //If Toggle Button is "ENABLED"
                    if(toggleBut.isChecked()){
                        locView.setText("\n     Latitude:   " + intent.getDoubleExtra("Latitude", 0) +
                                "\n Longitude:   " +intent.getDoubleExtra("Longitude",0));
                    }
                    // Else Set to Empty.
                    else{
                        locView.setText("Empty Coordinate Values");
                    }
                }
            };
        }
        registerReceiver(receiver, new IntentFilter("upLocation"));

    }
}