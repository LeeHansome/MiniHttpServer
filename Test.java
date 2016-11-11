package com.hsm;

import java.io.File;

public class Test {

	public static void main(String[] args) {
		File file = new File(System.getProperty("user.dir")+File.separator+"webRoot", "//index.jsp");
		System.out.println(file.getPath());
		if(file.exists())
			System.out.println("exists");
		else
			System.out.println("not exists");
	}

}
