package io.mosip.authentication.fw.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import io.mosip.authentication.idRepository.fw.util.IdRepoTestsUtil;

/**
 * Authentication Tests Listener class
 * 
 * @author Vignesh
 *
 */
public class AuthenticationTestsListener extends AuthTestsUtil implements IAnnotationTransformer{

	/**
	 * The method to set the invocationcount for authentication tests provided in runConfiguration property file
	 */
	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		if (testMethod.toString().contains("io.mosip.authentication.tests."))
			annotation.setInvocationCount(
					Integer.parseInt(getPropertyAsMap(new File(getRunConfigFile()).getAbsolutePath().toString())
							.get(getNormalisedClassName(testMethod.getDeclaringClass().getName()) + ".invocationCount")));
		else if (testMethod.toString().contains("io.mosip.idRepository.tests."))
			annotation.setInvocationCount(
					Integer.parseInt(getPropertyAsMap(new File(IdRepoTestsUtil.getIdRepoRunConfigFile()).getAbsolutePath().toString())
							.get(getNormalisedClassName(testMethod.getDeclaringClass().getName()) + ".invocationCount")));
	}
	
	/**
	 * To get actual or simple class name
	 * 
	 * @param testMethodName - method name for current test
	 * @return String - actual class name
	 */
	private String getNormalisedClassName(String testMethodName) {
		String value[] = testMethodName.toString().split(Pattern.quote("."));
		return value[value.length - 1].toString();
	}
}

