package com.iot.g12;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nle.mylibrary.forUse.mdbus4150.Modbus4150;
import com.nle.mylibrary.transfer.DataBusFactory;

public class MainActivity extends AppCompatActivity {

    private ImageView car1;
    private ImageView car2;
    private ImageView car3;
    private ImageView car4;
    private ImageView car5;
    private ImageView car6;
    private ImageView car7;
    private ImageView car8;
    private ImageView car9;
    private ImageView car10;

    private TextView leftText;

    private ObjectAnimator o1;
    private ObjectAnimator o2;
    private ObjectAnimator o3;
    private ObjectAnimator o4;
    private ObjectAnimator o5;
    private ObjectAnimator o6;
    private ObjectAnimator o7;
    private ObjectAnimator o8;
    private ObjectAnimator o9;
    private ObjectAnimator o10;

    private final int total = 10;
    private int left = 10;

    // 串口服务器IP和端口
    private String ip = "172.20.20.15";
    private int port = 6003;

    private final Modbus4150 modbus4150 = new Modbus4150(DataBusFactory.newSocketDataBus(ip, port));
    // 红外对射端口号
    private int irPort = 6;

    private int old = 1;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 200);
                try {
                    modbus4150.getVal(irPort, val -> {
                        // 如果旧值等于感应到的红外对射的值
                        if (val != old && val != 1) {
                            if (left <= 0) {
                                Toast.makeText(MainActivity.this, "车库空了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            left--;
                            Log.d("left", left + "");
                            switch (total - left) {
                                case 1: o1.start(); break;
                                case 2: o2.start(); break;
                                case 3: o3.start(); break;
                                case 4: o4.start(); break;
                                case 5: o5.start(); break;
                                case 6: o6.start(); break;
                                case 7: o7.start(); break;
                                case 8: o8.start(); break;
                                case 9: o9.start(); break;
                                case 10: o10.start(); break;
                            }
                            runOnUiThread(() -> {
                                String str = "空位:" + left + "个";
                                leftText.setText(str);
                            });
                        }
                        old = val;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void init() {
        car1 = findViewById(R.id.car1);
        car2 = findViewById(R.id.car2);
        car3 = findViewById(R.id.car3);
        car4 = findViewById(R.id.car4);
        car5 = findViewById(R.id.car5);
        car6 = findViewById(R.id.car6);
        car7 = findViewById(R.id.car7);
        car8 = findViewById(R.id.car8);
        car9 = findViewById(R.id.car9);
        car10 = findViewById(R.id.car10);
        leftText = findViewById(R.id.left_text);

        o1 = ObjectAnimator.ofFloat(car1, "translationX", -200);
        o2 = ObjectAnimator.ofFloat(car2, "translationX", -200);
        o3 = ObjectAnimator.ofFloat(car3, "translationX", -200);
        o4 = ObjectAnimator.ofFloat(car4, "translationX", -200);
        o5 = ObjectAnimator.ofFloat(car5, "translationX", -200);
        o6 = ObjectAnimator.ofFloat(car6, "translationX", 200);
        o7 = ObjectAnimator.ofFloat(car7, "translationX", 200);
        o8 = ObjectAnimator.ofFloat(car8, "translationX", 200);
        o9 = ObjectAnimator.ofFloat(car9, "translationX", 200);
        o10 = ObjectAnimator.ofFloat(car10, "translationX", 200);
    }
}