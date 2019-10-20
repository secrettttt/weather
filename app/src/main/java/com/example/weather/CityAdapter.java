package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class CityAdapter extends BaseAdapter {
    private LinkedList<City> mData;
    private Context mContext;

    public CityAdapter(LinkedList<City> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_city,parent,false);
        TextView CityChineseName = (TextView) convertView.findViewById(R.id.CityChineseName);
        TextView CityPinyinName = (TextView) convertView.findViewById(R.id.CityPinyinName);
        CityChineseName.setText(mData.get(position).getCityChineseName());
        CityPinyinName.setText(mData.get(position).getCityPinyinName());
        return convertView;
    }
}
