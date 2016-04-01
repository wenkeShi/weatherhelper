package com.example.weatherhelper.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.weatherhelper.db.WeatherHelperDB;
import com.example.weatherhelper.model.City;
import com.example.weatherhelper.model.County;
import com.example.weatherhelper.model.Province;

public class Utility {
	public synchronized static boolean handleProvincesResponse(WeatherHelperDB weatherHelperDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if(allProvinces.length>0&&allProvinces!=null){
				for(String p:allProvinces ){
					String[] provinceStr=p.split("\\|");
					Province province=new Province();
					province.setProvinceName(provinceStr[1]);
					province.setProvinceCode(provinceStr[0]);
					weatherHelperDB.saveProvince(province);
					}
				return true;
				}
		}
		return false;
	}
	
	public static boolean handleCitiesResponse(WeatherHelperDB weatherHelperDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(",");
			if(allCities!=null&&allCities.length>0){
				for(String c:allCities){
					String[] cityStr=c.split("\\|");
					City city=new City();
					city.setCityCode(cityStr[0]);
					city.setCityName(cityStr[1]);
					city.setProvinceId(provinceId);
					weatherHelperDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleCountiesResponse(WeatherHelperDB weatherHelperDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(",");
			if(allCounties!=null&&allCounties.length>0){
				for(String cn:allCounties){
					String[] countyStr=cn.split("\\|");
					County county=new County();
					county.setCountyCode(countyStr[0]);
					county.setCountyName(countyStr[1]);
					county.setCityId(cityId);
					weatherHelperDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
		
	}
}
