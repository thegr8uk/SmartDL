package com.itsmeyuke.smartdl.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;

import com.itsmeyuke.smartdl.model.DownloadStatus;
import com.itsmeyuke.smartdl.model.MyBoolean;
import com.itsmeyuke.smartdl.model.RequestEntity;
import com.itsmeyuke.smartdl.request.DownloadRequest;
import com.itsmeyuke.smartdl.request.M3U8Request;
import com.itsmeyuke.smartdl.request.RequestFactory;

public final class SmartDL {
	
	private String directory;
	private String fileName;
	private String fullPath;
	private String url;
	private Map<String, Object> headers;
	private int numberOfChunks;
	private int numberOfParallelConnections;
	private DownloadListener downloadListener;
	private int updatesIntervalInMs;

	private SmartDL(Builder builder) {
		setDirectory(builder.directory);
		setFileName(builder.fileName);
		setFullPath(builder.fullPath);
		setUrl(builder.url);
		setHeaders(builder.headers);
		setNumberOfChunks(builder.numberOfChunks);
		setNumberOfParallelConnections(builder.numberOfParallelConnections);
		setDownloadListeners(builder.downloadListener);
		setUpdatesIntervalInMs(builder.updatesIntervalInMs);
	}

	public static Builder newDownload() {
		return new Builder();
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
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

	public int getNumberOfChunks() {
		return numberOfChunks;
	}

	public void setNumberOfChunks(int numberOfChunks) {
		this.numberOfChunks = numberOfChunks;
	}

	public int getNumberOfParallelConnections() {
		return numberOfParallelConnections;
	}

	public void setNumberOfParallelConnections(int numberOfParallelConnections) {
		this.numberOfParallelConnections = numberOfParallelConnections;
	}

	public DownloadListener getDownloadListeners() {
		return downloadListener;
	}

	public void setDownloadListeners(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}
	
	public int getUpdatesIntervalInMs() {
		return updatesIntervalInMs;
	}

	public void setUpdatesIntervalInMs(int updatesIntervalInMs) {
		this.updatesIntervalInMs = updatesIntervalInMs;
	}
	
	public static final class Builder {
		
		private String directory;
		private String fileName;
		private String fullPath;
		private String url;
		private Map<String, Object> headers;
		private int numberOfChunks;
		private int numberOfParallelConnections;
		private DownloadListener downloadListener;
		private int updatesIntervalInMs;

		private Builder() {
		}

		public Builder directory(String directory) {
			this.directory = directory;
			return this;
		}

		public Builder fileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public Builder fullPath(String fullPath) {
			this.fullPath = fullPath;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}
		
		public Builder headers(Map<String, Object> headers) {
			this.headers = headers;
			return this;
		}
		
		public Builder numberOfChunks(int numberOfChunks) {
			this.numberOfChunks = numberOfChunks;
			return this;
		}
		
		public Builder numberOfParallelConnections(int numberOfParallelConnections) {
			this.numberOfParallelConnections = numberOfParallelConnections;
			return this;
		}
		
		public Builder downloadListener(DownloadListener downloadListener) {
			this.downloadListener = downloadListener;
			return this;
		}
		
		public Builder updatesIntervalInMs(int updatesIntervalInMs) {
			this.updatesIntervalInMs = updatesIntervalInMs;
			return this;
		}

		public SmartDL build() {
			return new SmartDL(this);
		}
	}

	public void execute() {
		
		DownloadRequest downloadRequest = RequestFactory.getDownloadRequest(this.url, this.headers);
		int numberOfThreads = this.numberOfParallelConnections == 0 ? 5 : this.numberOfParallelConnections;
		DownloadListener downloadListener2 = this.downloadListener;
		if(downloadListener2 != null)
			downloadListener2.onStart();
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		List<Future<DownloadObject>> list = new ArrayList<Future<DownloadObject>>();
		List<RequestEntity> requestEntities = downloadRequest.getRequestEntities();
		List<DownloadThread> myDownloadThreads = new ArrayList<>();
		
		for(int i=0; i < requestEntities.size(); i++){
			DownloadThread downloadThread = new DownloadThread(i, requestEntities.get(i).getUrl(), requestEntities.get(i).getHeaders());
			myDownloadThreads.add(downloadThread);
        }
		
		final int updateIntervalFinal = this.updatesIntervalInMs == 0 ? 250 : this.updatesIntervalInMs;
		
		MyBoolean downloadFinished = new MyBoolean(false);
		int[] totalSizeFinal = new int[1]; 
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				while(!downloadFinished.getValue()) {
					double totalSpeed = 0;
					int totalSize = 0;
					int downloadedSize = 0;
					for (DownloadThread downloadThread : myDownloadThreads) {
						totalSpeed += (downloadThread.currentSpeed == null ? 0 : downloadThread.currentSpeed);
						totalSize += downloadThread.totalSize;
						downloadedSize += downloadThread.downloadedSize;
						
					}
					if(totalSpeed != 0.0D) {
						DownloadStatus downloadStatus = new DownloadStatus();
						downloadStatus.setCurrentSpeed(totalSpeed);
						downloadStatus.setTotalSize(totalSize);
						downloadStatus.setDownloaded(downloadedSize);
						totalSizeFinal[0] = totalSize;
						if(downloadListener2 != null)
							downloadListener2.downloadUpdates(downloadStatus);
					}
					try {
						Thread.sleep(updateIntervalFinal);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		for(int i=0; i < myDownloadThreads.size(); i++){
			DownloadThread downloadThread = myDownloadThreads.get(i);
            Future<DownloadObject> future = executor.submit(downloadThread);
            list.add(future);
        }
		
		new Thread(runnable).start();
		
		String absoluteFilePath = "";
		if(this.fullPath != null) {
			absoluteFilePath = this.fullPath;
		} else {
			String dir = this.directory == null ? "" : this.directory;
			String fileName = this.fileName == null ? getFileNameFromUrl(this.url, downloadRequest) : this.fileName;
			absoluteFilePath = dir + File.separator + fileName;
		}
		File f = new File(absoluteFilePath);
		for(Future<DownloadObject> fut : list){
            try {
            	DownloadObject a = fut.get();
            	FileUtils.writeByteArrayToFile(f, a.content.array(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		downloadFinished.setValue(true);
		executor.shutdown();
		DownloadStatus downloadStatus = new DownloadStatus();
		downloadStatus.setCurrentSpeed(0.0);
		downloadStatus.setTotalSize(totalSizeFinal[0]);
		downloadStatus.setDownloaded(totalSizeFinal[0]);
		if(downloadListener2 != null)
			downloadListener2.downloadUpdates(downloadStatus);
		if(downloadListener2 != null)
			downloadListener2.onFinish();
	}

	private String getFileNameFromUrl(String url2, DownloadRequest downloadRequest) {
		if(downloadRequest instanceof M3U8Request)
			return getFileNameFromUrl(url2) + ".ts";
		else
			return getFileNameFromUrl(url2) + getExtensionFromUrl(url2);
	}
	
	private String getExtensionFromUrl(String uri) {
		String extension = ".ext";
		if(uri.contains(".")) {
		    extension = uri.substring(url.lastIndexOf("."));
		}
		return extension;
	}
	
	@SuppressWarnings("deprecation")
	private String getFileNameFromUrl(String uri) {
		String fileName = String.valueOf(System.currentTimeMillis());
		String decodedUrl = java.net.URLDecoder.decode(uri);
		fileName = decodedUrl.substring(decodedUrl.lastIndexOf('/')+1, decodedUrl.length());
		String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
		return fileNameWithoutExtn;
	}
}