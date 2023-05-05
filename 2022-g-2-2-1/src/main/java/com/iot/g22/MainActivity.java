package com.iot.g22;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nle.mylibrary.forUse.rfid.RFID;
import com.nle.mylibrary.transfer.DataBusFactory;

public class MainActivity extends AppCompatActivity {

    private String ip = "";
    private int port = 6001;
    private RFID rfid = new RFID(DataBusFactory.newSocketDataBus(ip, port));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}