package com.hsm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.servlet.ServletException;

import com.hsm.Processor.ServletProcessor;
import com.hsm.Processor.StaticResourceProcessor;


public class HttpProcessor {
	public static String SHUTDOWN_COMMAND = "/shutdown";
	public static final String WEB_ROOT = System.getProperty("user.dir")+File.separator+"webRoot";


	public void await(){
		boolean shutdown = false;
		InputStream in = null;
		OutputStream ou = null;
		ServerSocket serverSocket = null;
		try{
			InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
			int port = 8080;
			serverSocket = new ServerSocket(port, 1 ,inetAddress );
		}catch(Exception e){
			e.printStackTrace();
		}
		while(!shutdown){
			try {
				Socket socket = serverSocket.accept();
				in = socket.getInputStream();
				ou = socket.getOutputStream();
				Request request = new Request(in);
				request.prase();
				Response response = new Response(ou);
				response.setRequest(request);
				if(request.getUrl()!=null&&request.getUrl().startsWith("/servlet")){
					ServletProcessor servletProcessor = new ServletProcessor();
					try {
						servletProcessor.servletProcess(request,response);
					} catch (ServletException e) {
						e.printStackTrace();
					}
				}else{
					StaticResourceProcessor processor = new StaticResourceProcessor();
					processor.process(request,response);
				}
				socket.close();
				shutdown = SHUTDOWN_COMMAND.equals(request.getUrl());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
