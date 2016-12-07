package com.hsm.startup;

import com.hsm.connector.http.HttpConnector;
import com.hsm.connector.http.SimpleContainer;


public class Bootstrap {
    public static void main(String[] args) {
        HttpConnector httpConnector = new HttpConnector();
        SimpleContainer container = new SimpleContainer();
        httpConnector.setSimpleContainer(container);
        httpConnector.start();
    }

}
