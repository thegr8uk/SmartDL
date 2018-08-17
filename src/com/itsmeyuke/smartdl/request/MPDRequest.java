package com.itsmeyuke.smartdl.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itsmeyuke.smartdl.core.IntermediateParser;
import com.itsmeyuke.smartdl.core.SmartDL;
import com.itsmeyuke.smartdl.model.RequestEntity;
import com.itsmeyuke.smartdl.utils.ConnectionsUtils;

public class MPDRequest extends DownloadRequest {

	@Override
	protected DownloadRequest createRequestObject(SmartDL smartDL) {

		String url = smartDL.getUrl();
		Map<String, Object> headers = smartDL.getHeaders();
		String mpdContent = ConnectionsUtils.getContent(url, headers, smartDL.getPostData());
		List<String> urls = getStreams(url, mpdContent, smartDL.getIntermediateParser());

		DownloadRequest downloadRequest = new MPDRequest();
		List<RequestEntity> requestEntities = new ArrayList<>();
		for (String streamUrl : urls) {
			RequestEntity requestEntity = new RequestEntity(streamUrl, headers);
			requestEntities.add(requestEntity);
		}
		downloadRequest.setRequestEntities(requestEntities);
		return (MPDRequest) downloadRequest;
	}

	private List<String> getStreams(String url, String mpdContent, IntermediateParser intermediateParser) {

		if (intermediateParser != null)
			mpdContent = intermediateParser.parse(mpdContent);

		List<String> streams = new ArrayList<>();
		String initUrl = "";
		Pattern pattern = Pattern.compile("<Initialization\\s+sourceURL=\"(.+?)\"\\s?/>");
		Matcher matcher = pattern.matcher(mpdContent);
		if (matcher.find()) {
			initUrl = matcher.group(1);
			initUrl = initUrl.replaceAll("&amp;", "&");
		}
		streams.add(initUrl);

		pattern = Pattern.compile("<SegmentURL\\s+media=\"(.+?)\"\\s?/>");
		matcher = pattern.matcher(mpdContent);
		while (matcher.find()) {
			initUrl = matcher.group(1);
			initUrl = initUrl.replaceAll("&amp;", "&");
			streams.add(initUrl);
		}

		return streams;
	}
}
