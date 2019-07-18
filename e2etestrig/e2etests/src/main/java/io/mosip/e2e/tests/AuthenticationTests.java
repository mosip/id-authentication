package io.mosip.e2e.tests;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.mosip.authentication.e2e.AuthenticationE2E;
import io.mosip.e2e.util.TestRigException;

public class AuthenticationTests {
	@Test
	public void authenticateTest() throws TestRigException {
		System.out.println(AuthenticationE2E.performAuthE2E());
		for(Map.Entry<String, String> entry:AuthenticationE2E.performAuthE2E().entrySet()) {
			if(entry.getValue().equalsIgnoreCase("Fail")) {
				throw new TestRigException("IDA Authentication Failed Because all uins didn't get generated");
				
			} else {
				Assert.assertTrue(true);
			}
		}
	}
}
