package io.mosip.registrationProcessor.main;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;

public class RegistrationProcessorMain {
public static void main(String[] args) {
	String xmlFilePath=System.getProperty("user.dir")+"\\"+"testngRegistration-Processor.xml";
	System.out.println(xmlFilePath);
	TestNG testng = new TestNG();
	List<String> suites = new ArrayList<String>();
	suites.add(xmlFilePath);//path to xml..
	testng.setTestSuites(suites);
	testng.run();
}
}
