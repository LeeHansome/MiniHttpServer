package com.hsm.Processor;

import com.hsm.connector.http.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class ServletProcessor {

	public void servletProcess(HttpRequest request, HttpResponse response) throws ServletException, IOException {
		String uri = request.getUri();
		String servletName = uri.substring(uri.lastIndexOf("/") + 1);
		URL[] urls = new URL[1];
		URLStreamHandler urlStreamHandler = null;
		URLClassLoader loader = null;
		HttpRequestFacade requestFacade = new HttpRequestFacade(request);
		HttpResponseFacade responseFacade = new HttpResponseFacade(response);
		try {
		File servletpath = new File(Constants.WEB_ROOT);
		String repository = (new URL("file",null,servletpath.getCanonicalPath()+File.separator)).toString();
		urls[0] = new URL(null, repository, urlStreamHandler);
		loader = new URLClassLoader(urls);
		Class<Servlet> myclass = (Class<Servlet>) loader.loadClass(servletName);
		Servlet servlet = myclass.newInstance();
		servlet.service(requestFacade, responseFacade);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
	}
}
