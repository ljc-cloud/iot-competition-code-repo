package com.example.g221;

import androidx.appcompat.app.AppCompatActivity;
import cn.com.newland.nle_sdk.responseEntity.SensorDataRecord;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String access = "28FD18DACE93D49A98DBAAF223DF16E708CE13F8599963BADF3E3C91FF69ACAA79DCFC0DC47C5D148B1177531235B9299D95F0141EAD7F29A1496DA2BF03F4AE98141B8B8D1166FCDC044670AA17B805910C3B767981D0174ED0EC61E44C174CFEFFB55A843E0F737C4EF28CED8216F6E9AFA88892C010017B20E59FC0B14DA3F6FE5FEBD803ABEB522E39BA7661DD026655C416FC6A940B083E370745C4FA888FAC89E956E619354DB5EA0D8299A34FC971136094B64D76B7E1EACC4BDB4BD2E2358A2022CDE5B4FD4A80D1AD1E73E0458EF8066225B66F5D95F249BA17F66D";
    private NetWorkBusiness netWorkBusiness = new NetWorkBusiness(access, "http://api.nlecloud.com");
    private Handler handler = new Handler();
    private Context context = this;

    private ImageView fanBg;
    private Button open;
    private Button close;
    private TextView co;
    private ImageView fan;
    private ImageView window;
    private Button switch1;
    private Button switch2;

    private AnimationDrawable fanAni1;
    private AnimationDrawable fanAni2;
    private AnimationDrawable windowAni;

    private boolean fanState;
    private boolean windowState;

    private boolean start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        switch1.setEnabled(false);
        switch2.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 300);
                if (!start) {
                    return;
                }
                netWorkBusiness.getSensorData("699190", "I_co", null, null, null, null, null,
                        null, null, new NCallBack<BaseResponseEntity<SensorDataRecord>>(context) {
                            @Override
                            protected void onResponse(BaseResponseEntity<SensorDataRecord> response) {
                                SensorDataRecord resultObj = response.getResultObj();
                                if (resultObj == null) {
                                    return;
                                }
                                List<SensorDataRecord.DataPoint> dataPoints = resultObj.DataPoints;
                                if (dataPoints == null) {
                                    Log.d("CO", "Null");
                                    return;
                                }
                                List<SensorDataRecord.VR> pointDTO = dataPoints.get(0).PointDTO;
                                String coStr = pointDTO.get(0).Value;
                                int coValue = Integer.parseInt(coStr);
                                if (coValue > 800) {
                                    // 开推杆 开风扇 coValue > 800
                                    control("m_fan", 1);
                                    control("m_pushrod_putt", 1);
                                    control("m_pushrod_back", 0);
                                    window.setBackgroundResource(R.drawable.door_open_ani);
                                    fanAni1.start();
                                    fanAni2.start();
                                    windowAni.start();
                                } else if (coValue > 300) {
                                    // 开风扇 关推杆 300 < coValue < 800
                                    control("m_fan", 1);
                                    control("m_pushrod_putt", 0);
                                    control("m_pushrod_back", 1);
                                    fanAni1.start();
                                    fanAni2.start();
                                    window.setBackgroundResource(R.drawable.door_close_ani);
                                    windowAni = (AnimationDrawable) window.getBackground();
                                    windowAni.start();
                                } else if (coValue < 300){
                                    // 关风扇 coValue < 300
                                    control("m_fan", 0);
                                    control("m_pushrod_putt", 0);
                                    control("m_pushrod_back", 1);
                                    fanAni1.stop();
                                    fanAni2.stop();
                                    window.setBackgroundResource(R.drawable.door_close_ani);
                                    windowAni = (AnimationDrawable) window.getBackground();
                                    windowAni.start();
                                }
                                runOnUiThread(() -> {
                                    String template = coStr + "ppm";
                                    co.setText(template);
                                });
                            }
                        });
            }
        }).start();

        open.setOnClickListener(view -> {
            start = true;
            switch1.setEnabled(true);
            switch2.setEnabled(true);
        });

        close.setOnClickListener(view -> {
            start = false;
            switch1.setEnabled(false);
            switch2.setEnabled(false);
        });

        switch1.setOnClickListener(view -> {
            if (fanState) {
                control("m_fan", 0);
                fanAni1.stop();
                fanAni2.stop();
                switch1.setBackgroundResource(R.drawable.btn_list_switch_off);
                fanState = false;
            } else {
                control("m_fan", 1);
                fanAni1.start();
                fanAni2.start();
                switch1.setBackgroundResource(R.drawable.btn_list_switch_on);
                fanState = true;
            }
        });

        switch2.setOnClickListener(view -> {
            if (windowState) {
                Log.d("Push_rod", "back");
                control("m_pushrod_putt", 0);
                control("m_pushrod_back", 1);
                window.setBackgroundResource(R.drawable.door_close_ani);
                windowAni = (AnimationDrawable) window.getBackground();
                windowAni.start();
                switch2.setBackgroundResource(R.drawable.btn_list_switch_off);
                windowState = false;
            } else {
                Log.d("Push_rod", "push");
                control("m_pushrod_back", 0);
                control("m_pushrod_putt", 1);
                window.setBackgroundResource(R.drawable.door_open_ani);
                windowAni = (AnimationDrawable) window.getBackground();
                windowAni.start();
                switch2.setBackgroundResource(R.drawable.btn_list_switch_on);
                windowState = true;
            }
        });
    }

    private void control(String apiTag, Object data) {
        netWorkBusiness.control("699190", apiTag, data, new NCallBack<BaseResponseEntity>(context) {
            @Override
            protected void onResponse(BaseResponseEntity response) {
            }
        });
    }

    private void initView() {
        fanBg = findViewById(R.id.fan_bg);
        open = findViewById(R.id.open);
        close = findViewById(R.id.close);
        co = findViewById(R.id.co);
        fan = findViewById(R.id.fan);
        window = findViewById(R.id.window);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);

        fanAni1 = (AnimationDrawable) fan.getBackground();
        fanAni2 = (AnimationDrawable) fanBg.getBackground();
        windowAni = (AnimationDrawable) window.getBackground();
    }
}