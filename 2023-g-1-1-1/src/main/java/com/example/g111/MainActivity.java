package com.example.g111;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import cn.com.newland.nle_sdk.requestEntity.SignIn;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.newland.CameraManager;
import com.newland.view.PlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private PlayerView cameraView;
    private ImageView curtain;
    private ImageView lamp;
    private CameraManager cameraManager;
    private NetWorkBusiness netWorkBusiness;
    private Context context = this;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private Button switch1;
    private Button switch2;
    private boolean openCamera = false;
    private boolean openLamp = false;
    private String access = "F532F957A38355C452FCBDCB53CF3DEB6B6FE115D56517F68D9F4563DE7516BACFE48DB5A8A400DBE1D166A1FE14A27DE3C09D446D8D7D2F6E3E27160D98E15BB0724FB0FFF6BD9A6BC88199CCF105004150D0A5F51B23C4F5073F57AA832BFFBB683DFAAB155AA814847CE6E67DE6B079B74CDD839EA867D99131921210932F65E6560899B5A310A96F460C8909E8F66B1DDF5DFC1625A1A2EA2E49D062CBA4C7B2E5A2882202FD50F4D64C30358FE807452F2E11F816F8F643793AB8F55E716EADA57FC82030FAD7810CFB046690E625202F418E60EA82058A160BD005A30F";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private EditText openTime;
    private EditText closeTime;
    private Button switch3;
    private boolean openSetting = false;

    private Date open;
    private Date close;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        curtain = findViewById(R.id.curtain);
        cameraView = findViewById(R.id.camera);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        lamp = findViewById(R.id.lamp);
        layout1 = findViewById(R.id.setting1);
        layout2 = findViewById(R.id.setting2);
        openTime = findViewById(R.id.openTime);
        closeTime = findViewById(R.id.closeTime);
        switch3 = findViewById(R.id.switch3);

        netWorkBusiness = new NetWorkBusiness(access, "http://api.nlecloud.com");
        cameraManager = CameraManager.getInstance();
        cameraManager.setBaseInfo(cameraView, "admin", "", "172.18.3.13", "stream1");
//        curtain.setVisibility(View.INVISIBLE);
//        cameraView.setVisibility(View.VISIBLE);
        cameraManager.videoSetting(this);

        switch3.setOnClickListener(view -> {
            String openTimeStr = openTime.getText().toString();
            String closeTimeStr = closeTime.getText().toString();
            String[] split1 = openTimeStr.split(":");
            String[] split2 = closeTimeStr.split(":");
            open = new Date();
            open.setHours(Integer.parseInt(split1[0]));
            open.setMinutes(Integer.parseInt(split1[1]));
            close = new Date();
            close.setHours(Integer.parseInt(split2[0]));
            close.setMinutes(Integer.parseInt(split2[1]));
            if (openSetting) {
                switch3.setText("开启设定");
                openSetting = false;
            } else {
                switch3.setText("关闭设定");
                openSetting = true;
            }
        });

        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    if (openSetting) {
                        Date now = new Date();
                        Log.d("open", open.toString());
                        Log.d("close", close.toString());
                        Log.d("now", now.toString());
                        if (now.after(open) && now.before(close)) {
                            netWorkBusiness.control("695410", "m_strobe_red", 1, new NCallBack<BaseResponseEntity>(context) {
                                @Override
                                protected void onResponse(BaseResponseEntity baseResponseEntity) {
                                }
                            });
                        } else {
//                            Log.e("TAG","TAG");
                            netWorkBusiness.control("695410", "m_strobe_red", 0, new NCallBack<BaseResponseEntity>(context) {
                                @Override
                                protected void onResponse(BaseResponseEntity baseResponseEntity) {
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        switch1.setOnClickListener(view -> {
            if (openCamera) {
                switch1.setText("开启监测系统");
                switch2.setVisibility(View.VISIBLE);
                curtain.setVisibility(View.VISIBLE);
                cameraView.setVisibility(View.INVISIBLE);
                layout1.setVisibility(View.INVISIBLE);
                layout2.setVisibility(View.INVISIBLE);
                switch3.setVisibility(View.INVISIBLE);
                cameraManager.releaseCamera();
            } else {
                switch1.setText("关闭监测系统");
                switch2.setVisibility(View.INVISIBLE);
                curtain.setVisibility(View.INVISIBLE);
                cameraView.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                switch3.setVisibility(View.VISIBLE);
                cameraManager.videoSetting(this);
                cameraManager.openCamera();
            }
            openCamera = !openCamera;
        });
        switch2.setOnClickListener(view -> {
            if (openLamp) {
                netWorkBusiness.control("695410", "m_strobe_red", 0, new NCallBack<BaseResponseEntity>(context) {
                    @Override
                    protected void onResponse(BaseResponseEntity baseResponseEntity) {
                    }
                });
                lamp.setBackgroundResource(R.drawable.lamp_off);
                switch2.setBackgroundResource(R.drawable.btn_list_switch_off);
            } else {
                netWorkBusiness.control("695410", "m_strobe_red", 1, new NCallBack<BaseResponseEntity>(context) {
                    @Override
                    protected void onResponse(BaseResponseEntity baseResponseEntity) {
                    }
                });
                lamp.setBackgroundResource(R.drawable.lamp_on);
                switch2.setBackgroundResource(R.drawable.btn_list_switch_on);
            }
            openLamp = !openLamp;
        });

    }
}