package com.example.sensordata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MT19068_MainActivity extends AppCompatActivity {

    private Button audioButton;
    private Button acclButton;
    private Button wifiButton;
    private Button gpsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19068_activity_main);

        audioButton=(Button)findViewById(R.id.audio);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(MT19068_MainActivity.this, MT19068_AudioRecorder.class);
                startActivity(i);
            }
        });

        acclButton=(Button)findViewById(R.id.Accelerometer);
        acclButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a= new Intent(MT19068_MainActivity.this, MT19068_Accelerometer_Activity.class);
                startActivity(a);
            }
        });

        wifiButton=(Button)findViewById(R.id.wifi);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent w= new Intent(MT19068_MainActivity.this, MT19068_Wifi_Activity.class);
                startActivity(w);
            }
        });

        gpsButton=(Button) findViewById(R.id.gps);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent g= new Intent(MT19068_MainActivity.this, MT19068_GPS_Activity.class);
                startActivity(g);
            }
        });
    }
}
