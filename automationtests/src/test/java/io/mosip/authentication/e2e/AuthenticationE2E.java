package io.mosip.authentication.e2e;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;

public class AuthenticationE2E {

	public static void performAuthE2E() {
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		suitefiles.add(new File("./src/test/resources/testngAuthentication.xml").getAbsolutePath());
		runner.setTestSuites(suitefiles);
		runner.run();
	}
	
	public static void main(String arg[])
	{
		performAuthE2E();
	}
}
