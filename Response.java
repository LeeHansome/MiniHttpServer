package com.hsm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Response {
	private OutputStream ou = null;
	private Request request = null;
	private int Buffer_Size = 1024;
	
	public Response(OutputStream ou){
		this.ou = ou;
	}
	
	public void sendStaticResource(){
		FileInputStream fis = null;
		File file = new File(HttpServer.WEB_ROOT,"/"+request.getUrl());
		if(file.exists()){
			byte[] b = new byte[Buffer_Size];
			try {
				fis = new FileInputStream(file);
				int n = fis.read(b);
				while(n!=-1){
					ou.write(b, 0, n);
					n = fis.read(b);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}else{
			String errMsg = "HTTP/1.1 404 File Not Found\r\n"+
					"Content-Type: text/html\r\n"+
					"\r\n"+
					"<h1>File Not Found :(</h1>";
			try {
				ou.write(errMsg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(fis!=null)
					try {
						fis.close();
					} catch (IOException e) {						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}
	
	public OutputStream getOu() {
		return ou;
	}
	public void setOu(OutputStream ou) {
		this.ou = ou;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
}
