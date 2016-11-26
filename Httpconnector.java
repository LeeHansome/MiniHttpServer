package com.hsm.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.servlet.ServletException;

import com.hsm.HttpProcessor;

public class Httpconnector implements Runnable{
	boolean stopped = false;
	private String scheme = "http";

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try{
			InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
			serverSocket = new ServerSocket(port, 1 ,inetAddress );
		}catch(Exception e){
			e.printStackTrace();
		}
		while(!stopped){
			Socket socket = null;
			try {
			    socket = serverSocket.accept();				
			    HttpProcessor httpProcessor = new HttpProcessor(this);
			    httpProcessor.process(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}catch (ServletException e) {
				e.printStackTrace();
			}
		}
	}
	public void start(){
		Thread t = new Thread(this);
		t.start();
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	
}
