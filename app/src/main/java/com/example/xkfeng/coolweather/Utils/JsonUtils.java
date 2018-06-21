package com.example.xkfeng.coolweather.Utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.xkfeng.coolweather.JavaBean.City;
import com.example.xkfeng.coolweather.JavaBean.County;
import com.example.xkfeng.coolweather.JavaBean.Province;
import com.example.xkfeng.coolweather.JavaBean.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by initializing on 2018/6/14.
 */

public class JsonUtils {
    /*
       解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            List<Province> provinceList = Utils.parseJsonArrayWithGson(response , Province[].class) ;
            for (int i = 0 ; i < provinceList.size() ;i++)
            {
                Province province = new Province() ;
                province.setId(provinceList.get(i).getId());
                province.setProvinceCode(provinceList.get(i).getId());
                province.setProvinceName(provinceList.get(i).getProvinceName());
                province.save() ;
            }
            return true ;
        }

        return false ;
    }

    /*
      解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response , int provinceId)
    {
        if (!TextUtils.isEmpty(response))
        {
            List<City> citys = Utils.parseJsonArrayWithGson(response , City[].class) ;
            for (int i = 0 ; i< citys.size() ; i++)
            {
              //  Log.i("ChooseAreaFragment" , "ID IS " + citys.get(i).getId()) ;
                City city = new City() ;
                city.setId(citys.get(i).getId());
                city.setCityCode(citys.get(i).getId());
                city.setCityName(citys.get(i).getCityName());
                city.setProvinceId(provinceId);

                Log.i("ChooseAreaFragment" , "CODE IS " + city.getId() + " CODE IS " + city.getCityCode()) ;
                city.save() ;
            }
            return true ;
        }
        return false ;
    }

    /*
       解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response , int cityId)
    {
        if (!TextUtils.isEmpty(response))
        {
            List<County> counties = Utils.parseJsonArrayWithGson(response , County[].class) ;
            for (int i = 0 ; i < counties.size() ; i++)
            {
                County county = new County() ;
                county.setCountryName(counties.get(i).getCountryName());
                county.setWeatherId(counties.get(i).getWeatherId());
                county.setCityId(cityId);
                county.save() ;
            }

            return true ;
        }
        return false ;
    }

    /*
    将返回的Json数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response)
    {
        try{

            JSONObject jsonObject = new JSONObject(response) ;
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather") ;
            String weatherContent = jsonArray.getJSONObject(0).toString() ;
            return new Gson().fromJson(weatherContent ,Weather.class) ;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null ;
    }
}
