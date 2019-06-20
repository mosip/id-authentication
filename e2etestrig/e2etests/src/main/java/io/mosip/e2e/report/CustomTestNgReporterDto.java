package io.mosip.e2e.report;

/**
 * The Dto class to hold current test ClassName, MethodName and execution
 * timestamp for custom TestNG report
 * 
 * @author Vignesh
 *
 */
public class CustomTestNgReporterDto {

	private String testClassName;
	private String testMathodName;
	private long startTimeMillis;
	private long endTimeMillis;
	private long deltaMillis;
	private String log;
	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	public String getTestClassName() {
		return testClassName;
	}

	public void setTestClassName(String testClassName) {
		this.testClassName = testClassName;
	}

	public String getTestMathodName() {
		return testMathodName;
	}

	public void setTestMathodName(String testMathodName) {
		this.testMathodName = testMathodName;
	}

	public long getStartTimeMillis() {
		return startTimeMillis;
	}

	public void setStartTimeMillis(long startTimeMillis) {
		this.startTimeMillis = startTimeMillis;
	}

	public long getEndTimeMillis() {
		return endTimeMillis;
	}

	public void setEndTimeMillis(long endTimeMillis) {
		this.endTimeMillis = endTimeMillis;
	}

	public long getDeltaMillis() {
		return deltaMillis;
	}

	public void setDeltaMillis(long deltaMillis) {
		this.deltaMillis = deltaMillis;
	}
}

