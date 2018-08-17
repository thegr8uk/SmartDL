package com.itsmeyuke.smartdl.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ConnectionsUtils {
	
	public static String getContent(String completeUrl, Map<String, Object> headers, String payLoad) {

		String html = new String();
		HttpURLConnection httpURLConnection = null;
		
		try {

			URL url = new URL(completeUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			if (headers != null && !headers.isEmpty()) {
				for (String key : headers.keySet()) {
					httpURLConnection.setRequestProperty(key, headers.get(key).toString());
				}
			}
			if(payLoad != null) {
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestMethod("POST");
				try(DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
					dataOutputStream.write(payLoad.getBytes());
				}
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
			String content = new String();
			while ((content = br.readLine()) != null) {
				if (!content.equals("") && content.length() != 0)
					html += content.trim() + "\r\n";
			}

			return html;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpURLConnection.disconnect();
		}
		return html;
	}
	
	public static int getContentLength(String completeUrl, Map<String, Object> headers) {
		
		HttpURLConnection httpURLConnection = null;
		try {
			URL url = new URL(completeUrl);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			if (headers != null && !headers.isEmpty()) {
				for (String key : headers.keySet()) {
					httpURLConnection.setRequestProperty(key, headers.get(key).toString());
				}
			}
			httpURLConnection.setRequestMethod("HEAD");
			httpURLConnection.getInputStream();
			return httpURLConnection.getContentLength();
		} catch (IOException e) {
			try {
				URL url = new URL(completeUrl);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				if (headers != null && !headers.isEmpty()) {
					for (String key : headers.keySet()) {
						httpURLConnection.setRequestProperty(key, headers.get(key).toString());
					}
				}
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.getInputStream();
				return httpURLConnection.getContentLength();
			} catch(Exception e1) {
				throw new RuntimeException(e1);
			}
		} finally {
			httpURLConnection.disconnect();
		}
	}
	
}
