package com.example.xkfeng.coolweather.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.xkfeng.coolweather.JavaBean.Weather;
import com.example.xkfeng.coolweather.Utils.JsonUtils;
import com.example.xkfeng.coolweather.Utils.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by initializing on 2018/6/20.
 */

public class AutoUpdateService extends Service {

    private static final String TAG = "AutoUpdateService" ;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateBingPic();
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE) ;
        final int HOUR = 8 * 60 * 60 * 1000 ;
        long triggerAtTime = SystemClock.elapsedRealtime() + HOUR ;
        Intent intent1 = new Intent(this , AutoUpdateService.class) ;
        PendingIntent pi = PendingIntent.getService(this , 0 , intent1 , 0) ;
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , triggerAtTime , pi);
        Log.i(TAG , "SERVICE IS START") ;
        return super.onStartCommand(intent, flags, startId);
    }

    /*
    更新天气信息
     */
    private void updateWeather(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this) ;
        String weatherString = sharedPreferences.getString("weather" , null) ;
        if (weatherString != null){
            //有缓存的时候直接去读取天气信息
            final Weather weather = JsonUtils.handleWeatherResponse(weatherString) ;
            String weatherId = weather.basic.weatherId ;
            String url = "http://guolin.tech/api/weather?cityid=" + weatherId +"&key=722dda481604441db9967f3fabd76ed1" ;
            Utils.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseText = response.body().string() ;
                    Weather weather1 = JsonUtils.handleWeatherResponse(responseText) ;
                    if (weather!=null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit() ;
                        editor.putString("weather" , responseText) ;
                        editor.apply();


                    }
                }
            });
        }
    }

    /*
    更新必应每日一图
     */
    private void updateBingPic(){
        final String requestBingPic = "http://guolin.tech/api/bing_pic" ;
        Utils.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String bingPic = response.body().string() ;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit() ;
                editor.putString("bing_pic" , bingPic) ;
                editor.apply();


            }
        });
    }
}
