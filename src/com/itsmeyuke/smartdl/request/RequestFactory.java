package com.itsmeyuke.smartdl.request;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class RequestFactory {

	private static final String M3U8_MIME = "application/x-mpegURL";
	
	public static DownloadRequest getDownloadRequest(String url, Map<String, Object> headers) {

		String mimeType = getMimeType(url, headers);
		if(M3U8_MIME.equalsIgnoreCase(mimeType))
			return new M3U8Request().createRequestObject(url, headers);
		else
			return new FileRequest().createRequestObject(url, headers);
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
			throw new RuntimeException(e);
		} finally {
			if (httpURLConnection instanceof HttpURLConnection) {
				((HttpURLConnection) httpURLConnection).disconnect();
			}
		}
	}
}
