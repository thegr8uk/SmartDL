package com.itsmeyuke.smartdl.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itsmeyuke.smartdl.core.SmartDL;
import com.itsmeyuke.smartdl.model.RequestEntity;
import com.itsmeyuke.smartdl.utils.ConnectionsUtils;

public class FileRequest extends DownloadRequest {

	@Override
	protected FileRequest createRequestObject(SmartDL smartDL) {
		String url = smartDL.getUrl();
		Map<String, Object> headers = smartDL.getHeaders();
		int totalSize = ConnectionsUtils.getContentLength(url, headers);
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

	public List<String> getRangesByTotalLength(int totalBytes, int numberOfSpits) {
		
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
