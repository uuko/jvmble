package com.example.jvmble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BluetoothUtil {
    private EndBluetoothListener listener;
    private Context context;
    private  List<BluetoothDevice> bluetoothDeviceList=new ArrayList<>();

    public BluetoothUtil(Context context) {
        this.context=context;
    }

    public void getAllBlueDeviceList(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(broadcastReceiver, filter);
       listener=new EndBluetoothListener() {
                    @Override
                    public void endBleBroadcast() {

                    }
                };


    }

    interface EndBluetoothListener{
        void endBleBroadcast();
    }
    private String a="";
    public  BroadcastReceiver broadcastReceiver = new BroadcastReceiver( ) {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("aaaaaaaaaaa", "onReceive: ");
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Log.d("aaaaaaabb", "onReceive: ");
                listener.endBleBroadcast();


            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDeviceList.add(device);
                a=a+device+"\n";
                Log.d("aaaaaaaa", "onReceive: "+a);

            }
        }
    };
}
