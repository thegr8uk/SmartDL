package com.itsmeyuke.smartdl.core;

import java.nio.ByteBuffer;

public class DownloadObject {

	int position;
	ByteBuffer content;
	
	public DownloadObject(int capacity) {
		content = ByteBuffer.allocate(capacity); 
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long write(byte[] bytes) {
		content.put(bytes);
		return content.position();
	}
	
	public long write(byte b) {
		content.put(b);
		return content.position();
	}
}
