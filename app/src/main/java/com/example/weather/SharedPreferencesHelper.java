package com.example.weather;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesHelper {
    private Context mContext;

    public SharedPreferencesHelper()
    {

    }

    public SharedPreferencesHelper(Context mContext)
    {
        this.mContext = mContext;
    }

    public void save(String PreferencesCity)
    {
        SharedPreferences sp = mContext.getSharedPreferences("spCity",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("PreferencesCity",PreferencesCity);
        editor.commit();
    }

    public Map<String,String> read()
    {
        Map<String,String> data = new HashMap<String,String>();
        SharedPreferences sp = mContext.getSharedPreferences("spCity",Context.MODE_PRIVATE);
        data.put("PreferencesCity",sp.getString("PreferencesCity","北京市"));
        return data;
    }
}
