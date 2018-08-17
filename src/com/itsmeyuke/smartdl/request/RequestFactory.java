package com.itsmeyuke.smartdl.request;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.itsmeyuke.smartdl.core.SmartDL;

public class RequestFactory {

	private static final String M3U8_MIME = "application/x-mpegURL";
	private static final String MPD_MIME = "application/dash+xml";
	
	public enum RequestType {
		FILE, M3U8, MPD;
	}
	
	public static DownloadRequest getDownloadRequest(SmartDL smartDL) {

		String url = smartDL.getUrl();
		Map<String, Object> headers = smartDL.getHeaders();
		RequestType requestType = smartDL.getRequestAs();
		String mimeType = null;
		if(requestType == RequestType.M3U8)
			mimeType = M3U8_MIME;
		if(requestType == RequestType.MPD)
			mimeType = MPD_MIME;
		if(requestType == null)
			mimeType = getMimeType(url, headers);
		if(M3U8_MIME.equalsIgnoreCase(mimeType))
			return new M3U8Request().createRequestObject(smartDL);
		else if(MPD_MIME.equalsIgnoreCase(mimeType))
			return new MPDRequest().createRequestObject(smartDL);
		else
			return new FileRequest().createRequestObject(smartDL);
	}

	private static String getMimeType(String urlString, Map<String, Object> headers) {

		HttpURLConnection httpURLConnection = null;
		HttpURLConnection.setFollowRedirects(true);
		try {
			URL url = new URL(urlString);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("HEAD");
			if(headers != null && !headers.isEmpty()) {
				for (String key : headers.keySet()) {
					httpURLConnection.setRequestProperty(key, headers.get(key).toString());
				}
			}
			httpURLConnection.getInputStream();
			return httpURLConnection.getContentType();
		} catch (IOException e) {
			try {
				URL url = new URL(urlString);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("GET");
				if(headers != null && !headers.isEmpty()) {
					for (String key : headers.keySet()) {
						httpURLConnection.setRequestProperty(key, headers.get(key).toString());
					}
				}
				httpURLConnection.getInputStream();
				return httpURLConnection.getContentType();
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		} finally {
			if (httpURLConnection instanceof HttpURLConnection) {
				((HttpURLConnection) httpURLConnection).disconnect();
			}
		}
	}
}
