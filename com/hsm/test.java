package com.hsm;

import java.io.File;

/**
 * Created by yongzhangli on 29/11/16.
 */
public class test {
    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + File.separator + "out" + File.separator + "webRoot";
        System.out.println(path);
    }
}
