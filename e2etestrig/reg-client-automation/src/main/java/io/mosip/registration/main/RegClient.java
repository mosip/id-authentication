package io.mosip.registration.main;

import java.io.File;
import java.util.List;

import org.testng.TestNG;
import org.testng.collections.Lists;

import io.mosip.registration.constants.RegistrationConstants;


public class RegClient {
	public void createPacket() {
		TestNG testng = new TestNG();
        System.setProperty("spring.profiles.active", "preqa");
        System.setProperty("mosip.dbpath","./reg");
        System.setProperty("mosip.registration.db.key", this.getClass().getResource("/keys.properties").getPath());
        List<String> suites = Lists.newArrayList();
        String pathToXml=this.getClass().getResource("/RegClient/RegClient.xml").getPath();
        suites.add(pathToXml);
        testng.setTestSuites(suites);
        testng.run();
        

		}
	public static void main(String[] args) {
		RegClient client=new RegClient();
		client.createPacket();
	}

}
