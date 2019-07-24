package io.mosip.testrunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.testng.TestNG;


/**
 * Class to initiate mosip api test execution 
 * 
 * @author Vignesh
 *
 */
public class MosipTestRunner {
	
	public static void main(String arg[]) throws URISyntaxException, IOException{
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
	public static String getGlobalResourcePath(){
		return new File(MosipTestRunner.class.getClassLoader().getResource("").getPath()).getAbsolutePath();
	}
	
	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 * @throws IOException 
	 */
	public static String getTestGlobalResourcePath(){
		try {
		URL jar =MosipTestRunner.class.getClassLoader().getResource("automationtests-0.12.16-jar-with-dependencies.jar");
		ZipInputStream zip = new ZipInputStream(jar.openStream());
		 while(true) {
			 ZipEntry e = zip.getNextEntry();
			 if (e == null)
			      break;
			 String name = e.getName();
		 }
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return "Dummy";
		//return new File(MosipTestRunner.class.getClassLoader().getResource("automationtests-0.12.7-jar-with-dependencies.jar").getPath()).getAbsolutePath();
	}
	
	public static String checkRunType()
	{
		if(MosipTestRunner.class.getResource("MosipTestRunner.class").getPath().toString().contains("jar:"))
			return "JAR";
		else
			return "IDE";
	}
	
	public static void copy(InputStream in, OutputStream out){
		try {
	    byte[] buffer = new byte[1024];
	    while (true) {
	      int bytesRead = in.read(buffer);
	      if (bytesRead == -1)
	        break;
	      out.write(buffer, 0, bytesRead);
	    }
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	  }
	
	 public static void test() throws URISyntaxException, IOException {
	        URI uri = MosipTestRunner.class.getResource("/automationtests-0.12.7-jar-with-dependencies.jar").toURI();
	        Path myPath;
	        if (uri.toString().contains(".jar")) {
	            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
	            myPath = fileSystem.getPath("/automationtests-0.12.7-jar-with-dependencies.jar");
	        } else {
	            myPath = Paths.get(uri);
	        }
	        Stream<Path> walk = Files.walk(myPath, 1);
	        for (Iterator<Path> it = walk.iterator(); it.hasNext();){
	            System.out.println(it.next());
	        }
	    }

}
