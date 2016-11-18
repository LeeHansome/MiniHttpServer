package com.hsm.connector.http;

import java.io.IOException;
import java.io.InputStream;

public class SocketInputStream extends InputStream {
	private static final byte CR = (byte)'\r';
	private static final byte LF = (byte)'\n';
	private static final byte SP = (byte)' ';
	private static final byte HT = (byte)'\t';
	private static final byte COLON = (byte)':';
	private static final int LC_OFFSET = 'A'-'a';
	
	protected byte buf[];
	protected int count;
	protected InputStream is;
	
	public SocketInputStream(InputStream is,int bufferSize){
		this.is = is;
		buf = new byte[bufferSize];
	}
	/*protected static StringManager sm =
	        StringManager.getManager(Constants.Package);*/
	public void readRequestLine(HttpRequestLine requestLine){
		
	}
	
	/*public void readHeader(HttpHeader header){
		
	}*/
	
	public int available(){
		return 0;
	}
	
	public void close() throws IOException{
		if(is == null)
			return;
		is.close();
		is = null;
		buf = null;
	}
	
	protected void fill(){
		
	}
	
	@Override
	public int read() throws IOException {
		return 0;
	}

}
