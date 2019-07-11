package io.mosip.testrunner;

import java.io.File; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.testng.TestNG;


/**
 * Class to initiate mosip api test execution 
 * 
 * @author Vignesh
 *
 */
public class MosipTestRunner {
	
	public static String jarUrl = MosipTestRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	/**
	 * Main method to start mosip test execution
	 * 
	 * @param arg
	 */
	public static void main(String arg[]){
		if (checkRunType().equalsIgnoreCase("JAR")) {
			ExtractResource.removeOldMosipTestTestResource();
			ExtractResource.extractResourceFromJar();
		}
		startTestRunner();
	}
	/**
	 * The method to start mosip testng execution
	 * @throws IOException 
	 */
	public static void startTestRunner(){
		TestNG runner = new TestNG();
		List<String> suitefiles = new ArrayList<String>();
		suitefiles.add(new File(MosipTestRunner.getGlobalResourcePath()+"/testngapi.xml").getAbsolutePath());
		runner.setTestSuites(suitefiles);
		runner.setOutputDirectory("testng-report");
		runner.run();
	}
	
	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 * @throws IOException 
	 */
	public static String getGlobalResourcePath() {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			return new File(jarUrl).getParentFile().getAbsolutePath() + "/MosipTestResource".toString();
		} else if (checkRunType().equalsIgnoreCase("IDE"))
			return new File(MosipTestRunner.class.getClassLoader().getResource("").getPath()).getAbsolutePath().toString();
		return "Global Resource File Path Not Found";
	}
	
	/**
	 * The method will return mode of application started either from jar or eclipse ide
	 * 
	 * @return
	 */
	public static String checkRunType() {
		if (MosipTestRunner.class.getResource("MosipTestRunner.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}

}
