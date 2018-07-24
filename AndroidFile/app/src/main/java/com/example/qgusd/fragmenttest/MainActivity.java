package com.example.qgusd.fragmenttest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import java.util.ArrayList;
import java.util.Set;

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

        // list the paired devices in the TextView2
        TextView textView2 = (TextView)findViewById(R.id.textView2);
        for(BluetoothDevice pairedDevice : pairedDevices) {

            textView2.append(Html.fromHtml("<b>" + pairedDevice.getName() + "</b>"));
            textView2.append(" (" + pairedDevice.getAddress() + ")\n");
        }

    }
}
