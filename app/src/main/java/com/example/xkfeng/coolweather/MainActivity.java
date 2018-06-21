package com.example.xkfeng.coolweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.bumptech.glide.Glide;
import com.example.xkfeng.coolweather.JavaBean.City;
import com.example.xkfeng.coolweather.JavaBean.County;
import com.example.xkfeng.coolweather.JavaBean.Province;
import com.example.xkfeng.coolweather.JavaBean.Weather;
import com.example.xkfeng.coolweather.Service.AutoUpdateService;
import com.example.xkfeng.coolweather.Utils.JsonUtils;
import com.example.xkfeng.coolweather.Utils.Utils;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    /*
       和风天气KEY 722dda481604441db9967f3fabd76ed1
     */

    private ScrollView weatherLayout ;

    private TextView titleCity ;

    private TextView titleUpdateTime ;

    private TextView degreeText ;

    private TextView weatherInfoText ;

    private LinearLayout forecastLayout ;

    private TextView aqiText ;

    private TextView pm25Text ;

    private TextView comfortText ;

    private TextView carWashText ;

    private TextView sportText ;

    public ImageView bing_pic_ima ;

    private static final String TAG = "MainActivity" ;

    public SwipeRefreshLayout swipeRefreshLayout ;

    private String mWeatherId ;

    public DrawerLayout drawerLayout ;

    private Button navBtn ;

    private final static int REQUEST_CODE = 1 ;

    private LocationClient mLocationClient ;

    private static String LOCATION ;

    private ProgressDialog progressDialog ;

    private Province currentProvince ;

    private City currentCity ;

    private List<City> cityList ;

    private List<County> countyList ;

    private String weatherId ;

    private SharedPreferences sharedPreferences ;

    private BingPicBroadcastReceiver bingPicBroadcastReceiver;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new LocationData());
        LocationClientOption option = new LocationClientOption() ;
        option.setScanSpan(1000 * 60 * 60);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        setContentView(R.layout.activity_weather);

        Log.i(TAG ,"ONCREATE");

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        bingPicBroadcastReceiver = new BingPicBroadcastReceiver() ;
        IntentFilter filter = new IntentFilter("com.example.xkfeng.bingpicreceiver") ;
        registerReceiver(bingPicBroadcastReceiver , filter) ;
        /*
        申请权限
         */
        reqeustPermission();


        //初始化各个控件


        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            //有缓存时，直接解析天气数据
            Weather weather = JsonUtils.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //没有缓存的时候去服务器查询天气

        }
        bing_pic_ima = (ImageView) findViewById(R.id.bing_pic_ima);
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bing_pic_ima);
        } else {
            loadBingPic();
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navBtn = (Button) findViewById(R.id.nav_button);
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        /*
        初始影藏界面
         */
        sharedPreferences = getSharedPreferences("isFirst" , MODE_PRIVATE) ;

        if (sharedPreferences.getString("LOCATION", null) == null) {
            weatherLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void reqeustPermission() {

        List<String> perms = new ArrayList<>() ;
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            perms.add(Manifest.permission.ACCESS_FINE_LOCATION) ;
        }
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            perms.add(Manifest.permission.READ_PHONE_STATE)  ;
        }
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE) ;
        }
        if (perms.size()>0){
            String [] permissions = perms.toArray(new String[perms.size()]) ;
            ActivityCompat.requestPermissions(this , permissions , REQUEST_CODE);
        }
    }

    /*
    获取当前位置
     */
    public class LocationData extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            /*
            只在程序第一次启动的时候调用
             */
            Log.i(TAG , "SPF IS" + sharedPreferences) ;
            /*
            获取的数据可能为重庆市  湖南省
            但是在服务器中却没有市，省这个字段，所以需要截取
             */
            if (sharedPreferences.getString("LOCATION", null) == null)
            {
                LOCATION = bdLocation.getProvince().substring(0 ,bdLocation.getProvince().length()-1) ;
              //  Log.i(TAG , "THE LOCATION IS " + LOCATION) ;
                String address = "http://guolin.tech/api/china" ;
                //从服务器查询数据
                queryFromServer(address , "province");
                //写入数据
                SharedPreferences.Editor editor = getSharedPreferences("isFirst" , MODE_PRIVATE).edit() ;
                editor.putString("LOCATION" , LOCATION) ;
                editor.apply();
            }

        }
    }
    /*
    加载每日b
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic" ;
        Utils.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string() ;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit() ;
                editor.putString("bing_pic" , bingPic) ;
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bingPic).into(bing_pic_ima) ;
                    }
                });
            }
        });
    }

    /*
    处理并且显示Weather实体类的数据
     */
    private void showWeatherInfo(Weather weather)
    {
     //   Log.i(TAG , "WEATHER DATA IS  " + weather.forecasts.size()) ;
        String cityName = weather.basic.cityName ;
        String updateTime = weather.basic.update.updateTime.split(" ")[1] ;
        String degree = weather.now.temperature + " ℃" ;
        String weatherInfo = weather.now.more.info ;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Weather.Forecast forecast : weather.forecasts){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item , forecastLayout , false) ;
            TextView dateText = (TextView)view.findViewById(R.id.date_text) ;
            TextView infoText = (TextView)view.findViewById(R.id.info_text) ;
            TextView maxText = (TextView)view.findViewById(R.id.max_text) ;
            TextView minText = (TextView)view.findViewById(R.id.min_text) ;
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }

        if (weather.aqi != null)
        {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度： " + weather.suggestion.comfort.info ;
        String carWash = "洗车指数： " + weather.suggestion.carWash.info ;
        String sport = "运动建议： " + weather.suggestion.sport.info ;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this , AutoUpdateService.class) ;
        startService(intent) ;
    }

    /*
       根据天气id获取具体的天气数据
     */
    public void requestWeather(final String weatherId)
    {

         /*
        对网络状态进行判断
         */
        if (!Utils.JudgeNetState(MainActivity.this)){
            Toast.makeText(this , "当前处于没有网络的状态，获取失败" ,Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            closeProgressDialog();
            return ;
        }
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=722dda481604441db9967f3fabd76ed1" ;

        Utils.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this , "获取天气失败" ,Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string() ;
                final Weather weather = JsonUtils.handleWeatherResponse(responseText) ;
//                Log.i(TAG , "RESPONSE IS " + responseText) ;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit() ;
                            editor.putString("weather" ,responseText) ;
                            editor.apply();
                            //修正weatherId
                            //当前id可能来源于初始化的调用，
                            //也可能来自用户的重新选择
                            mWeatherId = weather.basic.weatherId ;
                            //显示信息
                            showWeatherInfo(weather);
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(MainActivity.this , "更新天气信息成功" ,Toast.LENGTH_SHORT).show();


                        }else {
                            Toast.makeText(MainActivity.this , "获取天气信息失败" ,Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);

                        }
                    }
                });

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE:
                if (grantResults.length>0){
                   for (int result : grantResults)
                   {
                       if (result != PackageManager.PERMISSION_GRANTED){
                           Toast.makeText(this , "部分权限没能成功获取，可能导致异常",Toast.LENGTH_SHORT).show();
                          // finish();
                           return ;
                       }
                   }
                }
                break ;
            default:
                Toast.makeText(this , "REQUEST CODE ERROR" , Toast.LENGTH_SHORT).show();
                break ;
        }
    }
    /*
        显示进度对话框
         */
    private void showProgressDialog(){
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(this) ;
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
    关闭进度框
     */
    private void closeProgressDialog(){
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }
    /*
      根据传入的地址和类型从服务器上查询省市县的数据
     */
    private void queryFromServer(String address , final String type)
    {
//        Log.i(TAG , "QUERY FROM SERVICE") ;
        showProgressDialog();
        Utils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this , "加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string() ;
                boolean result = false ;
                if ("province".equals(type)){

                   // result = JsonUtils.handleProvinceResponse(responseText) ;
                    Log.i(TAG , "on main response") ;
                    currentProvince = LitePal.where("provinceName = ? " , LOCATION).find(Province.class).get(0) ;

                    String address = "http://guolin.tech/api/china/" + currentProvince.getId() ;
                    //从服务器查询数据
                    queryFromServer(address , "city");
                    return ;

                }else if ("city".equals(type)){
                    result = JsonUtils.handleCityResponse(responseText , currentProvince.getId()) ;

                    cityList = LitePal.where("provinceid = ? " , String.valueOf(currentProvince.getId())).find(City.class) ;
                    if (cityList.size() > 0 ) {
                        currentCity = cityList.get(0);
                        int provinceCode = currentProvince.getId() ;
                        int cityCode = currentCity.getCityCode() ;
                        String address = "http://guolin.tech/api/china/" + provinceCode +"/" +cityCode ;
                        //从服务器查询数据
                        queryFromServer(address , "county");
                        closeProgressDialog();
                        return ;

                    }else {
                        Toast.makeText(MainActivity.this , "获取位置出现错误" , Toast.LENGTH_SHORT).show();
                        closeProgressDialog();

                        return ;
                    }

                }else if ("county".equals(type)){
                    result = JsonUtils.handleCountyResponse(responseText , currentCity.getId()) ;
                    countyList = LitePal.where("cityid = ?" ,String.valueOf(currentCity.getId())).find(County.class) ;
                    if (countyList.size() > 0 )
                    {
                        weatherId = countyList.get(0).getWeatherId() ;
                        requestWeather(weatherId);
                        Log.i(TAG , "获取位置成功 " + LOCATION) ;
                    }
                }


            }
        });
    }
    public  class BingPicBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("pic" );
            if (data != null)
            Glide.with(MainActivity.this).load(data).into(bing_pic_ima);
            Toast.makeText(MainActivity.this , "必应每日图片更新成功" , Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient.isStarted())
        mLocationClient.stop();

        if (bingPicBroadcastReceiver!=null)
        {
            unregisterReceiver(bingPicBroadcastReceiver);
            bingPicBroadcastReceiver=null ;
        }
    }
}
