package com.macrotellect.brainlinkdualtest;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.macrotellect.gs5001.EEGParse;
import com.macrotellect.gs5001.callBack.OnDataCallBack;
import com.macrotellect.gs5001.callBack.OnSignCallBack;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivitiy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBle();
        initParese();
        connectBluetooth();
    }

    void initBle() {
        BleManager.getInstance().init(getApplication());
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
//                .setServiceUuids()      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "BRAINLINK_DUAL", "brain radar")
//                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(0)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

    }

    void connectBluetooth() {
        if (BluetoothConnectCheck.checkPermissionAndGps(this)) {
            BleManager.getInstance()
                    .scanAndConnect(new BleScanAndConnectCallback() {
                        @Override
                        public void onScanFinished(BleDevice scanResult) {

                        }

                        @Override
                        public void onStartConnect() {

                        }

                        @Override
                        public void onConnectFail(BleDevice bleDevice, BleException exception) {
                            Log.e(TAG, "onConnectFail: " );
                            connectBluetooth();
                        }

                        @Override
                        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                            Log.e(TAG, "onConnectSuccess: ");

                            BleManager.getInstance().notify(bleDevice,
                                    "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                                    "6e400003-b5a3-f393-e0a9-e50e24dcca9e",
                                    new BleNotifyCallback() {
                                        @Override
                                        public void onNotifySuccess() {

                                        }

                                        @Override
                                        public void onNotifyFailure(BleException exception) {
                                            Log.e(TAG, "onNotifyFailure: " + exception.getDescription());
                                            connectBluetooth();
                                        }

                                        @Override
                                        public void onCharacteristicChanged(byte[] data) {
                                            eegParse.paring(data,data.length);
                                        }
                                    });
                        }

                        @Override
                        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                        }

                        @Override
                        public void onScanStarted(boolean success) {

                        }

                        @Override
                        public void onScanning(BleDevice bleDevice) {

                        }
                    });
        }

    }

    private EEGParse eegParse;

    void initParese() {
        eegParse = new EEGParse();
        eegParse.initNotChFreq(50);
        eegParse.setNotchFreq(true);
        eegParse.setOnSignCallBack(new OnSignCallBack() {
            @Override
            public void onSign(int i) {
                Log.e(TAG, "onSign: "+ i );
            }
        });
        eegParse.setOnDataCallBack(new OnDataCallBack() {
            @Override
            public void onFrequencyData(float[][] floats) {
                Log.e(TAG, "onFrequencyData: 左通道  "  + Arrays.toString(floats[0]));
                Log.e(TAG, "onFrequencyData: 右通道  "  + Arrays.toString(floats[1]));

            }

            @Override
            public void onEEgData(float[][] eegdatas) {
                Log.e(TAG, "onEEgData: 左通道  delta:"  + eegdatas[0][0] + "  theta:" + eegdatas[0][1] + "  alpha:" + eegdatas[0][2]+ "  smr:" + eegdatas[0][3]+ "  midBeta:" + eegdatas[0][4]+ "  highBeta:" + eegdatas[0][5]+ "  gamma:" + eegdatas[0][6]+ "  beta:" + eegdatas[0][7]+ "  sum:" + eegdatas[0][8]+ "  max:" + eegdatas[0][9]);
                Log.e(TAG, "onEEgData: 右通道  delta:"  + eegdatas[0][0] + "  theta:" + eegdatas[0][1] + "  alpha:" + eegdatas[0][2]+ "  smr:" + eegdatas[0][3]+ "  midBeta:" + eegdatas[0][4]+ "  highBeta:" + eegdatas[0][5]+ "  gamma:" + eegdatas[0][6]+ "  beta:" + eegdatas[0][7]+ "  sum:" + eegdatas[0][8]+ "  max:" + eegdatas[0][9]);
            }

            @Override
            public void onBrainData(int attLeft, int attRight, int medLeft, int medRight) {
                Log.e(TAG, "onBrainData: "+ attLeft+ "," +attRight +"    " + medLeft + "," + medRight);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == BluetoothConnectCheck.PER_LOC) {
            connectBluetooth();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothConnectCheck.RC_GPS || requestCode == BluetoothConnectCheck.RC_BT) {
            connectBluetooth();
        }
    }

}
