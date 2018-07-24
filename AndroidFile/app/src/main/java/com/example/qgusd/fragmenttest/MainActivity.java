package com.example.qgusd.fragmenttest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    MainFragment mainFragment;

    private BluetoothAdapter mBluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        Log.e("Frag", "Fragment");
///////////////////////////////여기까지 fragment 만든거

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {

            Toast.makeText(this, "This app requires a bluetooth capable phone", Toast.LENGTH_SHORT).show();
            finish();
        }
///////////////////////폰에 블루투스 모듈이 없으면 null 리턴, toast로 에러메시지 표시 후 앱 종료
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        if (!mBluetoothAdapter.isEnabled()) {//블루투스가 활성화 되어있지않으면
            //intent를 사용해 황성화 시키도록 요청
            //앱은 백그라운드로 들어가고 사용자에게 앱이 블루투스를 활성화 시키도록 묻는 팝업 창
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else listPairedDevices();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check if the result comes from the request to enable bluetooth
        if(requestCode == REQUEST_ENABLE_BT)
            // the request was successful? if so, display paired devices
            if(resultCode == RESULT_OK) listPairedDevices();

            // if not, display a toast message and close the app
            else {
                Toast.makeText(this, "This app requires bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            }

            super.onActivityResult(requestCode, resultCode, data);
    }
    //사용자가 request를 허가(또는 거부)하면 안드로이드는 앱의 onActivityResult 메소드가 호출되어서 request의 허가/거부를 확인할 수 있다.


    private void listPairedDevices() {

        // get the paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // Get the ListView element
        ListView listView1 = (ListView)findViewById(R.id.listView1);

        // Add the paired devices to an ArrayList
        ArrayList<String> arrayList1 = new ArrayList<>();
        for(BluetoothDevice pairedDevice : pairedDevices)
            arrayList1.add(pairedDevice.getName());

        // Create an array adapter for the ListView
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, R.layout.paired_device_row, arrayList1);
        listView1.setAdapter(arrayAdapter1);

        // Add a click listener
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Get the element clicked and send Hello world to it
                String listElement = (String)parent.getItemAtPosition(position);
                sayHelloToDevice(listElement);

            }
        });
    }
    ///////////////////////paird devices 를 listView에 넣어서 표시
    ///////////////////////onItemClickListener를 사용하여서 sayHelloDevice메서드 호출하도록

    private void sayHelloToDevice(String deviceName) {

        UUID SPP_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        // Get the Bluetooth device with the given name
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice targetDevice = null;
        for(BluetoothDevice pairedDevice : pairedDevices)
            if(pairedDevice.getName().equals(deviceName)) {
                targetDevice = pairedDevice;
                break;
            }

        // If the device was not found, toast an error and return
        if(targetDevice == null) {
            Toast.makeText(this, "Device not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a connection to the device with the SPP UUID
        BluetoothSocket btSocket = null;
        try {
            btSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
        } catch (IOException e) {
            Toast.makeText(this, "Unable to open a serial socket with the device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Connect to the device
        try {
            btSocket.connect();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to connect to the device", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OutputStreamWriter writer = new OutputStreamWriter(btSocket.getOutputStream());
            writer.write("Hello world!\r\n");
            writer.flush();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to send message to the device", Toast.LENGTH_SHORT).show();
        }

        try {
            btSocket.close();
            Toast.makeText(this, "Message successfully sent to the device", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to close the connection to the device", Toast.LENGTH_SHORT).show();
        }
    }
    ///////////listView 누루면 그 디바이스로 Hello World String 전달
    //////////////////////////////////////
}
