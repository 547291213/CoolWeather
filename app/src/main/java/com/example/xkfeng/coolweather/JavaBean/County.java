package com.example.xkfeng.coolweather.JavaBean;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 * Created by initializing on 2018/6/14.
 */

public class County extends LitePalSupport {

    @SerializedName("id")
    private int id ;

    @SerializedName("name")
    private String countryName ;

    @SerializedName("weather_id")
    private String weatherId ;

    private int cityId ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    protected long getBaseObjId() {
        return super.getBaseObjId();
    }

    @Override
    public void setToDefault(String fieldName) {
        super.setToDefault(fieldName);
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

}
