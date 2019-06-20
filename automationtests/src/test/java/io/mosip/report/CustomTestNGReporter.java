package io.mosip.report;

import org.testng.IReporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.mail.Folder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

/**
 * Customised Testng Report
 * 
 * @author Vignesh
 *
 */
public class CustomTestNGReporter extends Reporter implements IReporter {

	private static final Logger CustomTestNGReporterLog = Logger.getLogger(CustomTestNGReporter.class);
	private static final String defaultTestNgEmailableReport="./target/surefire-reports/emailable-report.html";
	private static final String extendtReport="./extent-report.html";
	private static final String emailableReportTemplateFile = new File(
			"./src/test/resources/customize-emailable-report-template.html").getAbsolutePath();
	private static StringBuffer customReportTemplateStr = new StringBuffer();

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HHmm");

	private static final String reportProfixFileName = "MOSIP_AuthenticationAutoRun_TestNGReport";

	// PieChart
	private int passTestCount = 0;
	private int skipTestCount = 0;
	private int failTestCount = 0;
	private int totalCount = 0;
	private String color = "";
	private int countTestClassName = 0;
	private boolean testClassNameFlag = false;

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		try {
			// Get content data in TestNG report template file.
			customReportTemplateStr = this.readEmailabelReportTemplate();
			// Create custom report title.
			customReportTemplateStr.append(this.getCustomReportTitle("MOSIP API Test Report"));
			customReportTemplateStr.append(afterTestReportTittleContent());
			// Create test suite summary data.
			customReportTemplateStr.append(this.getTestSuiteSummary(suites));
			customReportTemplateStr.append(afterTestCaseSummaryContent());
			// Create test methods summary data.
			customReportTemplateStr.append(this.getTestMethodSummary(suites));
			customReportTemplateStr.append(afterTestMethodSummaryContent());
			String finalcustomReport = updatePieChart(customReportTemplateStr.toString());

			removeOldCustomMosipReport(outputDirectory);

			File targetFile = new File(outputDirectory + "/"+reportProfixFileName/*getCurrentDateForReport()*/+".html");
			FileWriter fw = new FileWriter(targetFile);
			fw.write(finalcustomReport);
			fw.flush();
			fw.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String updatePieChart(String customReportTemplateStr) {
		customReportTemplateStr = customReportTemplateStr.replaceAll("\\$pass\\$", String.valueOf(passTestCount));
		customReportTemplateStr = customReportTemplateStr.replaceAll("\\$skip\\$", String.valueOf(skipTestCount));
		customReportTemplateStr = customReportTemplateStr.replaceAll("\\$fail\\$", String.valueOf(failTestCount));
		return customReportTemplateStr;
	}

	/* Read template content. */
	private StringBuffer readEmailabelReportTemplate() {
		StringBuffer retBuf = new StringBuffer();

		try {

			File file = new File(emailableReportTemplateFile);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();
			while (line != null) {
				retBuf.append(line);
				line = br.readLine();
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} finally {
			return retBuf;
		}
	}

	/* Build custom report title. */
	private String getCustomReportTitle(String title) {
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(title + " " + this.getDateInStringFormat(new Date()));
		return retBuf.toString();
	}

	/* Build test suite summary data. */
	private String getTestSuiteSummary(List<ISuite> suites) {
		StringBuffer retBuf = new StringBuffer();

		try {
			int totalTestCount = 0;
			int totalTestPassed = 0;
			int totalTestFailed = 0;
			int totalTestSkipped = 0;

			for (ISuite tempSuite : suites) {
				retBuf.append("<tr><td colspan=11><center><b>" + tempSuite.getName() + "</b></center></td></tr>");

				Map<String, ISuiteResult> testResults = tempSuite.getResults();

				for (ISuiteResult result : testResults.values()) {

					retBuf.append("<tr>");

					ITestContext testObj = result.getTestContext();

					totalTestPassed = testObj.getPassedTests().getAllMethods().size();
					totalTestSkipped = testObj.getSkippedTests().getAllMethods().size();
					totalTestFailed = testObj.getFailedTests().getAllMethods().size();

					totalTestCount = totalTestPassed + totalTestSkipped + totalTestFailed;

					/* Module Name. */
					retBuf.append("<td>");
					retBuf.append(testObj.getName());
					retBuf.append("</td>");

					/* Total test case count. */
					retBuf.append("<td>");
					retBuf.append(totalTestCount);
					totalCount = totalCount + totalTestCount;
					retBuf.append("</td>");

					/* Passed test case count. */
					retBuf.append("<td bgcolor=#3cb353>");
					retBuf.append(totalTestPassed);
					passTestCount = passTestCount + totalTestPassed;
					retBuf.append("</td>");

					/* Skipped test case count. */
					retBuf.append("<td bgcolor=#EEE8AA>");
					retBuf.append(totalTestSkipped);
					skipTestCount = skipTestCount + totalTestSkipped;
					retBuf.append("</td>");

					/* Failed test case count. */
					retBuf.append("<td bgcolor=#FF4500>");
					retBuf.append(totalTestFailed);
					failTestCount = failTestCount + totalTestFailed;
					retBuf.append("</td>");

					/*
					 * Get browser type. String browserType = tempSuite.getParameter("browserType");
					 * if(browserType==null || browserType.trim().length()==0) { browserType =
					 * "Chrome"; }
					 */

					/*
					 * Append browser type. retBuf.append("<td>"); retBuf.append(browserType);
					 * retBuf.append("</td>");
					 */

					/* Start Date */
					Date startDate = testObj.getStartDate();
					retBuf.append("<td>");
					retBuf.append(this.getTimeInStringFormat1(startDate));
					retBuf.append("</td>");

					/* End Date */
					Date endDate = testObj.getEndDate();
					retBuf.append("<td>");
					retBuf.append(this.getTimeInStringFormat1(endDate));
					retBuf.append("</td>");

					/* Execute Time */
					long deltaTime = endDate.getTime() - startDate.getTime();
					String deltaTimeStr = this.convertDeltaTimeToStringInHhMmSs(deltaTime);
					retBuf.append("<td>");
					retBuf.append(deltaTimeStr);
					retBuf.append("</td>");
					
					/* Build Number */
					String deploymentVersion = getAppDepolymentVersion();
					retBuf.append("<td>");
					retBuf.append(deploymentVersion);
					retBuf.append("</td>");

					/*
					 * Include groups. retBuf.append("<td>");
					 * retBuf.append(this.stringArrayToString(testObj.getIncludedGroups()));
					 * retBuf.append("</td>");
					 * 
					 * Exclude groups. retBuf.append("<td>");
					 * retBuf.append(this.stringArrayToString(testObj.getExcludedGroups()));
					 * retBuf.append("</td>");
					 */

					retBuf.append("</tr>");
				}
				/* Additing of total testcaseCount */
				retBuf.append("<tr>");

				retBuf.append("<td>");
				retBuf.append("Total Execution Count");
				retBuf.append("</td>");

				retBuf.append("<td>");
				retBuf.append(totalCount);
				retBuf.append("</td>");

				retBuf.append("<td bgcolor=#3cb353>");
				retBuf.append(passTestCount);
				retBuf.append("</td>");

				retBuf.append("<td bgcolor=#EEE8AA>");
				retBuf.append(skipTestCount);
				retBuf.append("</td>");

				retBuf.append("<td bgcolor=#FF4500>");
				retBuf.append(failTestCount);
				retBuf.append("</td>");

				retBuf.append("<tr>");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			return retBuf.toString();
		}
	}

	/* Get date string format value. */
	private String getDateInStringFormat(Date date) {
		StringBuffer retBuf = new StringBuffer();
		if (date == null) {
			date = new Date();
		}
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		retBuf.append(df.format(date));
		return retBuf.toString();
	}
	
	private String getTimeInStringFormat1(Date date) {
		StringBuffer retBuf = new StringBuffer();
		if (date == null) {
			date = new Date();
		}
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		retBuf.append(df.format(date));
		return retBuf.toString();
	}

	private String getTimeInStringFormat(Date date) {
		StringBuffer retBuf = new StringBuffer();
		if (date == null) {
			date = new Date();
		}
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		retBuf.append(df.format(date));
		return retBuf.toString();
	}

	/* Convert long type deltaTime to format hh:mm:ss:mi. */
	private String convertDeltaTimeToString(long deltaTime) {
		StringBuffer retBuf = new StringBuffer();
		long milli = deltaTime;

		String milliSec = "0";
		if (String.valueOf(milli).length() > 3)
			milliSec = String.valueOf(milli).substring(0, 3);
		else
			milliSec = String.valueOf(milli);
		/*
		 * long seconds = deltaTime / 1000; long minutes = seconds / 60; long hours =
		 * minutes / 60;
		 */
		long seconds = deltaTime / 1000 % 60;
		long minutes = deltaTime / (60 * 1000) % 60;
		long hours = deltaTime / (60 * 60 * 1000) % 24;
		retBuf.append(hours + ":" + minutes + ":" + seconds + ":" + milliSec);
		return retBuf.toString();
	}

	private String convertDeltaTimeToStringInHhMmSs(long deltaTime) {
		StringBuffer retBuf = new StringBuffer();
		long milli = deltaTime;
		/*
		 * long seconds = deltaTime / 1000; long minutes = seconds / 60; long hours =
		 * minutes / 60;
		 */
		long seconds = deltaTime / 1000 % 60;
		long minutes = deltaTime / (60 * 1000) % 60;
		long hours = deltaTime / (60 * 60 * 1000) % 24;

		retBuf.append(hours + ":" + minutes + ":" + seconds);
		return retBuf.toString();
	}

	/* Get test method summary info. */
	private String getTestMethodSummary(List<ISuite> suites) {
		StringBuffer retBuf = new StringBuffer();

		try {
			for (ISuite tempSuite : suites) {
				retBuf.append("<tr><td colspan=7><center><b>" + tempSuite.getName() + "</b></center></td></tr>");

				Map<String, ISuiteResult> testResults = tempSuite.getResults();

				for (ISuiteResult result : testResults.values()) {

					ITestContext testObj = result.getTestContext();

					String testName = testObj.getName();

					/* Get failed test method related data. */
					IResultMap testFailedResult = testObj.getFailedTests();
					String failedTestMethodInfo = this.getTestMethodReport(testName, testFailedResult, false, false);
					if (getStringCount("<td", failedTestMethodInfo) > 2)
						retBuf.append(failedTestMethodInfo);

					/* Get skipped test method related data. */
					IResultMap testSkippedResult = testObj.getSkippedTests();
					String skippedTestMethodInfo = this.getTestMethodReport(testName, testSkippedResult, false, true);
					if (getStringCount("<td", skippedTestMethodInfo) > 2)
						retBuf.append(skippedTestMethodInfo);

					/* Get passed test method related data. */
					IResultMap testPassedResult = testObj.getPassedTests();
					String passedTestMethodInfo = this.getTestMethodReport(testName, testPassedResult, true, false);
					if (getStringCount("<td", passedTestMethodInfo) > 2)
						retBuf.append(passedTestMethodInfo);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			return retBuf.toString();
		}
	}

	/* Get failed, passed or skipped test methods report. */
	private String getTestMethodReport(String testName, IResultMap testResultMap, boolean passedReault,
			boolean skippedResult) {
		StringBuffer retStrBuf = new StringBuffer();

		String resultTitle = testName;


		color = "#3cb353";


		if (skippedResult) {
			resultTitle += " - Skipped ";
			color = "#EEE8AA";
		} else {
			if (!passedReault) {
				resultTitle += " - Failed ";
				color = "#FF4500";
			} else {
				resultTitle += " - Passed ";
				color = "#3cb353";
			}
		}

		retStrBuf.append(
				"<tr bgcolor=" + color + "><td colspan=7><center><b>" + resultTitle + "</b></center></td></tr>");

		Set<ITestResult> testResultSet = testResultMap.getAllResults();
		// Sorting testClassName
		SortedSet<String> sortedTestsName = new TreeSet<>();
		for (ITestResult testResult : testResultSet) {
			sortedTestsName.add(testResult.getTestClass().getName());
		}
		// Sorting testMethodName
		SortedSet<String> sortedTestsMethodName = new TreeSet<>();
		for (ITestResult testResult : testResultSet) {
			sortedTestsMethodName.add(testResult.getMethod().getMethodName());
		}
		TreeMap<String, CustomTestNgReporterDto> customTestReport = new TreeMap<String, CustomTestNgReporterDto>();
		for (String testsName : sortedTestsName) {
			for (String testMethodName : sortedTestsMethodName) {
				testResultSet.forEach(testResult -> {
					if (testResult.getMethod().getMethodName().toString().equals(testMethodName)
							&& testResult.getTestClass().getName().toString().equals(testsName)) {
						CustomTestNgReporterDto objCustomTestNgReporterDto = new CustomTestNgReporterDto();
						objCustomTestNgReporterDto.setTestMathodName(testResult.getMethod().getMethodName());
						objCustomTestNgReporterDto.setTestClassName(testResult.getTestClass().getName());
						objCustomTestNgReporterDto.setStartTimeMillis(testResult.getStartMillis());
						objCustomTestNgReporterDto.setEndTimeMillis(testResult.getEndMillis());
						objCustomTestNgReporterDto
								.setDeltaMillis(testResult.getEndMillis() - testResult.getStartMillis());
						if(testResult.getThrowable()!=null)
							objCustomTestNgReporterDto.setLog(testResult.getThrowable().toString());
						else
							objCustomTestNgReporterDto.setLog("NA");
						customTestReport.put(testMethodName, objCustomTestNgReporterDto);
					}
				});

			}
		}
		for (String testsName : sortedTestsName) {
			testClassNameFlag = false;
			customTestReport.forEach((testMethod, object) -> {
				countTestClassName = 0;
				customTestReport.forEach((k, v) -> {
					if (v.getTestClassName().toString().equals(testsName))
						countTestClassName++;
				});
				if (object.getTestClassName().toString().equals(testsName)) {
					String testClassName = "";
					String testMethodName = "";
					String startDateStr = "";
					String endDateStr = "";
					String executeTimeStr = "";
					String log="";

					// Get testClassName
					String testClass[]=object.getTestClassName().toString().split(Pattern.quote("."));
					testClassName = testClass[testClass.length-1];

					// Get testMethodName
					testMethodName = testMethod.toString();

					// Get startDateStr
					startDateStr = this.getTimeInStringFormat(new Date(object.getStartTimeMillis()));

					// Get startDateStr
					endDateStr = this.getTimeInStringFormat(new Date(object.getEndTimeMillis()));

					// Get Execute time.
					executeTimeStr = this.convertDeltaTimeToString(object.getDeltaMillis());
					log=object.getLog();

					retStrBuf.append("<tr bgcolor=" + color + ">");

					if (!testClassNameFlag) {
						/* Add tests name. */
						retStrBuf.append("<td rowspan='" + countTestClassName + "'>");
						retStrBuf.append(testClassName);
						retStrBuf.append("</td>");
						testClassNameFlag = true;
					}

					/* Add test case name. */
					retStrBuf.append("<td>");
					retStrBuf.append(testMethodName);
					retStrBuf.append("</td>");

					/* Add start time. */
					retStrBuf.append("<td>");
					retStrBuf.append(startDateStr);
					retStrBuf.append("</td>");

					/* Add end time. */
					retStrBuf.append("<td>");
					retStrBuf.append(endDateStr);
					retStrBuf.append("</td>");

					/* Add execution time. */
					retStrBuf.append("<td>");
					retStrBuf.append(executeTimeStr);
					retStrBuf.append("</td>");

					retStrBuf.append("<td>");
					retStrBuf.append(log);
					retStrBuf.append("</td>");

					/*
					 * Add parameter. retStrBuf.append("<td>"); retStrBuf.append(paramStr);
					 * retStrBuf.append("</td>");
					 * 
					 * Add reporter message. retStrBuf.append("<td>");
					 * retStrBuf.append(reporterMessage); retStrBuf.append("</td>");
					 * 
					 * Add exception message. retStrBuf.append("<td>");
					 * retStrBuf.append(exceptionMessage); retStrBuf.append("</td>");
					 */


					retStrBuf.append("</tr>");
				}
			});
		}
		return retStrBuf.toString();
	}

	/* Convert a string array elements to a string. */
	private String stringArrayToString(String strArr[]) {
		StringBuffer retStrBuf = new StringBuffer();
		if (strArr != null) {
			for (String str : strArr) {
				retStrBuf.append(str);
				retStrBuf.append(" ");
			}
		}
		return retStrBuf.toString();
	}
	
	private int getStringCount(String whatToFind, String content) {
		int M = whatToFind.length();
		int N = content.length();
		int count = 0;

		/* A loop to slide pat[] one by one */
		for (int i = 0; i <= N - M; i++) {
			/*
			 * For current index i, check for pattern match
			 */
			int j;
			for (j = 0; j < M; j++) {
				if (content.charAt(i + j) != whatToFind.charAt(j)) {
					break;
				}
			}

			// if pat[0...M-1] = txt[i, i+1, ...i+M-1]
			if (j == M) {
				count++;
				j = 0;
			}
		}
		return count;
	}	

	private String encodeDefaultTestngReportFile() {
		// wait for file to load
		try {
			Thread.sleep(5000);	
			String content = FileUtils.readFileToString(new File(defaultTestNgEmailableReport).getAbsoluteFile(),"UTF-8");
			String base64encodedString = Base64.getEncoder().encodeToString(content.getBytes("UTF-8"));
			return base64encodedString;
		} catch (Exception e) {
			CustomTestNGReporterLog.error("Exception occured while encoding: " + e.getMessage());
			return "SomeThing went wrong with defaultTestNGFile";
		}
	}
	
	private String encodeExtentReportFile() {
		// wait for file to load
		try {
			Thread.sleep(5000);
			@SuppressWarnings("deprecation")
			String content = FileUtils.readFileToString(new File(extendtReport).getAbsoluteFile());
			String base64encodedString = Base64.getEncoder().encodeToString(content.getBytes("UTF-8"));
			return base64encodedString;
		} catch (Exception e) {
			CustomTestNGReporterLog.error("Exception occured while encoding: " + e.getMessage());
			return "SomeThing went wrong with Extent Report";
		}
	}
	

/*	private String getCurrentTimestampForReport() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return sdf.format(timestamp).toString();*/	

	private String getCurrentDateForReport() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy"); 
		LocalDateTime currentDateTime = LocalDateTime.now();
		return dtf.format(currentDateTime).toString();

	}
	
	private void removeOldCustomMosipReport(String outputDirectory) {
		File folder = new File(outputDirectory);
		for (int i = 0; i < folder.listFiles().length; i++) {
			if (folder.listFiles()[i].getName().contains(reportProfixFileName)) {
				if (folder.listFiles()[i].delete()) {
					CustomTestNGReporterLog.info("Old Report has been removed from directory successfuly..!");
				}
			}
		}
	}
	
	private String afterTestReportTittleContent()
	{
		return "</b>\r\n" + 
		"			<br>\r\n" + 
		"			<thead>\r\n" + 
		"				<tr>\r\n" + 
		"					<th>Module Name</th>\r\n" + 
		"					<th># Total Case</th>\r\n" + 
		"					<th># Passed</th>\r\n" + 
		"					<th># Skipped</th>\r\n" + 
		"					<th># Failed</th>\r\n" + 
		"					<th>Start Time<i><legend>(HH:MM:SS)</legend></i></th>\r\n" + 
		"					<th>End Time<i><legend>(HH:MM:SS)</legend></i></th>\r\n" + 
		"					<th>Execute Time<i><legend>(HH:MM:SS)</legend></i></th>\r\n" + 
		"					<th>Build Version</th>\r\n" + 
		"				</tr>\r\n" + 
		"			</thead>";
	}
	
	private String afterTestCaseSummaryContent()
	{
        return "</table>\r\n" + 
        "	</center>\r\n" + 
        "	<br><br><br><br><br><br><br><br><br>\r\n" + 
        "	<center>\r\n" + 
        "		<table id=\"test-summary\">\r\n" + 
        "			<thead>\r\n" + 
        "				<tr>\r\n" + 
        "					<th>API Name</th>\r\n" + 
        "					<th>TestCase</th>\r\n" + 
        "					<th>Start Time<i><legend>(HH:MM:SS)</legend></i></th>\r\n" + 
        "					<th>End Time<i><legend>(HH:MM:SS)</legend></i></th>\r\n" + 
        "					<th>Execution Time<i><legend>(HH:MM:SS:MilliSec)</legend></i></th>\r\n" + 
        "					 <th>Exception Message</th>\r\n" + 
        "				</tr>\r\n" + 
        "			</thead>";
	}
	private String afterTestMethodSummaryContent()
	{
		return "</table>\r\n" + 
		"	</center>\r\n" + 
		"</body>\r\n" + 
		"<br>\r\n" + 
		"<center>\r\n" + 
		"\r\n" + 
		"</center>\r\n" + 
		"</br>\r\n" + 
		"\r\n" + 
		"</html>";
	}

}
