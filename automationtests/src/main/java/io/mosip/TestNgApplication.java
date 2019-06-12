package io.mosip;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;


public class TestNgApplication {
       public static void main(String[] args) {
              // Create object of TestNG Class
              TestNG runner = new TestNG();
              // Create a list of String
              List<String> suitefiles = new ArrayList<String>();
              // Add xml file which you have to execute
              suitefiles.add("mosip-api-resources/testngapi.xml");
              // now set xml file for execution
              runner.setTestSuites(suitefiles);
              // finally execute the runner using run method
              runner.setOutputDirectory("testng-report");
              runner.run();
       }
}

//import java.io.File;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.Enumeration;
//import java.util.jar.JarEntry;
//import java.util.jar.JarFile;
//
//import org.codehaus.classworlds.Launcher;
//
///**
// * list resources available from the classpath @ *
// */
//public class TestNgApplication {
//
//	public static void main(String[] args) throws URISyntaxException, IOException {
//		final String path = "kernel/AuditLog";
//		final File jarFile = new File(
//				TestNgApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//
//		if (jarFile.isFile()) { // Run with JAR file
//			final JarFile jar = new JarFile(jarFile);
//			final Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
//			while (entries.hasMoreElements()) {
//			JarEntry je =	entries.nextElement();
//			if(je.isDirectory()) {
//				final String name = je.getName();
//				if (name.startsWith(path + "/")) { // filter according to the path
//					System.out.println(name);
//				}
//			}
//			}
//			jar.close();
//		} else { // Run with IDE
//			final URL url = Launcher.class.getResource("/" + path);
//			if (url != null) {
//				try {
//					final File apps = new File(url.toURI());
//					for (File app : apps.listFiles()) {
//						System.out.println(app);
//					}
//				} catch (URISyntaxException ex) {
//					// never happens
//				}
//			}
//		}
//	}
//}
