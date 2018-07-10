package com.itsmeyuke.smartdl.request;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itsmeyuke.smartdl.model.RequestEntity;

public class FileRequest extends DownloadRequest {

	@Override
	protected FileRequest createRequestObject(String url, Map<String, Object> headers) {
		int totalSize = getContentLength(url, headers);
		List<String> ranges = getRangesByTotalLength(totalSize, 4);
		DownloadRequest downloadRequest = new FileRequest();
		List<RequestEntity> requestEntities = new ArrayList<>();
		for (String range : ranges) {
			Map<String, Object> newHeader = null;
			if(headers != null)
				newHeader  = new HashMap<String, Object>(headers);
			else
				newHeader  = new HashMap<String, Object>();
			newHeader.put("Range", range);
			RequestEntity requestEntity = new RequestEntity(url, newHeader);
			requestEntities.add(requestEntity);
		}
		downloadRequest.setRequestEntities(requestEntities);
		return (FileRequest) downloadRequest;
	}

	private int getContentLength(String url2, Map<String, Object> headers) {
		URLConnection conn = null;
		try {
			URL url = new URL(url2);
			conn = url.openConnection();
			if (headers != null && !headers.isEmpty()) {
				for (String key : headers.keySet()) {
					conn.setRequestProperty(key, headers.get(key).toString());
				}
			}
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).setRequestMethod("HEAD");
			}
			conn.getInputStream();
			return conn.getContentLength();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).disconnect();
			}
		}
	}
	
	private List<String> getRangesByTotalLength(int totalBytes, int numberOfSpits) {
		
		int quotient = totalBytes / numberOfSpits;
		int reminder = totalBytes % numberOfSpits;
		List<String> out = new ArrayList<>();
		for (int i = 0; i < numberOfSpits; i++) {
			int start = i * quotient;
			int end =  start + quotient - 1;
			if(i == numberOfSpits-1)
				end = end + reminder + 1;
			
			String range = "bytes=" + start + "-" + end;
			out.add(range);
		}
		return out;
	}

}
