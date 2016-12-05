package com.hsm.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

public class HttpConnector implements Runnable {
	protected int minProcessors = 5;
	boolean stopped = false;
	private String scheme = "http";
	private int maxProcessors = 20;
	private int curProcessor = 0;
	private boolean chunkAllowed;
    private SimpleContainer simpleContainer;
    private int buffSize = 2048;
	private Stack<HttpProcessor> processors = new Stack<>();
	private int port = 8080;

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public int getCurProcessor() {
		return curProcessor;
	}

	public void setCurProcessor(int curProcessor) {
		this.curProcessor = curProcessor;
	}

	public Stack<HttpProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(Stack<HttpProcessor> processors) {
		this.processors = processors;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

    public SimpleContainer getSimpleContainer() {
        return simpleContainer;
    }

    public void setSimpleContainer(SimpleContainer simpleContainer) {
        this.simpleContainer = simpleContainer;
    }

	public boolean isChunkAllowed() {
		return chunkAllowed;
	}

	public void setChunkAllowed(boolean chunkAllowed) {
		this.chunkAllowed = chunkAllowed;
	}

	public int getMinProcessors() {
		return minProcessors;
	}

	public void setMinProcessors(int minProcessors) {
		this.minProcessors = minProcessors;
	}

	public int getMaxProcessors() {
		return maxProcessors;
	}

	public void setMaxProcessors(int maxProcessors) {
		this.maxProcessors = maxProcessors;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
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
				HttpProcessor httpProcessor = createProcessor();
				if (httpProcessor == null) {
					System.out.println("no processor");
					socket.close();
					continue;
				}
				httpProcessor.asign(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private HttpProcessor createProcessor() {
		if (!processors.isEmpty()) {
			return processors.pop();
		} else if (curProcessor < maxProcessors) {
			curProcessor++;
			HttpProcessor httpProcessor = new HttpProcessor(this);
			Thread thread = new Thread(httpProcessor, "processor_" + curProcessor);
			thread.start();
			return httpProcessor;
		} else
			return null;
	}

	public void start(){
		while (curProcessor < minProcessors) {
			if (maxProcessors > 0 && curProcessor > maxProcessors)
				break;
			HttpProcessor processor = new HttpProcessor(this);
			Thread thread = new Thread(processor, "processor_" + curProcessor);
			thread.start();
			recycle(processor);
			curProcessor++;
		}
		Thread t = new Thread(this);
		t.start();
	}

	public int getBuffSize() {
		return buffSize;
	}

	public void setBuffSize(int buffSize) {
		this.buffSize = buffSize;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void recycle(HttpProcessor processor) {
		processors.push(processor);
	}
}
