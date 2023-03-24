package com.iot.g31;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.nle.mylibrary.forUse.mdbus4017.MD4017;
import com.nle.mylibrary.forUse.mdbus4017.MD4017ValConvert;
import com.nle.mylibrary.forUse.mdbus4017.Md4017VIN;
import com.nle.mylibrary.transfer.DataBusFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView tempText;
    private TextView humText;

    private Handler handler = new Handler();

    private static final ExecutorService ES = Executors.newFixedThreadPool(3);

    private String ip = "172.20.20.15";
    private int port = 6005;

    private MD4017 md4017 = new MD4017(DataBusFactory.newSocketDataBus(ip, port));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ES.submit(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 5000);

                try {
                    md4017.getVin(vals -> {
                        float realTemp = MD4017ValConvert.getRealValByType(Md4017VIN.TEM, vals[1]);
                        float realHum = MD4017ValConvert.getRealValByType(Md4017VIN.TEM, vals[2]);
                        String temp = String.format(".2f ℃", realTemp);
                        String hum = String.format(".2f", realHum);

                        runOnUiThread(() -> {
                            tempText.setText(temp);
                            humText.setText(hum);
                        });
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}