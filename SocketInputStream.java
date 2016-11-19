package com.hsm.connector.http;

import java.io.EOFException;
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
	protected int pos;
	protected InputStream is;
	
	public SocketInputStream(InputStream is,int bufferSize){
		this.is = is;
		buf = new byte[bufferSize];
	}
	/*protected static StringManager sm =
	        StringManager.getManager(Constants.Package);*/
	public void readRequestLine(HttpRequestLine requestLine) throws IOException{
		if(requestLine.methodEnd != 0)
			requestLine.recycle();
		int chr = 0;
		do{
			try{
				chr = read();
			}catch(IOException e){
				chr = -1;
			}
		}while((chr == CR)||(chr == LF));
		//if(chr == -1)			
		//	throw new EOFException(sm.getString("requestStream.readline.error"));
		pos--;
		
		//Read the method
		
		int maxRead = requestLine.method.length;
		int readStart = pos;
		int readCount = 0;
		boolean space = false;
		while(!space){
			if(readCount >= maxRead){
				if((2 * maxRead) <= HttpRequestLine.MAX_METHOD_SIZE){
					char[] newBuffer = new char[2 * maxRead];
					System.arraycopy(requestLine.method , 0, newBuffer,0, maxRead);
					requestLine.method = newBuffer;
					maxRead = requestLine.method.length;
				}else{
					/* throw new IOException
                     (sm.getString("requestStream.readline.toolong"));*/
				}
			}			
			if(pos >= count){
				int val = read();
				if(val == -1){
					/* throw new IOException
                       (sm.getString("requestStream.readline.toolong"));*/
				}					
				pos = 0;
				readStart = 0;
			}
			if(buf[pos]==SP){
				space = true;
			}
			requestLine.method[readCount] = (char) buf[pos];
			readCount++;
			pos++;
		}
		requestLine.methodEnd = readCount - 1;
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
	
	protected void fill() throws IOException{
		pos = 0;
		count = 0;
		int nRead = is.read(buf, 0, buf.length);
		if(nRead > 0)
			count = nRead;
	}
	
	@Override
	public int read() throws IOException {
		if(pos >= count){
			fill();
			if(pos >= count)
				return -1;
		}
		return buf[pos++] & 0xff;
	}

}
