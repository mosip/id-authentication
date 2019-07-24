package io.mosip.e2e.util;

import java.io.File;

public class BaseUtil {
	public static String getGlobalResourcePath() {
	
			return new File(BaseUtil.class.getClassLoader().getResource("").getPath()).getAbsolutePath().toString();
	}
	public static void main(String[] args) {
		System.out.println(getGlobalResourcePath());
	}
}
