package com.example.weatherhelper.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.weatherhelper.R;
import com.example.weatherhelper.db.WeatherHelperDB;
import com.example.weatherhelper.model.City;
import com.example.weatherhelper.model.County;
import com.example.weatherhelper.model.Province;
import com.example.weatherhelper.util.HttpCallBackListener;
import com.example.weatherhelper.util.HttpUtil;
import com.example.weatherhelper.util.Utility;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherHelperDB weatherHelperDB;
	private List<String> dataList=new ArrayList<String>();
	
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County>countyList;
	
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView)findViewById(R.id.list_view);
		titleText=(TextView)findViewById(R.id.title_text);
		weatherHelperDB=WeatherHelperDB.getIntance(this);
		 System.out.println("ChooseAreaActivity"+"  execute");
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false)){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEL_PROVINCE){
					selectedProvince=provinceList.get(position);
					queryCities();
				}else if(currentLevel==LEVEL_CITY){
					selectedCity=cityList.get(position);
					queryCounties();
				}else if(currentLevel==LEVEL_COUNTY){
					String countyCode=countyList.get(position).getCountyCode();
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
					}
				
			}
		});
		queryProvinces();
		
		
	}
	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList=weatherHelperDB.loadCountys(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			listView.setSelection(0);
			adapter.notifyDataSetChanged();
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
			 System.out.println("ChooseAreaActivity.queryCounties()"+"  execute");
			 Log.d("ChooseAreaActivity.queryCounties()", "execute");
		}else{
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	private  void queryCities() {
		// TODO Auto-generated method stub
		cityList=weatherHelperDB.loadCitys(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			listView.setSelection(0);
			adapter.notifyDataSetChanged();
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
			System.out.println("ChooseAreaActivity.queryCities()"+"  execute");
			 Log.d("ChooseAreaActivity.queryCities()", "execute");
		}else{
		queryFromServer(selectedProvince.getProvinceCode(),"city");	
		}
	}
	private void queryProvinces() {
		// TODO Auto-generated method stub
		 provinceList=weatherHelperDB.loadProvinces();
		 
		 if(provinceList.size()>0){
			 dataList.clear();
			 for(Province province:provinceList){
			 dataList.add(province.getProvinceName());
			 }
			 listView.setSelection(0);
			 adapter.notifyDataSetChanged();
			 titleText.setText("中国");
			 currentLevel=LEVEL_PROVINCE;
			 System.out.println("ChooseAreaActivity.queryProvince"+"  execute");
			 Log.d("ChooseAreaActivity.queryProvince()", "execute");
		 }else{
			 queryFromServer(null,"province");
		 }
		 
	}
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		System.out.println("ChooseAreaActivity.queryFromServer()"+"  execute");
		 Log.d("ChooseAreaActivity.queryFromServer()", "execute");
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvincesResponse(weatherHelperDB, response);
				}else if("city".equals(type)){
					result=Utility.handleCitiesResponse(weatherHelperDB, response, selectedProvince.getId());
				}else{
					result=Utility.handleCountiesResponse(weatherHelperDB, response, selectedCity.getId());
				}
				if(result){
					//通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							// TODO Auto-generated method stub
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else{
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				//通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT);
					}
				});
			}
		});
	}
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
		
	}
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
	
}
