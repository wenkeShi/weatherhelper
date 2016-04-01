package com.example.weatherhelper.db;

import java.util.ArrayList;
import java.util.List;

import com.example.weatherhelper.model.City;
import com.example.weatherhelper.model.County;
import com.example.weatherhelper.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherHelperDB {
	private WeatherOpenHelper weatherOpenHelper;
	private static WeatherHelperDB weatherHelperDB;
	private SQLiteDatabase db;
	private static final String DBNAME="weatherhelper";
	private static final int VERSION=1;
	/**
	 * 构造函数私有化
	 * @param context
	 */
	private WeatherHelperDB(Context context){
		weatherOpenHelper=new WeatherOpenHelper(context, DBNAME, null, VERSION);
		db=weatherOpenHelper.getWritableDatabase();
	}
	public static synchronized  WeatherHelperDB getIntance(Context context){
		if(weatherHelperDB==null){
			weatherHelperDB=new WeatherHelperDB(context);
		}
		return weatherHelperDB;
	}
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues contentValues=new ContentValues();
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			db.insert("Procince", null, contentValues);
		}
	}
	public List<Province> loadProvinces(){
		List<Province> provinceList=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province=new Province();
				String provinceName=cursor.getString(cursor.getColumnIndex("province_name"));
				String provinceCode=cursor.getString(cursor.getColumnIndex("province_code"));
				int id=cursor.getInt(cursor.getColumnIndex("id"));
				province.setId(id);
				province.setProvinceCode(provinceCode);
				province.setProvinceName(provinceName);
				provinceList.add(province);
			}while(cursor.moveToNext());
		}
		return provinceList;
	}
	public void saveCity(City city){
		if(city!=null){
			ContentValues contentValues=new ContentValues();
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId());
			db.insert("City", null, contentValues);
		}
	}
	public List<City> loadCitys(int province_id){
		List<City> cityList=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", new String[]{String.valueOf(province_id)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				cityList.add(city);
			}while(cursor.moveToNext());
		}
		return cityList;
	}
	public void saveCounty(County county){
		if(county!=null){
			ContentValues contentValues=new ContentValues();
			contentValues.put("county_name", county.getCountyName());
			contentValues.put("county_code", county.getCountyCode());
			contentValues.put("city_id", county.getCityId());
			db.insert("County", null, contentValues);
		}
	}
	public List<County> loadCountys(int cityId){
		List<County> countyList=new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_id=?",new String[]{String.valueOf(cityId)} , null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				countyList.add(county);
			}while(cursor.moveToNext());
		}
		return countyList;
	}
}
