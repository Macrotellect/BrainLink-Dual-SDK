# Android BrainLinkDual_V1.0 Development Guide

### Introduction

This guide will teach you how to use BrainLinkDual SDK to write Android applications that can acquire brainwave data from Macrotellect 's Hardware.

**Supported Android Version：**

- Android 4.3 +

### Your First Project: : MacrotellectLinkDemo（Android studio）

1.  Copy the SDK to the project's libs folder and add dependencies in build.gradle.

```
       dependencies{
              ...
             
             implementation files('libs/brainlink_dual_V1.0.jar')
       }
       
```

 

2. Add permissions in AndroidMainifest.xml

```
       <uses-permission android:name="android.permission.BLUETOOTH" />
       <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
      <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"      />
       <uses-permission  android:name="android.permission.ACCESS_COARSE_LOCATION" />
       <uses-feature
            android:name="android.hardware.bluetooth_le"
           android:required="true" />
```

3. Receive brainwave data from BrainLinkDual SDK.

```java
 private EEGParse eegParse;

    void initParese() {
        eegParse = new EEGParse();
        eegParse.initNotChFreq(50);//Set notch frequency
        eegParse.setNotchFreq(true); //Whether to enable notch filtering
        eegParse.setOnSignCallBack(new OnSignCallBack() {
            @Override
            public void onSign(int i) {
                Log.e(TAG, "onSign: "+ i );
            }
        });
        eegParse.setOnDataCallBack(new OnDataCallBack() {
            @Override
            public void onFrequencyData(float[][] floats) {
                Log.e(TAG, "onFrequencyData: Left channel  "  + Arrays.toString(floats[0]));
                Log.e(TAG, "onFrequencyData: Right channel  "  + Arrays.toString(floats[1]));

            }

            @Override
            public void onEEgData(float[][] eegdatas) {
                Log.e(TAG, "onEEgData: Left channel  delta:"  + eegdatas[0][0] + "  theta:" + eegdatas[0][1] + "  alpha:" + eegdatas[0][2]+ "  smr:" + eegdatas[0][3]+ "  midBeta:" + eegdatas[0][4]+ "  highBeta:" + eegdatas[0][5]+ "  gamma:" + eegdatas[0][6]+ "  beta:" + eegdatas[0][7]+ "  sum:" + eegdatas[0][8]+ "  max:" + eegdatas[0][9]);
                Log.e(TAG, "onEEgData: Right channel  delta:"  + eegdatas[0][0] + "  theta:" + eegdatas[0][1] + "  alpha:" + eegdatas[0][2]+ "  smr:" + eegdatas[0][3]+ "  midBeta:" + eegdatas[0][4]+ "  highBeta:" + eegdatas[0][5]+ "  gamma:" + eegdatas[0][6]+ "  beta:" + eegdatas[0][7]+ "  sum:" + eegdatas[0][8]+ "  max:" + eegdatas[0][9]);
            }

            @Override
            public void onBrainData(int attLeft, int attRight, int medLeft, int medRight) {
                Log.e(TAG, "onBrainData: "+ attLeft+ "," +attRight +"    " + medLeft + "," + medRight);
            }
        });
    }



void connectBluetooth(){
    ...
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
                                          
                                        }

                                        @Override
                                        public void onCharacteristicChanged(byte[] data) {
                                            //Call this interface  where Bluetooth data is received
                                            eegParse.paring(data,data.length);
                                        }
                                    });
                        }
    
    ...
}





```





## MacrotellectLink  API Reference

## EEGParse Reference

Brain wave data analysis class, incoming raw brain wave data obtained in real time and return detailed brain wave information

### Method

**void initNotChFreq(float notch_freq)**

Set notch frequency

- notch_freq   notch frequency

**void setNotchFreq(boolean notchFreq)**

Turn on/off notch filtering

- notch_freq    **true:** enable notch filtering   **fasle:** does not enable filtering

**setOnSignCallBack(OnSignCallBack onSignCallBack)**

Set signal quality callback

- onSignCallBack   Signal quality callback interface

**setOnRawDataCallBack(OnRawDataCallBack onRawDataCallBack)**

Set Raw data callback

- onRawDataCallBack   Raw Data CallBack interface

**setOnDataCallBack(OnDataCallBack onDataCallBack)**

Set brainwave data callback

- onRawDataCallBack  Brainwave data callback interface



## OnSignCallBack Reference

Signal quality callback interface

### Method

**void onSign(int sign)**

get Sign quality 

- sign   Signal quality rangera 0 -100 



## OnRawDataCallBack Reference

Raw data CallBack Interface

**onRawdata(int left , int right)**

Obtain left and right brain raw data

- left  left raw data
- right  right raw data



## OnDataCallBack Reference

Brainwave data callback interface

**`void onFrequencyData(float[][]  frequencyData)`**

Obtain the amplitude of the left and right channels every 0.5 frequency interval

- frequencyData  amplitude,  Arrays size  ` float[2][140]`
	- frequencyData[0]  is left channel data ，frequencyData[1]  is right channel data
	- `frequencyData[0][0],frequencyData[0][1] ...frequencyData[0][140] ` is Amplitude of   0-0.5 Hz ，0.5 - 1Hz ... 69.5 - 70  frequency

**`void onEEgData(float[][] eegdata)` **

get EEG data

eegdata   Arrays size `float[2][10]`

- -  eegdata[0]  is left channel data ，eegdata[1]  is right channel data
	-  The size of the outer array is 10 `eegdata[0][0]` -  `eegdata[0][9]` Brainwave data in turn： delta ，theta,alpha,smr,midBeta,highBeta,Gamma,beta,sum(0-50 Hz sum) ，max(0-8 Maximum value in data). The first 8 data correspond to the frequency interval in order (unit Hz):   0.5 - 4 , 4 - 8 , 8 - 13 , 12 - 15 , 15 - 20 , 20 - 30 , 30 - 50 , 13 - 30  

**void onBrainData(int leftAtt, int rightAtt, int leftMed, int rightMed)**

Get the concentration and relaxation of the left and right channels

- leftAtt  Left channel concentration
- rightAtt Right channel concentration
- leftMed  Left channel relaxation
- rightMed  Right channel relaxation