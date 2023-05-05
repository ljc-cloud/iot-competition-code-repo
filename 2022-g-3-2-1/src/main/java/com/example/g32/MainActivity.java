package com.example.g32;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.nle.mylibrary.forUse.rfid.RFID;
import com.nle.mylibrary.transfer.DataBusFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private String ip = "172.20.20.15";
    private int port = 6001;
    private RFID rfid = new RFID(DataBusFactory.newSocketDataBus(ip, port));

    private static final ExecutorService ES = Executors.newFixedThreadPool(3);
    private Handler handler = new Handler();

    private TextView nameText;
    private TextView priceText;
    private TextView rfidText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameText = findViewById(R.id.name_text);
        priceText = findViewById(R.id.price_text);
        rfidText = findViewById(R.id.rfid_text);

        SharedPreferences sharedPreferences = getSharedPreferences("product", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        String rfid1 = "1";
        String rfid2 = "2";
        String rfid3 = "3";
        edit.putString(rfid1 + "name", "华为Mate20").putFloat(rfid1 + "price", 5999.0f);
        edit.putString(rfid2 + "name", "IPhoneXS").putFloat(rfid2 + "price", 2299.0f);
        edit.putString(rfid3 + "name", "小米Mix3").putFloat(rfid3 + "price", 7699.0f);
        edit.apply();

        ES.submit(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 300);
                try {
                    rfid.readSingleEpc(epc -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("product", MODE_PRIVATE);
                        String name = sharedPreferences.getString(epc + "name", "");
                        float price = sharedPreferences.getFloat(epc + "price", 0);

                        runOnUiThread(() -> {
                            rfidText.setText(epc);
                            nameText.setText(name);
                            String p = price + "元";
                            priceText.setText(p);
                        });
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}