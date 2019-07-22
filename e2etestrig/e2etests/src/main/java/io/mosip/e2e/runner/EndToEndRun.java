package io.mosip.e2e.runner;

import java.util.List;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;
/**
 * 
 * @author M1047227
 *
 */

public class EndToEndRun {
	/**
	 * 
	 * @param args
	 * Main method to run the e2e
	 */
public static void main(String[] args) {
	TestListenerAdapter tla = new TestListenerAdapter();
	String testNgFile=System.getProperty("user.dir")+"\\src\\test\\resources\\testNg.xml";
	TestNG testng = new TestNG();
	List<String> suites = Lists.newArrayList();
	suites.add(testNgFile);//path to xml..
	
	testng.setTestSuites(suites);
	testng.run();
}
}
