package com.example.jvmble;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.example.jvmble.BleAdapter.uniqueID;

public class ShowDataActivity extends AppCompatActivity {

    private View startduty;
    private View endduty;
    private String address;
    private EditText editText;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        address=getIntent().getStringExtra("address");
        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        new ConnectBT(this,address).execute();
        init();
    }

    private void init() {
        startduty=findViewById(R.id.startDuty);
        endduty=findViewById(R.id.endDuty);
        editText=findViewById(R.id.editText);
        startduty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((editText.getText().length()>0) && ( Pattern.compile("[a-zA-Z]*")
                        .matcher(editText.getText()).matches())){
                    sendOner();
                }
                else {
                    Toast.makeText(ShowDataActivity.this, "請輸入字串", Toast.LENGTH_SHORT).show();
                }

            }
        });

        endduty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendZeroner();
            }
        });
    }

    private void sendOner()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("1".toString().getBytes());
                String personName=editText.getText().toString();
                btSocket.getOutputStream().write(personName.getBytes());
                disconnect();
            }
            catch (IOException e)
            {
                Log.d("error", "sendOner: "+e);
            }
        }
    }

    private void sendZeroner()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("0".toString().getBytes());
                String personName=editText.getText().toString();
                btSocket.getOutputStream().write(personName.getBytes());
                disconnect();
            }
            catch (IOException e)
            {
                Log.d("error", "sendOner: "+e);
            }
        }
    }

    BluetoothSocket btSocket = null;
    ProgressDialog progress;

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        private Context context;
        private BluetoothAdapter myBluetooth;
        private String address;

        public ConnectBT(Context context, String address
        ) {
            this.context = context;
            this.address = address;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(uniqueID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                Log.d("aaaaaaaaaaaaaaaaa", "doInBackground: " + e);
               // Toast.makeText(context, "藍芽錯誤"+e, Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (!ConnectSuccess) {
//                    msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
//                    finish();
            } else {
                Log.d("aaaa", "onPostExecute: ");

//                    msg("Connected.");
//                    isBtConnected = true;
            }
            //   progress.dismiss();
        }
    }

    private void timer(){

    }
    private void disconnect() throws IOException {
        btSocket.close();
    }
}
