package com.itsmeyuke.smartdl.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itsmeyuke.smartdl.core.SmartDL;
import com.itsmeyuke.smartdl.model.RequestEntity;
import com.itsmeyuke.smartdl.utils.ConnectionsUtils;

public class M3U8Request extends DownloadRequest {

	@Override
	protected M3U8Request createRequestObject(SmartDL smartDL) {

		String url = smartDL.getUrl();
		Map<String, Object> headers = smartDL.getHeaders();
		String m3u8Content = ConnectionsUtils.getContent(url, headers, smartDL.getPostData());
		List<String> urls = getTsUrls(url, m3u8Content);

		DownloadRequest downloadRequest = new M3U8Request();
		List<RequestEntity> requestEntities = new ArrayList<>();
		for (String tsUrl : urls) {
			RequestEntity requestEntity = new RequestEntity(tsUrl, headers);
			requestEntities.add(requestEntity);
		}
		downloadRequest.setRequestEntities(requestEntities);
		return (M3U8Request) downloadRequest;
	}

	private List<String> getTsUrls(String url, String m3u8Content) {

		List<String> tsUrls = new ArrayList<>();
		Pattern pattern = Pattern.compile("^([^#].+?)$", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(m3u8Content);
		while (matcher.find()) {
			String tsPath = matcher.group(0);
			if (tsPath.toLowerCase().startsWith("http://"))
				tsUrls.add(tsPath);
			else if (tsPath.toLowerCase().startsWith("https://"))
				tsUrls.add(tsPath);
			else {
				try {
					String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
					String path = url.replace(fileName, "");
					tsPath = path + tsPath;
					tsUrls.add(tsPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return tsUrls;
	}

	
}
