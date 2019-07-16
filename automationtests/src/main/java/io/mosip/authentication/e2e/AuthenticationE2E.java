package io.mosip.authentication.e2e;

import java.util.Map;

import io.mosip.testrunner.MosipTestRunner;

public class AuthenticationE2E {

	public static Map<String,String> performAuthE2E() {
		MosipTestRunner.startTestRunner();
		return E2EReport.e2eReport;
	}
	
	public static void main(String arg[]) {
		performAuthE2E();
	}
}
