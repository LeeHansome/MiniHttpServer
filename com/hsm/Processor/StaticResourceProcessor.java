package com.hsm.Processor;

import com.hsm.connector.http.HttpRequest;
import com.hsm.connector.http.HttpResponse;

public class StaticResourceProcessor {

	public void process(HttpRequest request, HttpResponse response) {
		response.sendStaticResource();
	}

}
