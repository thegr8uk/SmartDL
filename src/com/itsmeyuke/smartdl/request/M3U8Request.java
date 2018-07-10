package com.itsmeyuke.smartdl.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itsmeyuke.smartdl.model.RequestEntity;

public class M3U8Request extends DownloadRequest {

	@Override
	protected M3U8Request createRequestObject(String url, Map<String, Object> headers) {

		String m3u8Content = getContent(url, headers);
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

	private String getContent(String completeUrl, Map<String, Object> headers) {

		try {

			String content = new String();
			String html = new String();
			URL url = new URL(completeUrl);

			URLConnection connection = url.openConnection();
			if (headers != null && !headers.isEmpty()) {
				for (String key : headers.keySet()) {
					connection.setRequestProperty(key, headers.get(key).toString());
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
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
		}
		return "";
	}

}
