package com.hsm;

import java.io.IOException;
import java.io.InputStream;

public class Request {
	private InputStream in = null;
	private String url = "";
	private String praseRequest = "";
	
	public Request(InputStream  in){
		this.in = in;
	}
	
	public void prase(){
		byte[] b = new byte[2048]; 
		StringBuffer sb = new StringBuffer();
		try {
			int n = in.read(b);
			for(int i=0;i<n;i++){
				sb.append((char) b[i]);
			}
			praseRequest = sb.toString();
			url = praseUrl();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String praseUrl(){
		int index1=0,index2=0;
		index1 = praseRequest.indexOf(" ");
		if(index1>0){
			index2 = praseRequest.indexOf(" ",index1+1);	
			if(index2>index1){
				return praseRequest.substring(index1+1,index2);				
			}
		}
		return null;
	}
	
	public InputStream getIn() {
		return in;
	}
	public void setIn(InputStream in) {
		this.in = in;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getPraseRequest() {
		return praseRequest;
	}

	public void setPraseRequest(String praseRequest) {
		this.praseRequest = praseRequest;
	}
	
}
