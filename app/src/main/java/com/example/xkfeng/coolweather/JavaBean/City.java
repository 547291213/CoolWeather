package com.example.xkfeng.coolweather.JavaBean;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 * Created by initializing on 2018/6/14.
 */

public class City extends LitePalSupport {
    @SerializedName("id")
    private int id ;

    @SerializedName("name")
    private String cityName ;

    private int cityCode ;

    private int provinceId ;

    @Override
    protected long getBaseObjId() {
        return super.getBaseObjId();
    }

    @Override
    public void setToDefault(String fieldName) {
        super.setToDefault(fieldName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

}
