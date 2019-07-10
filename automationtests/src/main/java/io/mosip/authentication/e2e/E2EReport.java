package io.mosip.authentication.e2e;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class E2EReport implements IReporter{

	public static Map<String,String> e2eReport = new HashMap<String,String>();
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		e2eReport = getE2eAuthTestReport(suites);
	}
	
	@SuppressWarnings("finally")
	private Map<String,String> getE2eAuthTestReport(List<ISuite> suites) {
		Map<String,String> e2eReport = new HashMap<String,String>();
		try {			
			for (ISuite tempSuite : suites) {
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				for (ISuiteResult result : testResults.values()) {
					ITestContext testObj = result.getTestContext();
					String testName = testObj.getName();
					IResultMap testPassedResult = testObj.getPassedTests();
					Set<ITestResult> testPassedResultSet = testPassedResult.getAllResults();
					for (ITestResult testResult : testPassedResultSet) {
						e2eReport.put(testResult.getTestClass().getName(), "PASS");
					}
					IResultMap testFailedResult = testObj.getFailedTests();
					Set<ITestResult> testResultSet = testFailedResult.getAllResults();
					for (ITestResult testResult : testResultSet) {
						e2eReport.put(testResult.getTestClass().getName(), "FAIL");
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			return e2eReport;
		}
	}
}
