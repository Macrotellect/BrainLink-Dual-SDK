package com.macrotellect.brainlinkdualtest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BluetoothConnectCheck {
    public static final int RC_GPS = 4483;
    public static final int RC_BT = 4484;
    public static final int PER_LOC = 4485;

  public static boolean checkPermissionAndGps(final Activity context){
      //位置权限
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          new AlertDialog.Builder(context)
                  .setTitle("权限")
                  .setMessage("请先授予位置权限")
                  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PER_LOC);
                      }
                  })
                  .setCancelable(false)
                  .show();
          return false;
      }

      //打开gps
      if (!isOPenGps(context)) {
          new AlertDialog.Builder(context)
                  .setTitle("位置服务")
                  .setMessage("请先打开GPS位置服务")
                  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                          context.startActivityForResult(intent, RC_GPS);
                      }
                  })
                  .setCancelable(false)
                  .show();
          return false;
      }

      BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
      if (defaultAdapter == null) {
          return false;
      }
      if (!defaultAdapter.isEnabled()) {
          Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
          context.startActivityForResult(intent, RC_BT);
          return false;
      }
      return  true;
  }
    public static boolean isOPenGps(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }
}
