package com.itsmeyuke.smartdl.core;

import com.itsmeyuke.smartdl.model.DownloadStatus;

public interface DownloadListener {
	
	public void downloadUpdates(DownloadStatus downloadStatus);
	
	public void onFinish();
	
	public void onStart();
	
}
