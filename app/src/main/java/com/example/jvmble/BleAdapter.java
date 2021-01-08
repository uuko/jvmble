package com.example.jvmble;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BleAdapter extends RecyclerView.Adapter<BleAdapter.ViewHolder>  {
    static  UUID uniqueID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    List<BluetoothDevice>list=new ArrayList<>();
    private Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble, parent, false);
        context=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.bind(list.get(position),position);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }


    public void  setList (List<BluetoothDevice> list){
        this.list=list;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    BluetoothDevice device;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bleName;
        TextView bleIp;
        String uuidy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bleName = itemView.findViewById(R.id.bleName);
            bleIp = itemView.findViewById(R.id.bleIp);
        }

        public void bind(final BluetoothDevice list, Integer position) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            bleIp.setText(list.getAddress());
            bleName.setText(list.getName());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    device = myBluetooth.getRemoteDevice(list.getAddress());
                    Log.d("aaaaaaaaaaaa", "onClick: " + list.getAddress());
//                    Intent intent=new Intent(context,ShowDataActivity.class);
//                    intent.putExtra("address",list.getAddress());
                    Intent intent=new Intent(context,FakeActivity.class);
//                    intent.putExtra("address",list.getAddress());
                    context.startActivity(intent);


                }
            });
        }



    }
}
