package com.hsm;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.servlet.ServletException;









import org.apache.catalina.util.StringManager;

import com.hsm.Processor.ServletProcessor;
import com.hsm.Processor.StaticResourceProcessor;
import com.hsm.connector.http.HttpRequest;
import com.hsm.connector.http.HttpRequestLine;
import com.hsm.connector.http.Httpconnector;
import com.hsm.connector.http.SocketInputStream;


public class HttpProcessor {
	private Httpconnector httpconnector;
	private HttpRequest request;
	private HttpRequestLine requestLine = new HttpRequestLine();
	//private HttpResponse response;
	protected String method = null;
	protected String queryString = null;
	protected StringManager sm = StringManager.getManager("com.hsm.connnector.http");
	
	public static String SHUTDOWN_COMMAND = "/shutdown";
	public static final String WEB_ROOT = System.getProperty("user.dir")+File.separator+"webRoot";
 
	public HttpProcessor(Httpconnector httpconnector){
		this.httpconnector = httpconnector;
	}
	
	
	public void process(Socket socket){
		SocketInputStream input = null;
		OutputStream ouput = null;
		try {
			input = new SocketInputStream(socket.getInputStream(),2048);
			ouput = socket.getOutputStream();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Request request = new Request(input);
		request.prase();
		Response response = new Response(ouput);
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
	private void praseRequest(SocketInputStream socketInputStream,OutputStream ou) throws IOException, ServletException{
		socketInputStream.readRequestLine(requestLine);
		String method = new String(requestLine.method,0,requestLine.methodEnd);
		String uri = null;
		String protocol = new String(requestLine.protocol,0,requestLine.protocolEnd);
		
		//Validate the incoming request line
		if(method.length() < 1){
			throw new ServletException("Missing the HTTP request method");
		}else if(requestLine.uriEnd < 1){
			throw new ServletException("Missing HTTP request URI");
		}
		//Prase any query parameters out of the request URI
		int question = requestLine.indexOf("?");
		if(question >= 0){
			request.setQueryString(new String(requestLine.uri,question+1,requestLine.uriEnd-question-1));
			uri = new String(requestLine.uri,0,question);
		}else{
			request.setQueryString(null);
			uri = new String(requestLine.uri,0,requestLine.uriEnd);
		}
		
		//checking for an absolute URI
		if(!uri.startsWith("/")){
			int pos = uri.indexOf("://");
			if(pos != -1){
				pos = uri.indexOf("/",pos+3);
				if(pos == -1){
					uri = "";
				}else{
					uri = uri.substring(pos);
				}
			}
		}
		//prase any requested session ID out of the request URI
		String match = ";jsessionid=";
		int semicolon = uri.indexOf(match);
		if(semicolon >= 0){
			String rest = uri.substring(semicolon + match.length());
			int semicolon2 = rest.indexOf(";");
			if(semicolon2 >= 0){
				request.setRequestedSessionId(rest.substring(0,semicolon2));
				rest = rest.substring(semicolon2);
			}else{
				request.setRequestedSessionId(rest);
				rest = "";
			}
			request.setRequestedSessionURL(true);
			uri = uri.substring(0,semicolon)+rest;
		}else{
			request.setRequestedSessionURL(false);
			request.setRequestedSessionId(null);
		}
		//Normalize URI
		
		String normalizedUri = nomallize(uri);
		((HttpRequest)request).setMethod(method);
		request.setProtocol(protocol);
		if(normalizedUri != null){
			request.setUrl(normalizedUri);
		}else{
			request.setUrl(uri);
		}
		if(normalizedUri == null)
			throw new ServletException("Invalid URI:"+uri+"'");
	}


	protected String nomallize(String path) {
		if(path == null)
			return null;
		String normalized = path;
		if(normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
			normalized = "/~"+normalized.substring(4);
		
		if((normalized.indexOf("%25")) >= 0
			||(normalized.indexOf("%2F")) >= 0
			||(normalized.indexOf("%2E")) >= 0
			||(normalized.indexOf("%2C")) >= 0
			||(normalized.indexOf("%2f")) >= 0
			||(normalized.indexOf("%2e")) >= 0
			||(normalized.indexOf("%2c")) >= 0){
			return null;
		}
		if(normalized.equals("/."))
			return "/";
		
		if(normalized.indexOf('\\')>=0)
			normalized = normalized.replace('\\', '/');
		if(!normalized.startsWith("/"))
			normalized = "/" + normalized;
		while(true){
			int index = normalized.indexOf("//");
			if(index <= 0)
				break;
			normalized = normalized.substring(0,index)+normalized.substring(index+1);
		}
		while(true){
			int index = normalized.indexOf("/./");
			if(index < 0)
				break;
			normalized = normalized.substring(0,index)+normalized.substring(index+2);
		}
		while(true){
			int index = normalized.indexOf("/../");
			if(index < 0)
				break;
			if(index == 0)
				return null;
			int index2 = normalized.lastIndexOf("/",index-1);
			normalized = normalized.substring(0,index2)+normalized.substring(index+3);
		}
		if(normalized.indexOf("/...") >= 0)
			return null;
		return normalized;
	}
}
