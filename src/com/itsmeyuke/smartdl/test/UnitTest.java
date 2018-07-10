package com.itsmeyuke.smartdl.test;

import com.itsmeyuke.smartdl.core.DownloadListener;
import com.itsmeyuke.smartdl.core.SmartDL;
import com.itsmeyuke.smartdl.model.DownloadStatus;

public class UnitTest implements DownloadListener{

	public static void main(String[] args) {
		
		UnitTest test = new UnitTest();
		test.doJob();
	}

	public void doJob() {
		SmartDL smartDL = SmartDL.newDownload()
				.url("https://musicaudiohls-a.erosnow.com/hls/music/5/1039665/musicaudio/6548454/320/1039665_6548454_0_0_320_0_.m3u8")
				.directory("C:\\Users\\Unnikuttan\\Desktop")
				.numberOfParallelConnections(2)
				.downloadListener(this)
				.updatesIntervalInMs(100)
				.build();
		smartDL.execute();
	}
	
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

}
