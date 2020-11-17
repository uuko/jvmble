package com.example.jvmble;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MainContract.View{

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    private TextView mStatusBleTv, mPairedTv;
    ImageView mBlueIV;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn,listBtn;
    BluetoothAdapter bluetoothAdapter;
    BleAdapter adapter;
    RecyclerView recyclerView;
    private BluetoothUtil bluetoothUtil;
    private Boolean bleStatus=false;
    private View progress;
    List<BluetoothDevice> deviceList=new ArrayList<>();
    IntentFilter filter;
    private int changeTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothUtil=new BluetoothUtil(this);
        progress=findViewById(R.id.progress);
        listBtn=findViewById(R.id.listAllBtn);
        recyclerView=findViewById(R.id.recycleView);
        mStatusBleTv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairTv);
        mBlueIV = findViewById(R.id.bluetoothIv);
        mOnBtn = findViewById(R.id.onButn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.PairedBtn);
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            mStatusBleTv.setText("Bluetooth is not available");
        } else {
            mStatusBleTv.setText("Bluetooth is  available");


            if (bluetoothAdapter.isEnabled()) {
                bleStatus=true;
                mBlueIV.setImageResource(R.drawable.bluetooth_on);
            } else {
                bleStatus=false;
                mBlueIV.setImageResource(R.drawable.bluetooth_off);
            }
            if ((changeTime>0)&&(changeTime%2==0)){
                listBtn.setEnabled(true);
            }
            else {
                listBtn.setEnabled(false);
            }
            listBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("onccccccccc", "onClick: ");
                    deviceList.clear();
                    recyclerView.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                    registerReceiver(broadcastReceiver, filter);
                }
            });
            mOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    turnOnOffBle();
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bluetoothAdapter.isDiscovering()) {
                        showToast("Making Your Device Discoverable");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivityForResult(intent, REQUEST_DISCOVER_BT);
                    }
                }
            });


            mPairedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                    if (bluetoothAdapter.isEnabled()) {

                        mPairedTv.setText("Paired Devices");
                        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();


                        deviceList.clear();

                        for (BluetoothDevice device : devices) {
                            deviceList.add(device);
//                            mPairedTv.append("\n Device : " + device.getName() + " , " + device);
                        }
                        adapter=new BleAdapter();
                        recyclerView.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setAdapter(adapter);
                        adapter.setList(deviceList);

                    } else {
                        showToast("Turn On bluetooth to get paired devices");
                    }
                }
            });
        }
    }

    public void turnOnOffBle(){
        if (!bleStatus){
            if (!bluetoothAdapter.isEnabled()) {
                showToast("Turning on Bluetooth..");
                changeTime++;
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            } else {
                showToast("Bluetooth is already on");

            }
            mOnBtn.setText("Turn Off");
            bleStatus=true;
        }
        else {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
                changeTime++;
                showToast("Turning  Bluetooth off");
                mBlueIV.setImageResource(R.drawable.bluetooth_off);
            } else {
                showToast("Bluetooth is already off");

            }
            mOnBtn.setText("Turn On");
            bleStatus=false;
        }
        if ((changeTime>0)&&(changeTime%2==0)){
            listBtn.setEnabled(true);
        }
        else {
            listBtn.setEnabled(false);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        if ((changeTime>0)&&(changeTime%2==0)){
            Log.d("onstart", "onStart: ");
            recyclerView.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            registerReceiver(broadcastReceiver, filter);
        }

    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        if ((changeTime>0)&&(changeTime%2==0)){
//            Log.d("onPostResume", "onStart: ");
//            recyclerView.setVisibility(View.GONE);
//            progress.setVisibility(View.VISIBLE);
//            registerReceiver(broadcastReceiver, filter);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(broadcastReceiver);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    mBlueIV.setImageResource(R.drawable.bluetooth_on);
                    showToast("Bluetooth is On");
                } else {
                    showToast("Bluetooth is Off");

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    String a="";
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("aaaaaaaaaaa", "onReceive: ");
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                Log.d("bbbbbbbbbb", "onReceive: ");
                Log.d("aaaaaaaaaaaa", "onReceive: "+a);
                adapter=new BleAdapter();
                recyclerView.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(adapter);
                adapter.setList(deviceList);
                unregisterReceiver(broadcastReceiver);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device);

                a=a+device+"\n";
                Log.d("aaaaa1", "onReceive: "+a);

            }
        }
    };

    @Override
    public void setList(List<BluetoothDevice> list) {

    }
}
