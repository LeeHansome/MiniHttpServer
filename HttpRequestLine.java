package com.hsm.connector.http;

import java.net.URLEncoder;

public final class HttpRequestLine {
	
	public static final int INITIAL_METHOD_SIZE = 8;
	public static final int INITIAL_URL_SIZE = 64;
	public static final int INITIAL_PROTOCOL_SIZE = 8;
	public static final int MAX_METHOD_SIZE = 1024;
	public static final int MAX_URL_SIZE = 32768;
	public static final int MAX_PROTOCOL_SIZE = 1024;
	
	public HttpRequestLine(){
		this(new char[INITIAL_METHOD_SIZE],0,new char[INITIAL_URL_SIZE],0,new char[INITIAL_PROTOCOL_SIZE],0);
	}
	public HttpRequestLine(char[] method, int methodEnd,char[] url,int urlEnd,char[] protocol,int protocolEnd){
		this.method = method;
		this.methodEnd = methodEnd;
		this.uri = url;
		this.uriEnd = urlEnd;
		this.protocol = protocol;
		this.protocolEnd = protocolEnd;
	}
    public char[] method;
    public int methodEnd;
    public char[] uri;
    public int uriEnd;
    public char[] protocol;
    public int protocolEnd;
    
    public void recycle(){
    	methodEnd = 0;
    	uriEnd = 0;
    	protocolEnd = 0;
    }
    public int indexOf(char[] buf){
    	return indexOf(buf,buf.length);
    }
	public int indexOf(char[] buf, int end) {
		char firstChar = buf[0];
		int pos = 0;
		while(pos<uriEnd){
			pos = indexOf(firstChar,pos);
			if(pos==-1)
				return -1;
			if((uriEnd - pos)<end)
				return -1;
			for(int i=0;i<end;i++){
				if(uri[i+pos]!=buf[i])
					break;
				if(i==(end-1))
					return pos;
			}
			pos++;
		}
		return -1;
	}
	public int indexOf(char firstChar, int start) {
		for(int i=start;i<uriEnd;i++){
			if(uri[i]==firstChar)
				return i;
		}
		return -1;
	}
/*    public int hashCode() {
        return 0;
    }


    public boolean equals(Object obj) {
        return false;
    }
*/
    
}