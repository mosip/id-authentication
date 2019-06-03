package io.mosip.registration.main;

import java.util.List;
import java.util.ResourceBundle;

import org.testng.TestNG;
import org.testng.collections.Lists;

public class RegClient {
	public static ResourceBundle prop = ResourceBundle.getBundle("config");
 
	public void createPacket() {
		TestNG testng = new TestNG();
		System.setProperty("spring.profiles.active", prop.getString("enviroment"));
		System.setProperty("mosip.dbpath", prop.getString("dBPath"));
		System.setProperty("mosip.registration.db.key",
				this.getClass().getResource(prop.getString("dBKeyPath")).getPath());
		List<String> suites = Lists.newArrayList();
		String pathToXml = this.getClass().getResource(prop.getString("RunnerXmlFileLocation")).getPath();
		suites.add(pathToXml);
		testng.setTestSuites(suites);
		testng.run();

	}

	public static void main(String[] args) {
		RegClient client = new RegClient();
		client.createPacket();
	}

}
