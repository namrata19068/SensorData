package com.example.sensordata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MT19068_Accelerometer_Activity extends AppCompatActivity  implements SensorEventListener {

    private float lastX, lastY, lastZ;
    private TextView textView;
    private Button button1;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private Vibrator v;
    private float vibrateThreshold = 0;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView currentX, currentY, currentZ;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19068_activity_accelerometer_);
        initializeViews();
        createfunc();
        button1 = (Button)findViewById(R.id.fetch);
        textView = (TextView) findViewById(R.id.fetchtext);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAccData();
            }
        });

    }



    private void createfunc() {
        sqLiteDatabase=openOrCreateDatabase("accelerometer",0,null);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+"accelerometer");
        sqLiteDatabase.execSQL(
                "create table if not exists " + "accelerometer" +
                        "(x varchar(50), y varchar(50), z varchar(50), timestamp varchar(50))"
        );

        //sqLiteDatabase
        //      .execSQL("create table if not exists "+
        //            "accelerometer(x varchar(50),y varchar(50),z varchar(50),id varchar(50))");
        sqLiteDatabase.execSQL("Delete from accelerometer");


    }
    public void fetchAccData()
    {
        Cursor cursor=sqLiteDatabase.rawQuery("select * from accelerometer",null);
        while(cursor.moveToNext())
        {
            textView.append("\n"+"X: "+cursor.getString(0).toString()+" y: "+cursor.getString(1).toString()+" z: "+cursor.getString(2).toString() +" timestamp: "+cursor.getString(3).toString());
        }
    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - sensorEvent.values[0]);
        deltaY = Math.abs(lastY - sensorEvent.values[1]);
        deltaZ = Math.abs(lastZ - sensorEvent.values[2]);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        sqLiteDatabase.execSQL("insert into accelerometer values('"+sensorEvent.values[0]+"','"+sensorEvent.values[1]+"','"+sensorEvent.values[2]+"','"+format+"')");// if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
    }
    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText( Float.toString(deltaX));
        currentY.setText( Float.toString(deltaY));
        currentZ.setText( Float.toString(deltaZ));
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}