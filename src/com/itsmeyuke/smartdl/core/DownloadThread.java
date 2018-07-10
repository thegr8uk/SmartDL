package com.itsmeyuke.smartdl.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;

import com.itsmeyuke.smartdl.model.CyclicArray;

public class DownloadThread implements Callable<DownloadObject> {

	int id;
	DownloadObject downloadObject;
	String url;
	Map<String, Object> headers;
	Double currentSpeed;
	int totalSize;
	int downloadedSize;

	public DownloadThread(int id, String url, Map<String, Object> headers) {
		this.url = url;
		this.id = id;
		this.headers = headers;
		int capacity = findOutCapacity(this.url, this.headers);
		this.totalSize = capacity;
		downloadObject = new DownloadObject(capacity);
	}

	private int findOutCapacity(String url2, Map<String, Object> headers2) {
		URLConnection conn = null;
		try {
			URL url = new URL(url2);
			conn = url.openConnection();
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).setRequestMethod("HEAD");
			}
			if (headers2 != null && !headers2.isEmpty()) {
				for (String key : headers2.keySet()) {
					conn.setRequestProperty(key, headers2.get(key).toString());
				}
			}
			conn.getInputStream();
			return conn.getContentLength();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).disconnect();
			}
		}
	}

	@Override
	public DownloadObject call() throws Exception {

		downloadFile();
		return this.downloadObject;
	}

	private void downloadFile() {
		try {
			URL link = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) link.openConnection();
			if (this.headers != null && !this.headers.isEmpty()) {
				for (String key : this.headers.keySet()) {
					connection.setRequestProperty(key, this.headers.get(key).toString());
				}
			}
			InputStream in = new BufferedInputStream(connection.getInputStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			int i = 0;
			int b = 0;
			int t = 0;
			long start = 0L;
			long end = 0L;
			CyclicArray<Double> cyclicArray = new CyclicArray<>(1);
			while (-1 != (n = in.read(buf))) {
				t = t+n;
				this.downloadedSize = t;
				if(i%50 == 0) {
					start = System.currentTimeMillis();
					b = 0;
				}
				byte[] subArray = Arrays.copyOfRange(buf, 0, n);
				b = b+n;
				if(i%50 == 49)
					end = System.currentTimeMillis();
				this.downloadObject.write(subArray);
				if(i%50 == 49) {
					long totalTime = end - start;
					double speed = 0;
					if(b > 0 && totalTime >= 0) {
						speed = (double)b / (double) totalTime;
					}
					cyclicArray.add(speed);
					this.currentSpeed = cyclicArray.average();
				}
				i++;
			}
			this.currentSpeed = 0d;
			out.close();
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
