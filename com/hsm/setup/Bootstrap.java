package com.hsm.setup;

import com.hsm.connector.http.Httpconnector;

public class Bootstrap {

	public static void main(String[] args) {
		Httpconnector httpconnector = new Httpconnector();
		httpconnector.start();
	}

}
