package com.example.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.databinding.DataBindingUtil;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import android.util.Log;

import com.example.myapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    Vector<AccessPoint> accessPoints;
    LinearLayoutManager linearLayoutManager;
    AccessPointAdapter accessPointAdapter;
    WifiManager wifiManager;
    List<ScanResult> scanResult;
    ActivityMainBinding binding;
    /* Location permission 을 위한 필드 */
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    // 원하는 권한을 배열로 넣어줍니다.
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    /* Location permission 을 위한 필드 */

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.accessPointRecyclerView.setLayoutManager(linearLayoutManager);
        accessPoints = new Vector<>();
        if (wifiManager != null) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(mWifiScanReceiver, filter);
            wifiManager.startScan();
        }
    }


    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    getWIFIScanResult();
                    wifiManager.startScan();
                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    context.sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
                }

            }

        }
    };

    public void getWIFIScanResult() {
        scanResult = wifiManager.getScanResults();
        if (accessPoints.size() != 0) {
            accessPoints.clear();
        }
        for (int i = 0; i < scanResult.size(); i++) {
            ScanResult result = scanResult.get(i);
            if (result.frequency < 3000) {
                Log.d(". SSID : " + result.SSID,
                        result.level + ", " + result.BSSID);
                accessPoints.add(new AccessPoint(result.SSID, result.BSSID, String.valueOf(result.level)));
            }
        }
        accessPointAdapter = new AccessPointAdapter(accessPoints, MainActivity.this);
        binding.accessPointRecyclerView.setAdapter(accessPointAdapter);
        accessPointAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiScanReceiver);
    }

    /* Location permission 을 위한 메서드들 */
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted");
                }
            }
        }
    }

}
