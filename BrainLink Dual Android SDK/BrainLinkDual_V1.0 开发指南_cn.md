# Android BrainLinkDual_V1.0 开发指南

### 介绍

本指南将教你如何使用BrainLinkDual从宏智力公司的双通道硬件中获取脑电波数据。这将使您的Android应用程序能够接收和使用脑波数据，你可以通过蓝牙，宏智力公司的硬件和BrainLinkDual SDK来获取它们。

**支持的Android版本：**

- Android 4.3 +

### 你的第一个项目: MacrotellectLinkDemo（Android studio）

1. 将 sdk 复制到项目的libs 文件夹中，并在build.gradle 中添加依赖。

```
       dependencies{
              ...
             
             implementation files('libs/brainlink_dual_V1.0.jar')
       }
       
```

 

2. 在AndroidMainifest.xml中添加权限

```
       <uses-permission android:name="android.permission.BLUETOOTH" />
       <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
      <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"      />
       <uses-permission  android:name="android.permission.ACCESS_COARSE_LOCATION" />
       <uses-feature
            android:name="android.hardware.bluetooth_le"
           android:required="true" />
```

3. 调用sdk 获取数据

```java
 private EEGParse eegParse;

    void initParese() {
        eegParse = new EEGParse();
        eegParse.initNotChFreq(50);//设置陷波频率
        eegParse.setNotchFreq(true); //是否启用陷波过滤
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
                                            //在接收蓝牙数据的地方调用该接口，传入接收到的字节数据
                                            eegParse.paring(data,data.length);
                                        }
                                    });
                        }
    
    ...
}





```





## MacrotellectLink  API 参考

## EEGParse 参考

脑电波数据解析类，传入实时得到的脑电原始数据并返回详细的脑波信息

### Method

**void initNotChFreq(float notch_freq)**

设置陷波频率

- notch_freq 陷波频率

**void setNotchFreq(boolean notchFreq)**

开启/关闭陷波过滤

- notch_freq  true 开启陷波过滤 fasle 不开启过滤

**setOnSignCallBack(OnSignCallBack onSignCallBack)**

设置信号质量回调

- onSignCallBack 信号质量回调接口

**setOnRawDataCallBack(OnRawDataCallBack onRawDataCallBack)**

设置电位数据回调

- onRawDataCallBack 电位数据回调接口

**setOnDataCallBack(OnDataCallBack onDataCallBack)**

设置脑波数据回调

- onRawDataCallBack 脑波数据回调接口



## OnSignCallBack 参考

信号质量回调接口

### Method

**void onSign(int sign)**

获取信号质量

- sign 信号质量 0 -100 



## OnRawDataCallBack 参考

实时电位回调接口

**onRawdata(int left , int right)**

获取左右脑电位数据

- left  左脑电位数据
- right 右脑电位数据



## OnDataCallBack 参考

脑波数据回调接口

**`void onFrequencyData(float[][]  frequencyData)`**

获取左右通道每0.5个频率区间的振幅大小

- frequencyData 振幅二维数组 大小`float[2][140]`
	- frequencyData[0] 表示左通道数据 ，frequencyData[1] 右通道数据
	- `frequencyData[0][0],frequencyData[0][1] ...frequencyData[0][140] ` 表示  0-0.5 Hz ，0.5 - 1Hz ... 69.5 - 70的振幅

**`void onEEgData(float[][] eegdata)` **

获取eeg数据

- eegdata 二维数组 大小 `float[2][10]`
	-  eegdata[0] 表示左通道数据 ，eegdata[1] 右通道数据
	-  外层数组大小为10 `eegdata[0][0]` -  `eegdata[0][9]` 依次表示脑波数据： delta ，theta,alpha,smr,midBeta,highBeta,Gamma,beta,sum(0-50 Hz 总和) ，max(0-8数据中的最大值)。前8个数据对应频率区间依次为(单位 Hz)   0.5 - 4 , 4 - 8 , 8 - 13 , 12 - 15 , 15 - 20 , 20 - 30 , 30 - 50 , 13 - 30  

**void onBrainData(int leftAtt, int rightAtt, int leftMed, int rightMed)**

获取左右通道的专注度和放松度

- leftAtt 左通道专注度
- rightAtt 右通道专注度
- leftMed 左通道放松度
- rightMed 右通道放松度