package io.mosip.registration.main;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import org.testng.TestNG;
import org.testng.collections.Lists;

public class RegClient {
	public static ResourceBundle prop = ResourceBundle.getBundle("config");

	public void createPacket() {
		TestNG testng = new TestNG();
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("spring.profiles.active", prop.getString("enviroment"));
		System.setProperty("mosip.reg.dbpath", prop.getString("dBPath"));
		System.setProperty("mosip.reg.db.key", this.getClass().getResource(prop.getString("dBKeyPath")).getPath());
		System.setProperty("mosip.reg.healthcheck.url", prop.getString("healthcheckURL"));
		System.setProperty("userID", prop.getString("userID"));
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
