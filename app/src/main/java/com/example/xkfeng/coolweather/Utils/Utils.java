package com.example.xkfeng.coolweather.Utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by initializing on 2018/6/14.
 */

public class Utils {

    /*
      通用的网络访问回馈接口
     */
    public static void sendOkHttpRequest(String address , okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient() ;
        Request request = new Request.Builder()
                .url(address)
                .build() ;
        client.newCall(request).enqueue(callback);
    }

    /*
     通用的JSONOBJECT解析接口
     */
    public static <T> T parseJsonObjectWithGson(String jsonData , Class<T> type)
    {
        Gson gson = new Gson() ;
        T result = gson.fromJson(jsonData , type) ;
        return result ;
    }

    /*
      通用的JSONARRAY解析接口
     */
    public static <T>List<T> parseJsonArrayWithGson (String jsonData , Class<T[]> type)
    {
        Gson gson = new Gson() ;
        T[] result = gson.fromJson(jsonData , type) ;
        return(List<T>)  Arrays.asList(result) ;
    }
    /*
       判断网络是否可用
     */
    public static boolean JudgeNetState(Context mContext){
        if(mContext != null)
        {
            //获取ConnectivityManager对象
            Context context =  mContext ;
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE) ;
            //获取NetwORKInfo状态
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo() ;
            if (networkInfo!=null){

                return networkInfo.isAvailable() ;
            }
            return false ;
        }
        return false ;


    }

    /*
       获取网络连接的类型：wifi  mobile之类
     */
    public static int JudgeNetType(Context context){
        if (context != null){

            //虎丘手机的连接管理对象
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            //获取NetInfo的信息
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo() ;
            //判断NetWorkInfo对象是否为null  并且判断状态是否为wifi状态
            if (networkInfo != null && networkInfo.isAvailable())
            {
                return  networkInfo.getType() ;
            }
            return -1 ;
        }
        return -1 ;
    }

}
