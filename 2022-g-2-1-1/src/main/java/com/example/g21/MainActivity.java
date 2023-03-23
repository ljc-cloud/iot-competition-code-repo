package com.example.g21;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nle.mylibrary.forUse.mdbus4150.Modbus4150;
import com.nle.mylibrary.transfer.DataBusFactory;

public class MainActivity extends AppCompatActivity {

    private String ip = "172.20.20.15";
    private int port = 6003;

    private final Modbus4150 modbus4150 = new Modbus4150(DataBusFactory.newSocketDataBus(ip, port));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}