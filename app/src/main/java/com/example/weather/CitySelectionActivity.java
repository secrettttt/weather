package com.example.weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class CitySelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private List<City> mData = null;
    private Context mContext;
    private CityAdapter mAdapter = null;
    private ListView list_city;
    private ImageView backButton;
    private TextView preferencesCityText;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private City city;
    private int changePreferencesCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        backButton = (ImageView)findViewById(R.id.city_selection_title_back);
        backButton.setOnClickListener(this);

        preferencesCityText = (TextView)findViewById(R.id.city_selection_title_preferences_city);
        preferencesCityText.setText(MainActivity.data.get("PreferencesCity"));

        mContext = CitySelectionActivity.this;
        list_city = (ListView) findViewById(R.id.list_city);
        mData = new LinkedList<City>();

        //数据库查询操作
        CityListDBOpenHelper cityListDBOpenHelper = new CityListDBOpenHelper(this,"citylist.db",null,1);
        SQLiteDatabase db = cityListDBOpenHelper.getWritableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM city", null);
        if(cursor.moveToFirst())
        {
            do{
                String cityChineseName = cursor.getString(cursor.getColumnIndex("cityChineseName"));
                String cityPinyinName = cursor.getString(cursor.getColumnIndex("cityPinyinName"));
                mData.add(new City(cityChineseName, cityPinyinName));
            }while(cursor.moveToNext());
        }
        cursor.close();

        mAdapter = new CityAdapter((LinkedList<City>) mData, mContext);
        list_city.setAdapter(mAdapter);

        list_city.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = mData.get(position);
                Log.d("selected_city: ", city.getCityPinyinName());

                alert = null;
                builder = new AlertDialog.Builder(mContext);
                alert = builder.setTitle("是否将该城市改为默认城市：")
                        .setMessage("将当前选中的城市改为默认城市,\n程序启动时默认显示该城市的天气信息。")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changePreferencesCity=0;
                                startIntentToMainActivity();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changePreferencesCity=1;
                                startIntentToMainActivity();
                            }
                        }).create();             //创建AlertDialog对象
                alert.show();                    //显示对话框
            }
        });

    }

    private void startIntentToMainActivity()
    {
        //Intent发起端
        Intent intent = new Intent(CitySelectionActivity.this,MainActivity.class);
        intent.putExtra("selected_city",city.getCityChineseName()+"市");
        intent.putExtra("changePreferencesCity",changePreferencesCity);
        startActivity(intent);
    }


    @Override
    public void onClick(View v)
    {
        if(v.getId()== R.id.city_selection_title_back)
        {
            finish();
        }
    }
}
