package com.example.weatherhelper.util;

public interface HttpCallBackListener {
	void onFinish(String response);
	void onError(Exception e);
}
