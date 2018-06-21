package com.example.xkfeng.coolweather.JavaBean;

import android.view.MotionEvent;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by initializing on 2018/6/19.
 */

public class Weather {
    public String status ;

    public Basic basic ;

    public AQI aqi ;

    public Now now ;

    public Suggestion suggestion ;

    @SerializedName("daily_forecast")
    public List<Forecast> forecasts ;

    public class Basic{

        @SerializedName("city")
        public String cityName ;

        @SerializedName("id")
        public String weatherId ;

        public Update update ;

        public class Update{

            @SerializedName("loc")
            public String updateTime ;
        }
    }

    public class AQI{
        public AQICity  city ;

        public class AQICity{

            public String aqi ;

            public String pm25 ;
        }
    }

    public class Now{

        @SerializedName("tmp")
        public String temperature ;

        @SerializedName("cond")
        public More more ;

        public class More{

            @SerializedName("txt")
            public String info ;
        }
    }

    public class Suggestion{

        @SerializedName("comf")
        public Comfort comfort ;

        @SerializedName("cw")
        public CarWash carWash ;

        public Sport sport ;

        public class Comfort {

            @SerializedName("txt")
            public String info ;
        }
        public class CarWash {
            @SerializedName("txt")
            public String info ;
        }
        public class Sport {
            @SerializedName("txt")
            public String info ;
        }
    }

    public class Forecast{

        public String date ;

        @SerializedName("tmp")
        public Temperature temperature ;

        @SerializedName("cond")
        public More more ;

        public class Temperature {
            public String max ;

            public String min ;
        }

        public class More {

            @SerializedName("txt_d")
            public String info ;
        }
    }
}
