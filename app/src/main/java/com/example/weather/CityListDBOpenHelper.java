package com.example.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CityListDBOpenHelper extends SQLiteOpenHelper {

    public CityListDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,"citylist.db",null,1);
    }

    //数据库第一次创建时被调用
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE city(cityid INTEGER PRIMARY KEY AUTOINCREMENT, cityChineseName ,cityPinyinName)");
        //插入操作
        String[] nameList = {"北京","上海","广州","深圳","天津","杭州","南京","武汉","成都","重庆",
               "长沙","哈尔滨","沈阳","长春","海口","南宁","昆明","合肥","银川","呼和浩特","兰州","西宁","乌鲁木齐","拉萨"};
        String[] pinyinList = {"beijing","shanghai","guangzhou","shenzhen","tianjin","hangzhou","nanjing","wuhan","chengdu","chongqing",
               "changsha","harbin","shenyang","changchun","haikou","nanning","kunming","hefei","yinchuan","huhhot","lanzhou","xining","urumqi","lasa"};
        for(int i=0;i<nameList.length;i++) {
            ContentValues values = new ContentValues();
            values.put("cityChineseName", nameList[i]);
            values.put("cityPinyinName", pinyinList[i]);
            db.insert("city", null, values);
        }
    }

    //软件版本号发生改变时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("");
    }
}
