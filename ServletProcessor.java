package com.hsm.Processor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hsm.HttpServer;
import com.hsm.Request;
import com.hsm.RequestFacade;
import com.hsm.Response;
import com.hsm.ResponseFacade;

public class ServletProcessor {

	public void servletProcess(Request request, Response response) throws ServletException, IOException {
		String url = request.getUrl();
		String servletName = url.substring(url.lastIndexOf("/")+1);
		URL[] urls = new URL[1];
		URLStreamHandler urlStreamHandler = null;
		URLClassLoader loader = null;
		RequestFacade requestFacade = new RequestFacade(request);
		ResponseFacade responseFacade = new ResponseFacade(response);
		try {
		File servletpath = new File(HttpServer.WEB_ROOT);
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
