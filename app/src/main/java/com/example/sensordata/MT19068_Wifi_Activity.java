package com.example.sensordata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MT19068_Wifi_Activity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private  int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mt19068_activity_wifi_);

        buttonScan = findViewById(R.id.scanbtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanWifi();

            }
        });

        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()){
            Toast.makeText(this,"Wifi is disabled! Please turn on wifi",Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);
        scanWifi();

    }

    private  void scanWifi(){
        arrayList.clear();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this,"Scanning Wifi!..",Toast.LENGTH_SHORT).show();


    }
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results){

                // arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities);
                //adapter.notifyDataSetChanged();
                int rssi = wifiManager.getConnectionInfo().getRssi();
                int level1 = WifiManager.calculateSignalLevel(rssi, 5);
                int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                // System.out.println("Level is " + level + " out of 5");

                if (level <= 0 && level >= -50) {
                    //Best signal
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+" Excellent");
                    adapter.notifyDataSetChanged();
                    //System.out.println(level);

                } else if (level < -50 && level >= -70) {
                    //Good signal
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+"Good Signal");
                    adapter.notifyDataSetChanged();
                    //System.out.println("Strength"+level);



                } else if (level < -70 && level >= -80) {
                    //Low signal
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+"Low Signal");
                    adapter.notifyDataSetChanged();
                    //System.out.println("Strength"+level);


                } else if (level < -80 && level >= -100) {
                    //Very weak signal
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+"Very Weak Signal");
                    adapter.notifyDataSetChanged();
                    //System.out.println("Strength"+level);


                } else {
                    // no signals
                    arrayList.add(scanResult.SSID +"-"+ scanResult.capabilities+"No signal");
                    adapter.notifyDataSetChanged();
                    //System.out.println("Strength"+level);
                }
            }
            int rssi = wifiManager.getConnectionInfo().getRssi();
            int level = WifiManager.calculateSignalLevel(rssi, 5);
            //System.out.println("Level is " + level + " out of 5");
        }
    };
}