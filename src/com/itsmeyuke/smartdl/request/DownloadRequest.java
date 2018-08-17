package com.itsmeyuke.smartdl.request;

import java.util.List;
import java.util.Map;

import com.itsmeyuke.smartdl.core.SmartDL;
import com.itsmeyuke.smartdl.model.RequestEntity;

public abstract class DownloadRequest {

	protected abstract DownloadRequest createRequestObject(SmartDL smartDL);
	
	private List<RequestEntity> requestEntities;

	public DownloadRequest() {
		super();
	}

	public DownloadRequest(List<RequestEntity> requestEntities) {
		super();
		this.requestEntities = requestEntities;
	}

	public List<RequestEntity> getRequestEntities() {
		return requestEntities;
	}

	public void setRequestEntities(List<RequestEntity> requestEntities) {
		this.requestEntities = requestEntities;
	}
	
}
