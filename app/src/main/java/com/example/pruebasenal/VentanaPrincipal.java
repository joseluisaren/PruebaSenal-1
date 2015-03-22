package com.example.pruebasenal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class VentanaPrincipal extends Activity {

    private BluetoothAdapter bTAdapter ;
    TextView rssi_msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interfaz_ventana_principal);

        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        bTAdapter = BluetoothAdapter.getDefaultAdapter();

        rssi_msg = (TextView) findViewById(R.id.textView);

    }

    public void buscar(View v){
        rssi_msg.setText("");
        bTAdapter.startDiscovery();
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                rssi_msg.setText(rssi_msg.getText() + name + " => " + rssi + "dBm\n");
            }
            else
                Toast.makeText(VentanaPrincipal.this,"No hay dispositivos",Toast.LENGTH_LONG).show();
        }
    };



}
