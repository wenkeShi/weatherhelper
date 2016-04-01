package com.example.weatherhelper.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallBackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					System.out.println(response.toString());
					Log.d("sendHttpRequest", response.toString());
					if(listener!=null){
						//调用onFinish()方法
						
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					// TODO: handle exception
					if(listener!=null){
						listener.onError(e);
					}
				}finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
