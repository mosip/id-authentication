package io.mosip.e2e.report;



import java.io.FileReader;


import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.apache.maven.model.Model;

public class Reporter {
	private static  final Logger REPORTLOG = Logger.getLogger(Reporter.class);

	public static String getAppDepolymentVersion() {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = null;
		try {
			model = reader.read(new FileReader("pom.xml"));
		} catch (Exception e) {
			REPORTLOG.error("Exception in tagging the build number" + e.getMessage());
		}
		return model.getParent().getVersion();
	}
	
	public static String getAppEnvironment() {
		//return System.getProperty("spring.profiles.active");
		return "qa";
	}
}