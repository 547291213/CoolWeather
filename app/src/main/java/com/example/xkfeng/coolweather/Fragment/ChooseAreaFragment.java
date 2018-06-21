package com.example.xkfeng.coolweather.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.coolweather.JavaBean.City;
import com.example.xkfeng.coolweather.JavaBean.County;
import com.example.xkfeng.coolweather.JavaBean.Province;
import com.example.xkfeng.coolweather.MainActivity;
import com.example.xkfeng.coolweather.R;
import com.example.xkfeng.coolweather.Utils.JsonUtils;
import com.example.xkfeng.coolweather.Utils.Utils;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by initializing on 2018/6/14.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0 ;
    public static final int LEVEL_CITY = 1 ;
    public static final int LEVEL_COUNTY = 2 ;
    private ProgressDialog progressDialog ;
    private Button backButton ;
    private TextView titleText ;
    private ListView listView ;
    private ListView listView1 ;
    private ListView listView2 ;
    private ArrayAdapter<String> adapter ;
    private ArrayAdapter<String> adapter1 ;
    private ArrayAdapter<String> adapter2 ;
    private List<String> dataList = new ArrayList<>() ;
    private List<String> dataList1 = new ArrayList<>() ;
    private List<String> dataList2 = new ArrayList<>() ;

    private TextView provinceText , cityText ,countyText ;
    private View lineView1 , lineView2 ,lineView3 ;
    /*
       省列表
     */
    private List<Province> provinceList ;
    /*
    市列表
     */
    private List<City> cityList ;
    /*
    县列表
     */
    private List<County> countyList ;

    /*
    选中的省份
     */
    private Province selectedProvince ;
    /*
    选中的城市
     */
    private City selectedCity ;
    /*
    当前选中的级别
     */
    private int currentLevel ;

    private static final String TAG = "ChooseAreaFragment" ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area , container , false) ;
        titleText = (TextView)view.findViewById(R.id.title_text) ;
        backButton= (Button)view.findViewById(R.id.back_button) ;
        listView = (ListView) view.findViewById(R.id.list_view) ;
        listView1 = (ListView)view.findViewById(R.id.list_view1) ;
        listView2 = (ListView) view.findViewById(R.id.list_view2) ;

        adapter = new ArrayAdapter<String>(getContext() , R.layout.simple_list_item_1 , dataList) ;
        adapter1 = new ArrayAdapter<String>(getContext() , R.layout.simple_list_item_1 , dataList1) ;
        adapter2 = new ArrayAdapter<String>(getContext() , R.layout.simple_list_item_1 , dataList2) ;

        provinceText = (TextView)view.findViewById(R.id.province_text) ;
        cityText = (TextView)view.findViewById(R.id.city_text) ;
        countyText = (TextView)view.findViewById(R.id.county_text) ;

        lineView1 = (View)view.findViewById(R.id.line_view1) ;
        lineView2 = (View)view.findViewById(R.id.line_view2) ;
        lineView3 = (View)view.findViewById(R.id.line_view3) ;

        Log.i(TAG , "THE SIZE IS " + dataList.size()) ;
        listView.setAdapter(adapter);
        ;

        return  view ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){

                    selectedProvince = provinceList.get(position) ;
                    String data = dataList.get(position) ;
                    adapter.clear();
                    adapter.add(data);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                    queryCities();
//                }else if (currentLevel == LEVEL_CITY){
//                    selectedCity = cityList.get(position) ;
//                    queryCounties();
//
//                }else if (currentLevel == LEVEL_COUNTY) {
//
//                    String weatherId = countyList.get(position).getWeatherId() ;
////                    if (getActivity() instanceof ChooseActivity)
////                    {
////                        Intent intent = new Intent(getActivity() , MainActivity.class) ;
////                        intent.putExtra("weather_id" ,weatherId ) ;
////                        startActivity(intent);
////                        getActivity().finish();
////                    }else
//                     if (getActivity() instanceof  MainActivity)
//                    {
//                        MainActivity activity = (MainActivity)getActivity() ;
//                        activity.drawerLayout.closeDrawers();
//                        activity.swipeRefreshLayout.setRefreshing(true);
//                        activity.requestWeather(weatherId);
//                    }

                }
            }
        });
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    String data = dataList1.get(position) ;
                    adapter1.clear();
                    adapter1.add(data);
                    adapter1.notifyDataSetChanged();
                    listView1.setSelection(0);
                    queryCounties();

                }
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 if (currentLevel == LEVEL_COUNTY) {

                    String weatherId = countyList.get(position).getWeatherId() ;

                    if (getActivity() instanceof  MainActivity)
                    {
                        MainActivity activity = (MainActivity)getActivity() ;
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY)
                {
                    queryCities();
                }else if (currentLevel == LEVEL_CITY)
                {
                    queryProvince();
                }
            }
        });

        queryProvince();
    }

    /*
    查询所有的省，优先从数据库查询，没有就去服务器上查询
     */
    private void queryProvince(){
        titleText.setText("中国");

        listView1.setVisibility(View.INVISIBLE);
        listView2.setVisibility(View.INVISIBLE);
        lineView1.setVisibility(View.VISIBLE);
        lineView2.setVisibility(View.INVISIBLE);
        lineView3.setVisibility(View.INVISIBLE);
        cityText.setVisibility(View.INVISIBLE);
        countyText.setVisibility(View.INVISIBLE);

        backButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class) ;

        if (provinceList.size() > 0)
        {
            dataList.clear();

            for (Province province : provinceList){
                dataList.add(province.getProvinceName()) ;
              //  Log.i(TAG ,"PROVINCE IS EXCUTE" + province.getProvinceName()) ;

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE ;
        }else {

            String address = "http://guolin.tech/api/china" ;
            //从服务器查询数据
            queryFromServer(address , "province");
        }
    }

    /*
    查询省内所有的市，优先从数据库查询，如果没有查询到，就去服务器上查询
     */
    private void queryCities(){
        listView1.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.INVISIBLE);
        lineView1.setVisibility(View.INVISIBLE);
        lineView2.setVisibility(View.VISIBLE);
        lineView3.setVisibility(View.INVISIBLE);
        cityText.setVisibility(View.VISIBLE);
        countyText.setVisibility(View.INVISIBLE);

        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ? " , String.valueOf(selectedProvince.getId())).find(City.class) ;
        if (cityList.size() > 0 )
        {
            dataList1.clear();
            for (City city : cityList)
            {
                Log.i(TAG ,"City IS EXCUTE" + city.getId() + city.getCityName() + city.getCityCode()) ;
                dataList1.add(city.getCityName()) ;
            }
            adapter1.notifyDataSetChanged();
            listView1.setAdapter(adapter1);
            listView1.setSelection(0);
            //listView.setSelection(0);
            currentLevel=LEVEL_CITY ;
        }else {
            int provinceCode = selectedProvince.getProvinceCode() ;

            String address = "http://guolin.tech/api/china/" + selectedProvince.getId() ;
            //从服务器查询数据
            queryFromServer(address , "city");
        }

    }

    /*
       查询选中市内所有的县
     */
    private void queryCounties()
    {
        listView1.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.VISIBLE);
        lineView1.setVisibility(View.INVISIBLE);
        lineView2.setVisibility(View.INVISIBLE);
        lineView3.setVisibility(View.VISIBLE);
        cityText.setVisibility(View.VISIBLE);
        countyText.setVisibility(View.VISIBLE);

        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid = ?" ,String.valueOf(selectedCity.getId())).find(County.class) ;
        if (countyList.size() > 0 )
        {
            dataList2.clear();
            for (County county : countyList)
            {
                dataList2.add(county.getCountryName()) ;
            }
            adapter2.notifyDataSetChanged();
            listView2.setAdapter(adapter2);
            listView2.setSelection(0);
//            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY ;

        }else {
            int provinceCode = selectedProvince.getId() ;
            int cityCode = selectedCity.getCityCode() ;
            String address = "http://guolin.tech/api/china/" + provinceCode +"/" +cityCode ;
            Log.i(TAG ,"COUNTY IS " + selectedProvince.getId() +"  "+ selectedCity.getCityCode()) ;

            //从服务器查询数据
            queryFromServer(address , "county");

        }

    }
    /*
    显示进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(getActivity()) ;
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
         /*
        对网络状态进行判断
         */
        if (!Utils.JudgeNetState(getContext())){
            Toast.makeText(getContext() , "当前处于没有网络的状态，获取失败" ,Toast.LENGTH_SHORT).show();
            return ;
        }
        Log.i(TAG , "QUERY FROM SERVICE") ;
        showProgressDialog();
        Utils.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext() , "加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string() ;
                Log.i(TAG , "THE TEXT IS " + responseText) ;
                boolean result = false ;
                if ("province".equals(type)){
                    result = JsonUtils.handleProvinceResponse(responseText) ;
                }else if ("city".equals(type)){

                    result = JsonUtils.handleCityResponse(responseText , selectedProvince.getId()) ;
                }else if ("county".equals(type)){
                    result = JsonUtils.handleCountyResponse(responseText , selectedCity.getId()) ;
                }

                if (result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

}
