package io.mosip.authentication.fw.util;

import java.io.File;

/**
 * Class to hold all the test parameter such as testcasename, testscenario ,
 * test id. Used for dataprovider class
 * 
 * @author Vignesh
 *
 */
public class TestParameters {
	
	private String testCaseName = null;
    private String testScenario = null;
    private File testCaseFile=null;
    private String testId=null;
    /**
     * The method assign current testcasename,scenraio and its file path
     * 
     * @param testcasename
     * @param scenario
     * @param testCaseFile
     * @param testId
     */
    public TestParameters(String testcasename,
                          String scenario,File testCaseFile,String testId) {
        this.testCaseName = testcasename;
        this.testScenario = scenario;
        this.testCaseFile= testCaseFile;
        this.testId=testId;
    }
    /**
     * The method assign current testcasename,scenraio and its file path
     * 
     * @param object
     */
	public TestParameters(TestParameters object) {
		this.testCaseName = object.getTestCaseName();
		this.testScenario = object.getTestScenario();
		this.testCaseFile = object.getTestCaseFile();
		this.testId = object.getTestId();
	}
    /**
     * The method get currrent test case name
     * 
     * @return testcaseName
     */
	public String getTestCaseName() {
		return testCaseName;
	}
    /**
     * The method set current test case name
     * 
     * @param testCaseName
     */
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
    /**
     * The method get current test scenario
     * 
     * @return testScenario
     */
	public String getTestScenario() {
		return testScenario;
	}
    /**
     * The method set test scenario
     * 
     * @param testScenario
     */
	public void setTestScenario(String testScenario) {
		this.testScenario = testScenario;
	}
    /**
     * The method get test case file path
     * 
     * @return testCaseFile
     */
	public File getTestCaseFile() {
		return testCaseFile;
	}
    /**
     * The method set test case File
     * 
     * @param testCaseFile
     */
	public void setTestCaseFile(File testCaseFile) {
		this.testCaseFile = testCaseFile;
	}
    /**
     * The method get test case ID
     * 
     * @return testId
     */
	public String getTestId() {
		return testId;
	}
    /**
     * The method set test ID
     * 
     * @param testId
     */
	public void setTestId(String testId) {
		this.testId = testId;
	}   

}
