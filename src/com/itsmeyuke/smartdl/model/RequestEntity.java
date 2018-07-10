package com.itsmeyuke.smartdl.model;

import java.util.Map;

public class RequestEntity {
	
	String url;
	Map<String, Object> headers;
	
	public RequestEntity() {
		super();
	}
	
	public RequestEntity(String url, Map<String, Object> headers) {
		super();
		this.url = url;
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}
	
}
