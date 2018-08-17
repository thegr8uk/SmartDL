package com.itsmeyuke.smartdl.test;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itsmeyuke.smartdl.core.DownloadListener;
import com.itsmeyuke.smartdl.core.IntermediateParser;
import com.itsmeyuke.smartdl.core.SmartDL;
import com.itsmeyuke.smartdl.model.DownloadStatus;
import com.itsmeyuke.smartdl.request.RequestFactory;

public class UnitTest implements DownloadListener, IntermediateParser {
	
	@Override
	public void onFinish() {
		System.out.println("Finished");
	}

	@Override
	public void downloadUpdates(DownloadStatus downloadStatus) {
		System.out.println(downloadStatus);
	}

	@Override
	public void onStart() {
		System.out.println("Started");
	}

	@Override
	public String parse(String input) {
		JsonParser parser = new JsonParser(); 
		JsonObject json = (JsonObject) parser.parse(input);
		JsonArray contentResponseList = json.getAsJsonArray("contentResponseList");
		JsonObject contentResponse = contentResponseList.get(0).getAsJsonObject();
		String manifest = contentResponse.get("manifest").getAsString();
		return manifest;
	}
	
	public static void main(String[] args) {
		
		UnitTest test = new UnitTest();
		//test.testM3U8WithNoHeader();
		test.testCustomMPD();
	}

	public void testM3U8WithNoHeader() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Cookie", "hdntl=exp=1533725936~acl=%2fi%2fsongs%2f13%2f1580913%2f16506930%2f16506930_128.mp4%2f*~data=hdntl~hmac=7e9d4cd4c8b0475746e376896b3dbe7a2028fcc8492d105c0d93cd0a689051e6;_alid_=sXsi0gawfDp+JCtDq1eqsQ==;");
		SmartDL smartDL = SmartDL.newDownload()
				.url("https://vodhls-vh.akamaihd.net/i/songs/13/1580913/16506930/16506930_128.mp4/index_0_a.m3u8?set-akamai-hls-revision=5")
				.headers(headers)
				.postData("'")
				.directory("C:\\Users\\Unnikuttan\\Desktop")
				.numberOfParallelConnections(2)
				.setRequestAs(RequestFactory.RequestType.M3U8)
				.downloadListener(this)
				.intermediateParser(this)
				.updatesIntervalInMs(100)
				.build();
		smartDL.execute();
	}
	
	public void testCustomMPD() {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("csrf-rnd", "1857551982");
		headers.put("csrf-ts", "1533813803");
		headers.put("csrf-token", "qfMcFoqI0LS6b7G7dO4q2mrBGrSoiLMqdvFMssIZEeQ=");
		headers.put("Cookie", "session-id=257-1440256-4422858; ubid-acbin=257-4792170-8693721; x-wl-uid=1QgMYSWRTNXro+rBbzfxjZrRizXjLTmV8ax/7MVzUvLkCbF0F/ltU6mpla0SC1KwACHUGliiCgzsMiLVim8IujrL5ASxbcrohgYzc+bhxCaF1kveGYQc4sdabVrOiiTiCj7dS7HSuA3E=; visitCount=31; session-id-time=2082758401l; session-token=\"Pkx4fXpb7mnd8IfUkV0i2a2lQ6VtY/o7oOaGIz3QRZJYht2H9+skXO7wEYo7AT8Yz5VxwzvsV0pxfOMiDa6k1940Fhl3SSXs6Uu12ESDIc6d7RPXfEVSK4vZP2ZsR/3jNjlRoR88XbKbSUZC0mbWh+6xa6gj6vRcnpJjIknyaj2cwWwpZShafJRhRVDGsDo9DmUwH3+QDq2EHm/CfFkjuF8i/vvtlgoocGdViOiPL6kY8SEkbrE6DjtycNjQ/DtFAeTO5Kp0wTs=\"; x-acbin=\"MX5kz@7sxwP0ZXpAUhjovM5zuunA5DW0G4OmBzxd61CaxLxGECzzycKrtArb15EF\"; at-acbin=Atza|IwEBIJEOU5PMT_yBV5i0NBrBjvbnoU6GQFcw0vWMG_zyEE4kFSBQXsEYoDzBTkxdmQzuE_P_RQtQqaFCye-k3XfI5M71-FtW1RElTWmvJxX88PXDp80D7-Merys-c-Ef2P4FFBliDsPFZ9t4p0EQkJph7Y0ZRR7gJ4HwW9JZF12rpFvOoHIz5w-HYX4dXAiLze0iEB2aLOOlljHMijp-G4nw7PWMwOecHNxS-IFFBA4cfkvNfgs2jkEhxJoOF-7tP6IKHHIndU_jdyL6_765loPIl1U7akcl445OdnpgAZgE3NW3DqO40QCNhA9yMqkrgyc9ndx66oQ7aRX5VI9MKCb-ggPWc0_ZsuBdRcFgV4V6pxJbApwsfleB9vQgK5eTEGnb46zMqE7Vw8l8Uv7hHIpxb_M5; sess-at-acbin=\"bYSPn70BjxJok6uvbvkYGGN5kKrS51Opl23FN38GvJw=\"; sst-acbin=Sst1|PQFjjCWGrsq_HcLY4SkGdw88C80tWuYk0dmfxY0Etkq3Y01qlbsz-gvazSDJCeh8GBL2DLWfSj8p604N-5zdCbFVHidR6YzAYlK4Pj2tLh5NnKZafHfoiRR-3TFDRgFswHCLwzAwNV6y1hKOF_zLnTFbG3t9MnDYZgsDoQI2cqTuk37Xr0pM4AIOppg99XGgL5ta0S58zB7oml-KKDvxJZ1OXsMR-o-znO9kfAt8USUv2WuG4SOsFRSTPTdsPW1Hog4yr1WK5T51IMhxEGqoNSPx-j7GVualslKCm0_XWcCfgvL8TMc4F24pMVvtIqAT1HIRVApVJARjU3O5Zmquxy_NAg; csm-hit=YRSEPGYDTRKS564N8AN4+s-YRSEPGYDTRKS564N8AN4|1533813893351");
		headers.put("X-Amz-Target", "com.amazon.digitalmusiclocator.DigitalMusicLocatorServiceExternal.getDashManifests");
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("Content-Encoding", "amz-1.0");
		headers.put("Origin", "https://music.amazon.in");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		String payLoad = "{\"customerId\":\"A2HUP7K0VOK6PK\",\"deviceToken\":{\"deviceTypeId\":\"A16ZV8BU3SN1N3\",\"deviceId\":\"25747921708693721\"},\"appMetadata\":{\"https\":\"true\"},\"clientMetadata\":{\"clientId\":\"WebCP\"},\"contentIdList\":[{\"identifier\":\"B07C4TXYYM\",\"identifierType\":\"ASIN\"}],\"bitRateList\":[\"HIGH\"],\"mpegDashVersion\":\"V1\",\"withInitSegment\":true}";
		SmartDL smartDL = SmartDL.newDownload()
				.url("https://music.amazon.in/EU/api/dmls/")
				.headers(headers)
				.postData(payLoad)
				.directory("C:\\Users\\Unnikuttan\\Desktop\\AMZNOut")
				.fileName("Rasaathi.m4a")
				.numberOfParallelConnections(4)
				.setRequestAs(RequestFactory.RequestType.MPD)
				.downloadListener(this)
				.intermediateParser(this)
				.updatesIntervalInMs(100)
				.build();
		smartDL.execute();
	}

}
