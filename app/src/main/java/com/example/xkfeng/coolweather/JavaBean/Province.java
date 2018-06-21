package com.example.xkfeng.coolweather.JavaBean;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 * Created by initializing on 2018/6/14.
 */

public class Province extends LitePalSupport{
    @SerializedName("id")
    private int id ;

    @SerializedName("name")
    private String provinceName ;

    private int provinceCode ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }


}
