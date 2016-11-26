package com.hsm.connector.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

public class HttpRequest implements HttpServletRequest{
	private InputStream input = null;
	private String uri = "";
	private String queryString=null;
	private String praseRequest = "";
	private String requestedSessionId=null;
	private boolean requestedSessionURL=false;
	private String method;
	private String protocol;

	protected HashMap headers = new HashMap<>();
	protected ArrayList cookies = new ArrayList<>();
	//protected parameterma
	public HttpRequest(InputStream  input){
		this.input = input;
	}
	
	public void prase(){
		byte[] b = new byte[2048]; 
		StringBuffer sb = new StringBuffer();
		try {
			int n = input.read(b);
			for(int i=0;i<n;i++){
				sb.append((char) b[i]);
			}
			praseRequest = sb.toString();
			uri = praseUrl();
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
		return input;
	}
	public void setIn(InputStream in) {
		this.input = in;
	}
	public String getUrl() {
		return uri;
	}
	public void setUrl(String url) {
		this.uri = url;
	}

	public String getPraseRequest() {
		return praseRequest;
	}

	public void setPraseRequest(String praseRequest) {
		this.praseRequest = praseRequest;
	}

	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getDateHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIntHeader(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Part getPart(String arg0) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0)
			throws IOException, ServletException {
		return null;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

	public HashMap getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap headers) {
		this.headers = headers;
	}

	public void setRequestedSessionId(String requestedSessionId) {
		this.requestedSessionId = requestedSessionId;
	}

	public void setCookies(ArrayList cookies) {
		this.cookies = cookies;
	}

	public boolean isRequestedSessionURL() {
		return requestedSessionURL;
	}

	public void setRequestedSessionURL(boolean requestedSessionURL) {
		this.requestedSessionURL = requestedSessionURL;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
}
