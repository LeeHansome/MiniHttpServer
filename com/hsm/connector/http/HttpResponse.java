package com.hsm.connector.http;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

public class HttpResponse implements HttpServletResponse{
	protected HashMap headers = new HashMap();
	protected int status = HttpServletResponse.SC_OK;
	private OutputStream ou = null;
	private HttpRequest request = null;
	private int Buffer_Size = 1024;
	
	public HttpResponse(OutputStream ou){
		this.ou = ou;
	}

	public HashMap getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap headers) {
		this.headers = headers;
	}

	public int getBuffer_Size() {
		return Buffer_Size;
	}

	public void setBuffer_Size(int buffer_Size) {
		Buffer_Size = buffer_Size;
	}

	public void sendStaticResource(){
		FileInputStream fis = null;
		File file = new File(Constants.WEB_ROOT, "/" + request.getUri());
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
					} catch (IOException e) {
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
	public HttpRequest getRequest() {
		return request;
	}
	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return new PrintWriter(ou,true);
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub

	}

	public void setContentLengthLong(long arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCookie(Cookie arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsHeader(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeURL(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setStatus(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendError(int arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
}
