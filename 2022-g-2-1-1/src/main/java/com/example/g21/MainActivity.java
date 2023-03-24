package com.example.g21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.nle.mylibrary.forUse.mdbus4150.Modbus4150;
import com.nle.mylibrary.transfer.DataBusFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private String ip = "172.20.20.15";
    private int port = 6003;
    private int old = 0;
    private int id = 1;
    private Parking previous;

    private final Modbus4150 modbus4150 = new Modbus4150(DataBusFactory.newSocketDataBus(ip, port));

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Parking> dataList = new ArrayList<>(10);

    private Handler handler = new Handler();

    private DataBaseHelper dataBaseHelper;
    private static final ExecutorService ES = Executors.newSingleThreadExecutor();

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        MyAdapter adapter = new MyAdapter(this, R.layout.list_item, dataList);
        listView.setAdapter(adapter);

        dataBaseHelper = DataBaseHelper.getInstance(this);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        // 每个0.2s收集一次数据
        ES.submit(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 200);
                try {
                    modbus4150.getVal(0, val -> {
                        // 判断是否为重复数据
                        if (val != old) {
                            ContentValues contentValues = new ContentValues();
                            // 格式化时间
                            String time = simpleDateFormat.format(new Date());
                            // 存入数据库
                            contentValues.put("time", time);
                            String value = val == 1? "true" : "false";
                            contentValues.put("value", value);
                            db.insert("record", null, null);
                            // 如果val = 1 ，为开
                            if (val == 1) {
                                Parking parking = new Parking(id, time, "");
                                previous = parking;
                                dataList.add(parking);
                            } else {
                                // 如果val = 0 ，为关
                                previous.setCloseTime(time);
                                // 计算持续时间，单位s
                                try {
                                    Date open = simpleDateFormat.parse(previous.getOpenTime());
                                    Date close = simpleDateFormat.parse(previous.getCloseTime());
                                    long s = close.getTime() - open.getTime() / 1000;
                                    previous.setContinueTime(s);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            // 呼叫适配器更新数据
                            adapter.notifyDataSetChanged();
                        }
                        // 更新
                        old = val;
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}