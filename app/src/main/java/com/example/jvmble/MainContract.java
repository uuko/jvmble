package com.example.jvmble;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface MainContract {
    interface View {
        void setList(List<BluetoothDevice> list);
    }
}
