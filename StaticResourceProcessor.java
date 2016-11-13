package com.hsm.Processor;

import com.hsm.Request;
import com.hsm.Response;

public class StaticResourceProcessor {

	public void process(Request request, Response response) {
		response.sendStaticResource();
	}

}
