package com.itsmeyuke.smartdl.model;

public class DownloadStatus {

	double currentSpeed;
	int totalSize;
	int downloaded;
	
	public DownloadStatus() {
		super();
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(double currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}

	@Override
	public String toString() {
		return "DownloadStatus [currentSpeed=" + currentSpeed + ", totalSize=" + totalSize + ", downloaded="
				+ downloaded + "]";
	}

}
