package com.example.g21;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static DataBaseHelper INSTANCE;
    private final static String DB_NAME = "ir_record";

    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table record(" +
                "id integer primary key autoincrement," +
                "time datetime," +
                "value text)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static DataBaseHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataBaseHelper(context, DB_NAME, null, 1);
        }
        return INSTANCE;
    }
}
