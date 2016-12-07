package com.hsm.connector.http;


import org.apache.catalina.util.StringManager;
import org.apache.tomcat.util.http.FastHttpDateFormat;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class HttpProcessor implements Runnable {
	public static String SHUTDOWN_COMMAND = "/shutdown";
	protected String method = null;
	protected String queryString = null;
	protected StringManager sm = StringManager.getManager("com.hsm.connector.http");
    private HttpResponse response;
	private HttpConnector httpConnector;
	private HttpRequest request;
	private HttpRequestLine requestLine = new HttpRequestLine();
	private boolean stopped;
	private boolean available = false;
	private Socket socket;
	private int status;
    private int proxyPort;
    private int serverPort;
    private boolean keepAlive;
	private boolean sendAck;
	private boolean http11;

	public HttpProcessor(HttpConnector httpConnector) {
		this.httpConnector = httpConnector;
	}


	synchronized void asign(Socket socket) {
		while (available) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.socket = socket;
		available = true;
		notifyAll();
	}

	@Override
	public void run() {
		while (!stopped) {
			Socket socket = await();
			if (socket == null) {
				continue;
			}
			try {
				process(socket);
				System.out.println("I am the thread:" + Thread.currentThread().getName());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServletException e) {
				e.printStackTrace();
			}
			httpConnector.recycle(this);
		}
	}


	private synchronized Socket await() {
		while (!available) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Socket socket = this.socket;
		available = false;
		notifyAll();
		return socket;
	}

	private void process(Socket socket) throws IOException, ServletException {
		boolean ok = true;
		boolean finishRepose = true;
		SocketInputStream input = null;
		OutputStream output = null;
		try {
			input = new SocketInputStream(socket.getInputStream(), httpConnector.getBuffSize());
			output = socket.getOutputStream();
			
		} catch (IOException e1) {
			ok = false;
			e1.printStackTrace();
		}
		boolean keepAlive = true;
		request = new HttpRequest(input);
		response = new HttpResponse(output);
		while (!stopped && ok && keepAlive) {
			finishRepose = true;
			request.setSteam(input);
			request.setResponse(response);
			response.setRequest(request);
			response.setStream(output);
			response.setHeader("Server", "SERVER_INFO");

			if (ok) {
				praseConnection(socket);
				praseRequest(input, output);
                System.out.println(request.getProtocol());
                String method = new String(requestLine.method, 0, requestLine.methodEnd);
                String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);
                request.setProtocol(protocol);
                request.setMethod(method);
//				if (request.getProtocol().startsWith("http/0"))
//					praseHeaders(input);
//				if (request.getProtocol().equals("http/1.1"))
//					http11 = true;
                if (http11) {
//					ackRequest(output);
				}
				if (httpConnector.isChunkAllowed())
					response.setAllowChunk(true);
				response.setHeader("Date", FastHttpDateFormat.getCurrentDate());
				httpConnector.getSimpleContainer().invoke(request, response);
			}
			if (finishRepose) {
//				response.finshRespone();
//				request.finshRequest();
                output.flush();
				if ("close".equals(response.getHeader("Connection"))) {
					keepAlive = false;
				}
				status = Constants.PROCESSOR_IDLE;
//				request.recycle();
//				response.recycle();
            }
			shutDown(input);
			socket.close();


		}

    /*    response.setRequest(request);
		if (request.getUri() != null && request.getUri().startsWith("/servlet")) {
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
		}*/
	}

    private void shutDown(SocketInputStream input) throws IOException {
        if (input != null) {
            input.close();
        }
    }

	private void praseConnection(Socket socket) {
		request.setInetAddress(socket.getInetAddress());
		if (proxyPort != 0)
			request.setPort(proxyPort);
		else
			request.setPort(serverPort);
		request.setSocket(socket);
	}

	private void praseRequest(SocketInputStream socketInputStream,OutputStream ou) throws IOException, ServletException{
		socketInputStream.readRequestLine(requestLine);
        String uri = null;
        //Validate the incoming request line
        String method = new String(requestLine.method, 0, requestLine.methodEnd);
        String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);
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
//        request.setMethod(method);
//        request.setProtocol(protocol);
        if(normalizedUri != null){
            request.setUri(normalizedUri);
        }else{
            request.setUri(uri);
        }
		if(normalizedUri == null)
			throw new ServletException("Invalid URI:"+uri+"'");
	}

	private void praseHeaders(SocketInputStream input) throws IOException, ServletException {
		while(true){
			HttpHeader header = new HttpHeader();
			input.readHeader(header);
			if(header.nameEnd == 0){
				if(header.valueEnd == 0)
					return;
				else{
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.colon"));
				}
			}

			String name = new String(header.name,0,header.nameEnd);
			String value = new String(header.value,0,header.valueEnd);
			request.addHeader(name,value);
			if (header.equals(DefaultHeaders.AUTHORIZATION_NAME)) {
				request.setAuthorization(value);
			} else if (header.equals(DefaultHeaders.ACCEPT_LANGUAGE)) {
                //praseAcceptLanguage(value);
            } else if (header.equals(DefaultHeaders.COOKIE_NAME)) {
				//prase cookie
			} else if (header.equals(DefaultHeaders.CONTENT_LENGTH_NAME)) {
				//get content length
			} else if (header.equals(DefaultHeaders.CONTENT_TYPE_NAME)) {
				request.setContentType(value);
			} else if (header.equals(DefaultHeaders.HOST_NAME)) {
				//get host name
			} else if (header.equals(DefaultHeaders.CONNECTION_NAME)) {
				if (header.valueEquals(DefaultHeaders.CONNECTION_CLOSE_VALUE)) {
					keepAlive = false;
					response.setHeader("Connection", "close");
				}
			} else if (header.equals(DefaultHeaders.EXPECT_NAME)) {
				if (header.valueEquals(DefaultHeaders.EXPECT_100_VALUE))
					sendAck = true;
				else
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.unknownExpectation"));
			} else if (header.equals(DefaultHeaders.TRANSFER_ENCODING_NAME)) {
				//request.setTransferEncoding(header);
			}
			//request.nextHeader();
		}
	}

	protected String nomallize(String path) {
		if(path == null)
			return null;
		if (path.equals("/") || path.equals(""))
			return "index.html";
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
