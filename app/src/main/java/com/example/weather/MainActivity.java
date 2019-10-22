package com.example.weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView managerButton;
    private ImageView updateButton;
    private ImageView locateButton;
    private RelativeLayout main_background;
    private TextView dateText,weekText,theLunarCalendarText;
    private ImageView weatherImg;
    private TextView realTimeTemperatureText,realTimeWeatherStatusText;
    private TextView updateTimeText;
    private TextView cityNameText;
    private TextView lowTemperatureText,highTemperatureText;
    private TextView weatherStateText;
    private TextView windText;

    private Timer timer = new Timer();

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    private String locationResult = "北京市";

    private Context mContext;
    private SharedPreferencesHelper sh;
    public static Map<String,String> data;

    private void initView()
    {
        main_background = (RelativeLayout)findViewById(R.id.main);
        dateText = (TextView)findViewById(R.id.basicInfo_date);
        weekText = (TextView)findViewById(R.id.basicInfo_week);
        theLunarCalendarText = (TextView)findViewById(R.id.basic_theLunarCalendar);
        weatherImg = (ImageView)findViewById(R.id.todayinfo_weatherStatusImg);
        realTimeTemperatureText = (TextView)findViewById(R.id.todayinfo_realTimeTemperature);
        realTimeWeatherStatusText = (TextView)findViewById(R.id.todayinfo_realTimeWeatherStatus);
        updateTimeText = (TextView)findViewById(R.id.todayinfo_updateTime);
        cityNameText = (TextView)findViewById(R.id.todayinfo_cityName);
        //java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference
        //原因:以下两行没写(查博客得到的原因：很大几率是因为控件实列写错了，好好检查R.id.xx有没有写错)
        lowTemperatureText = (TextView)findViewById(R.id.todayinfo_lowTemperature);
        highTemperatureText = (TextView)findViewById(R.id.todayinfo_highTemperature);
        weatherStateText = (TextView)findViewById(R.id.todayinfo_weatherState);
        windText =(TextView)findViewById(R.id.todayinfo_wind);
    }

    private Handler messageHandler = new Handler(){
        @Override
        //重写handleMessage方法：根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg){
            if(msg.what == 0){
                getWeatherDataFromNet(locationResult);
            }
            if(msg.what == 1) {
                String response = msg.getData().getString("response");//接受msg传递过来的参数
                parseJson(response,"now");
            }
            if(msg.what ==2) {
                String response = msg.getData().getString("response");//接受msg传递过来的参数
                parseJson(response,"forecast");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //通过setContentView(View)接口把布局加载到Activity创建的窗口上
        setContentView(R.layout.main);

        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mContext = getApplicationContext();
        sh = new SharedPreferencesHelper(mContext);
        data = sh.read();
        locationResult = data.get("PreferencesCity");

        //Intent接收端
        Intent intent = getIntent();
        if(intent.getStringExtra("selected_city")!= null)//判断intent是否有值传入
        {
            if(intent.getIntExtra("changePreferencesCity",0) == 1)
            {
                sh.save(intent.getStringExtra("selected_city"));
                data = sh.read();
                locationResult = data.get("PreferencesCity");
            }
            else
            {
                locationResult = intent.getStringExtra("selected_city");
            }
        }

        //动态申请权限
        final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }


        //定位
        getLocationFromBaiduAPI();

        managerButton = (ImageView)findViewById(R.id.title_city_manager);
        managerButton.setOnClickListener(this);

        //这里是利用接口实现事件监听器，除了用接口，还可以使用匿名类实现
        updateButton = (ImageView)findViewById(R.id.title_city_update);
        //setOnClickListener的参数要求是一个实现了OnClickListener接口的对象实体，它可以是任何类的实例，只要该类实现了OnClickListener
        //在这里，它指的是当前的MainActivity对象
        updateButton.setOnClickListener(this);


        locateButton = (ImageView)findViewById(R.id.title_city_locate);
        locateButton.setOnClickListener(this);

        //检查网络的连接状态
        if(CheckNet.getNetState(this)==CheckNet.NET_WIFI)
        {
            Log.d("网络已连接","WIFI");
            Toast.makeText(this, "网络已连接：WIFI", Toast.LENGTH_LONG).show();
            getWeatherDataFromNet(locationResult);
        }
        else if(CheckNet.getNetState(this)==CheckNet.NET_MOBILE)
        {
            Log.d("网络已连接","MOBILE");
            Toast.makeText(this, "网络已连接：MOBILE", Toast.LENGTH_LONG).show();
            getWeatherDataFromNet(locationResult);
        }
        else
        {
            Log.d("网络未连接","NONE");
            Toast.makeText(this, "网络未连接", Toast.LENGTH_LONG).show();
        }

        initView();

        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                Message message = new Message();
                message.what = 0;
                messageHandler.sendMessage(message);
            }
        },1800000,1800000);//1000==1秒，1800000==30分钟
    }

    public void onClick(View v) {
        if(v.getId()==R.id.title_city_manager)
        {
            Intent intent = new Intent(this,CitySelectionActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.title_city_update)
        {
            getWeatherDataFromNet(locationResult);
        }
        if(v.getId()==R.id.title_city_locate)
        {
            getLocationFromBaiduAPI();
        }
    }

    private void parseJson(String jsonData,String step)
    {
        ArrayList WeatherInfos = new ArrayList<WeatherInfo>();
        WeatherInfo wi = new WeatherInfo();
        try{
            JSONObject jsonObject0 = new JSONObject(jsonData);
            JSONArray jsonArray0 = jsonObject0.getJSONArray("HeWeather6");
            JSONObject jsonObject1 = (JSONObject) jsonArray0.get(0);
            JSONObject jsonObject2 = (JSONObject) jsonObject1.get("basic");
            wi.setCityName(jsonObject2.getString("location"));
            cityNameText.setText(locationResult);

            JSONObject jsonObject3 = (JSONObject) jsonObject1.get("update");
            wi.setDate(jsonObject3.getString("loc"));
            dateText.setText(wi.getDate());

            weekText.setText(wi.getWeek());

            Calendar cal=Calendar.getInstance();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
            cal.setTimeZone(TimeZone.getDefault());
            TheLunarCalendar lunar=new TheLunarCalendar(cal);
            theLunarCalendarText.setText("农历"+lunar.getChinaMonthString(lunar.getMonth())+"月"+lunar.getChinaDayString(lunar.getDay()));

            if(wi.isNight())
                main_background.setBackgroundResource(R.drawable.main_background_night);
            else
                main_background.setBackgroundResource((R.drawable.main_background_default));

            if(step=="now")
            {
                /*
                    wi.setUpdateTime(jsonObject3.getString("loc"));
                 */
                updateTimeText.setText(wi.getUpdateTime());

                JSONObject jsonObject4 = (JSONObject) jsonObject1.get("now");
                wi.setRealTimeTemperature(jsonObject4.getString("tmp"));
                realTimeTemperatureText.setText(wi.getRealTimeTemperature());

                wi.setRealTimeWeatherStatus(jsonObject4.getString("cond_txt"));
                realTimeWeatherStatusText.setText(wi.getRealTimeWeatherStatus());
                if(wi.isNight())
                {
                    weatherImg.setImageResource(R.drawable.weather_night);
                }
                else
                {
                    switch(wi.getRealTimeWeatherStatus())
                    {
                        case "晴":weatherImg.setImageResource(R.drawable.weather_qing);break;
                        case "小雨":weatherImg.setImageResource(R.drawable.weather_yu);break;
                        case "中雨":weatherImg.setImageResource(R.drawable.weather_yu);break;
                        case "大雨":weatherImg.setImageResource(R.drawable.weather_yu);break;
                        case "雷阵雨":weatherImg.setImageResource(R.drawable.weather_yu);break;
                        case "暴雨":weatherImg.setImageResource(R.drawable.weather_yu);break;
                        case "阵雨":weatherImg.setImageResource(R.drawable.weather_yu);break;
                        case "小雪":weatherImg.setImageResource(R.drawable.weather_xue);break;
                        case "中雪":weatherImg.setImageResource(R.drawable.weather_xue);break;
                        case "暴雪":weatherImg.setImageResource(R.drawable.weather_xue);break;
                        case "雪":weatherImg.setImageResource(R.drawable.weather_xue);break;
                        case"多云":weatherImg.setImageResource(R.drawable.weather_yun);break;
                        case "阴":weatherImg.setImageResource(R.drawable.weather_yun);break;
                        default:weatherImg.setImageResource(R.drawable.weather_qing);break;
                    }
                }
            }

            if(step=="forecast")
            {
                JSONArray jsonArray1 = jsonObject1.getJSONArray("daily_forecast");
                JSONObject jsonObject5 = (JSONObject) jsonArray1.get(0);
                wi.setLowTemperature(jsonObject5.getString("tmp_min"));
                lowTemperatureText.setText(wi.getLowTemperature());

                wi.setHighTemperature(jsonObject5.getString("tmp_max"));
                highTemperatureText.setText(wi.getHighTemperature());

                wi.setWeatherState(jsonObject5.getString("cond_txt_d"));
                weatherStateText.setText(wi.getWeatherState());

                wi.setWind(jsonObject5.getString("wind_dir"));
                windText.setText(wi.getWind());
            }
            WeatherInfos.add(wi);

            Toast.makeText(MainActivity.this,"天气信息更新成功",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){e.printStackTrace();}
    }


    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            int errorCode = location.getLocType();
            Log.d("errorCode",errorCode+" ");

            if(errorCode !=61||errorCode!=161)
            {
                Toast.makeText(MainActivity.this,"定位失败，请在系统设置中打开定位权限。",Toast.LENGTH_LONG).show();
                cityNameText.setText(locationResult);
            }
            else
            {
                Toast.makeText(MainActivity.this,"定位成功！",Toast.LENGTH_LONG).show();
                String country = location.getCountry();    //获取国家
                String province = location.getProvince();    //获取省份
                String city = location.getCity();    //获取城市
                String district = location.getDistrict();    //获取区县
                locationResult = district;
                cityNameText.setText(locationResult);
                Log.d("country",country);
                Log.d("province",province);
                Log.d("city", city);
                Log.d("district", district);
            }
        }
    }




    private void getWeatherDataFromNet(String cityName) {
        //注意以下两个URL：http和https的区别
        //http://wthrcdn.etouch.cn/weather_mini?city=
        //https://wthrcdn.etouch.cn/weather_mini?city=
        //final String address = "http://wthrcdn.etouch.cn/weather_mini?city=" + cityName;
        //final String address = "https://api.heweather.net/s6/weather/now?location="+cityName+"&key=6932201f6c414b1daed47c34fec6ef3f"
        //得到的状态码："HeWeather6":[{"status":"permission denied"}
        final String addressNow= "https://free-api.heweather.net/s6/weather/now?location="+cityName+"&key=ed5e845bf8274a38bd9b073eb35c3241";
        final String addressForecast= "https://free-api.heweather.net/s6/weather/forecast?location="+cityName+"&key=ed5e845bf8274a38bd9b073eb35c3241";
        //final String address = "https://api.heweather.net/s6/weather/now?location="+cityName+"&key=6932201f6c414b1daed47c34fec6ef3f";
        Log.d("AddressNow:", addressNow);
        Log.d("AddressForecast:",addressForecast);


        //HttpURLconnection是同步的请求，所以必须放在子线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try{
                    for(int i=0;i<2;i++)
                    {
                        URL url = new URL(addressNow);
                        URL url2 = new URL(addressForecast);
                        if(i==0){
                            urlConnection = (HttpURLConnection) url.openConnection();
                        }
                        else if(i==1){
                            urlConnection = (HttpURLConnection) url2.openConnection();
                        }
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setConnectTimeout(8000);
                        urlConnection.setReadTimeout(8000);
                        InputStream in = urlConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuffer sb = new StringBuffer();
                        String str;
                        while ((str = reader.readLine()) != null) {
                            sb.append(str);
                            Log.d("date from url", str);
                        }
                        String response = sb.toString();
                        Log.d("response", response);

                        Message msg = new Message();
                        if(i==0){
                            msg.what = 1;
                        }
                        else if(i==1){
                            msg.what = 2;
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("response",response);  //往Bundle中存放数据
                        msg.setData(bundle);//mes利用Bundle传递数据
                        messageHandler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getLocationFromBaiduAPI()
    {
        //Toast.makeText(this,"正在定位...",Toast.LENGTH_SHORT);你不show一下是显示不出来的！！！
        Toast.makeText(this,"正在定位...",Toast.LENGTH_SHORT).show();
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        getWeatherDataFromNet(locationResult);
    }


    //首先检查定位是否打开
    final boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }

    //如果定位已经打开，OK 很好，如果定位没有打开，则需要用户去打开
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;
    private void setLocationService() {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
    }

    //进入定位设置界面，让用户自己选择是否打开定位。选择的结果获取
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (isLocationEnable(this)) {
                //定位已打开的处理
            } else {
                //定位依然没有打开的处理
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }


}
