package com.hsm.startup;

import com.hsm.connector.http.HttpConnector;

public class Bootstrap {

    public static void main(String[] args) {
        HttpConnector httpConnector = new HttpConnector();
        httpConnector.start();
    }

}
