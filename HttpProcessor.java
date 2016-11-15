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
import com.hsm.connector.http.Httpconnector;


public class HttpProcessor {
	private Httpconnector httpconnector;
	public static String SHUTDOWN_COMMAND = "/shutdown";
	public static final String WEB_ROOT = System.getProperty("user.dir")+File.separator+"webRoot";
	public HttpProcessor(Httpconnector httpconnector){
		this.httpconnector = httpconnector;
	}

	public void process(Socket socket){
		InputStream in = null;
		OutputStream ou = null;
		try {
			in = socket.getInputStream();
			ou = socket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
			catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			StaticResourceProcessor processor = new StaticResourceProcessor();
			processor.process(request,response);
		}
		try {
			if(socket!=null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
