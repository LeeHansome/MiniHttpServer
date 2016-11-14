package com.hsm.boot;

import com.hsm.HttpProcessor;

public class Bootstrap {

	public static void main(String[] args) {
		HttpProcessor httpProcessor = new HttpProcessor();
		httpProcessor.await();
	}

}
