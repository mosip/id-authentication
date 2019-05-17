package io.mosip.authentication.fw.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * Authentication Tests Listener class
 * 
 * @author Vignesh
 *
 */
public class AuthenticationTestsListener extends IdaScriptsUtil implements IAnnotationTransformer{

	/**
	 * The method to set the invocationcount for authentication tests provided in runConfiguration property file
	 */
	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		if (testMethod.toString().contains(".authentication."))
			annotation.setInvocationCount(
					Integer.parseInt(getPropertyAsMap(new File("./" + getRunConfigFile()).getAbsolutePath().toString())
							.get(getNormalisedClassName(testMethod.toString()) + ".invocationCount")));
	}
	
	/**
	 * To get actual or simple class name
	 * 
	 * @param testMethodName - method name for current test
	 * @return String - actual class name
	 */
	private String getNormalisedClassName(String testMethodName) {
		return testMethodName.split(Pattern.quote("."))[4];
	}
}

