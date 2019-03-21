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

    public TestParameters(String testcasename,
                          String scenario,File testCaseFile,String testId) {
        this.testCaseName = testcasename;
        this.testScenario = scenario;
        this.testCaseFile= testCaseFile;
        this.testId=testId;
    }
    
	public TestParameters(TestParameters object) {
		this.testCaseName = object.getTestCaseName();
		this.testScenario = object.getTestScenario();
		this.testCaseFile = object.getTestCaseFile();
		this.testId = object.getTestId();
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getTestScenario() {
		return testScenario;
	}

	public void setTestScenario(String testScenario) {
		this.testScenario = testScenario;
	}

	public File getTestCaseFile() {
		return testCaseFile;
	}

	public void setTestCaseFile(File testCaseFile) {
		this.testCaseFile = testCaseFile;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

    

}

