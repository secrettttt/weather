package com.example.weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

public class CitySelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private List<City> mData = null;
    private Context mContext;
    private CityAdapter mAdapter = null;
    private ListView list_city;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        backButton = (ImageView)findViewById(R.id.city_selection_title_back);
        backButton.setOnClickListener(this);

        mContext = CitySelectionActivity.this;
        list_city = (ListView) findViewById(R.id.list_city);
        mData = new LinkedList<City>();
        mData.add(new City("北京", "beijing"));
        mData.add(new City("上海", "shanghai"));
        mData.add(new City("广州", "guangzhou"));
        mData.add(new City("深圳", "shenzhen"));
        mData.add(new City("天津", "tianjin"));
        mData.add(new City("杭州", "hangzhou"));
        mData.add(new City("南京", "nanjing"));
        mData.add(new City("武汉", "wuhan"));
        mData.add(new City("成都", "chengdu"));
        mData.add(new City("重庆", "chongqing"));
        mData.add(new City("长沙", "changsha"));
        mData.add(new City("哈尔滨", "harbin"));
        mData.add(new City("沈阳", "shenyang"));
        mData.add(new City("长春", "changchun"));
        mData.add(new City("海口", "haikou"));
        mData.add(new City("南宁", "nanning"));
        mData.add(new City("昆明", "kunming"));
        mData.add(new City("合肥", "hefei"));
        mData.add(new City("拉萨", "lasa"));
        mAdapter = new CityAdapter((LinkedList<City>) mData, mContext);
        list_city.setAdapter(mAdapter);

        list_city.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = mData.get(position);
                Log.d("selected_city: ", city.getCityPinyinName());

                //Intent发起端
                Intent intent = new Intent(CitySelectionActivity.this,MainActivity.class);
                intent.putExtra("selected_city",city.getCityChineseName()+"市");
                startActivity(intent);
            }
        });

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
