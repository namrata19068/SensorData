package com.example.sensordata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import android.database.sqlite.SQLiteDatabase;

public class MT19068_AudioRecorder extends AppCompatActivity {

    Button btnRecord,btnStopRecord,btnPlay,btnStop;
    String pathSave="";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    TextView textView;
    private SQLiteDatabase sqLiteDatabase;

    final int REQUEST_PERMISSION_CODE=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19068_activity_audio_recorder);
///////////////////////////////////////////////////////////////////////////////////////////////////////
        textView = (TextView) findViewById(R.id.textAudio);
        sqLiteDatabase=openOrCreateDatabase("audio",0,null);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+"accelerometer");
        sqLiteDatabase.execSQL(
                "create table if not exists " + "audio" +
                        "(audio_file blob,timestamp varchar(50))"
        );


        sqLiteDatabase.execSQL("Delete from audio");
        /////////////////////////////////////////////////////////////////////////////////////////////

        if(!checkPermissionFromDevice())
            requestPermission();

        btnPlay=(Button)findViewById(R.id.btnPlay);
        btnRecord=(Button)findViewById(R.id.btnStartRecord);
        btnStop=(Button)findViewById(R.id.btnStop);
        btnStopRecord=(Button)findViewById(R.id.btnStopRecord);




            btnRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(checkPermissionFromDevice()){


                    pathSave= Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+
                            UUID.randomUUID().toString()+"_audio_record.3gp";

                    //Sets up the media recorder########
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
////////////////////////////////////////////////////////////////////////////////////////////////////
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(pathSave);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        byte[] buffer =new byte[1024];
                        int read=0;
                        while (true) {
                            try {
                                if (!((read = fis.read(buffer)) != -1)) break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            baos.write(buffer, 0, read);
                        }
                        try {
                            baos.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] fileByteArray = baos.toByteArray();



                        ContentValues cv = new ContentValues();
                        cv.put("filename", pathSave);
                        cv.put("blob", fileByteArray);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        String format = simpleDateFormat.format(new Date());
                        sqLiteDatabase.execSQL("insert into audio values('"+cv+"','"+format+"' )");
////////////////////////////////////////////////////////////////////////////////////////////////////
                    Toast.makeText(MT19068_AudioRecorder.this,"Recording....",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        requestPermission();
                    }
                }
            });

            btnStopRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaRecorder.stop();
                    btnStopRecord.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnRecord.setEnabled(true);
                    btnStop.setEnabled(false);
                }
            });

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnStop.setEnabled(true);
                    btnStopRecord.setEnabled(false);
                    btnRecord.setEnabled(false);


                    mediaPlayer= new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    Toast.makeText(MT19068_AudioRecorder.this,"Playing....",Toast.LENGTH_SHORT).show();
                }
            });


            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnStopRecord.setEnabled(false);
                    btnRecord.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnPlay.setEnabled(true);

                    if(mediaPlayer!= null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        setupMediaRecorder();
                    }
                }
            });

             Button fetch= (Button) findViewById(R.id.fetchAudio);
             fetch.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     fetchAccData();
                 }
             });

    }
        private void setupMediaRecorder()
        {
            mediaRecorder= new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(pathSave);
        }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result= ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result== PackageManager.PERMISSION_GRANTED &&
                record_audio_result== PackageManager.PERMISSION_GRANTED;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void fetchAccData()
    {
        Cursor cursor=sqLiteDatabase.rawQuery("select * from audio",null);
        while(cursor.moveToNext())
        {
            textView.append("\n"+"X: "+cursor.getString(0).toString()+" y: "+cursor.getString(1).toString() +" timestamp: "+cursor.getString(2).toString());
        }
    }
}
