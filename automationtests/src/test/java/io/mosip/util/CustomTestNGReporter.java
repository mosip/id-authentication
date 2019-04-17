package io.mosip.util;

import org.testng.IReporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.testng.IInvokedMethod;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

/**
 * Customised Testng Report
 * 
 * @author Vignesh
 *
 */
public class CustomTestNGReporter implements IReporter {

	// This is the customize emailable report template file path.
	private static final String emailableReportTemplateFile = new File(
			"./src/test/resources/customize-emailable-report-template.html").getAbsolutePath();
	private static String customReportTemplateStr;
	// PieChart
	private int passTestCount = 0;
	private int skipTestCount = 0;
	private int failTestCount = 0;
	private int totalCount = 0;

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		try {
			// Get content data in TestNG report template file.
			customReportTemplateStr = this.readEmailabelReportTemplate();

			// Create custom report title.
			String customReportTitle = this.getCustomReportTitle("MOSIP API Test Report");

			// Create test suite summary data.
			String customSuiteSummary = this.getTestSuiteSummary(suites);

			// Create test methods summary data.
			String customTestMethodSummary = this.getTestMehodSummary(suites);

			// Replace report title place holder with custom title.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$TestNG_Custom_Report_Title\\$",
					customReportTitle);

			// Replace test suite place holder with custom test suite summary.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Summary\\$", customSuiteSummary);

			// Replace test methods place holder with custom test method summary.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Detail\\$",
					customTestMethodSummary);
			customReportTemplateStr = updatePieChart(customReportTemplateStr);
			customReportTemplateStr=customReportTemplateStr.replaceAll("\\$detailedReport\\$", '"'+encodeDefaultTestngReportFile()+'"');
			// Write replaced test report content to custom-emailable-report.html.
			File targetFile = new File(outputDirectory + "/custom-emailable-report.html");
			FileWriter fw = new FileWriter(targetFile);
			fw.write(customReportTemplateStr);
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
	private String readEmailabelReportTemplate() {
		StringBuffer retBuf = new StringBuffer();

		try {

			File file = new File(this.emailableReportTemplateFile);
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
			return retBuf.toString();
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
					retBuf.append(this.getTimeInStringFormat(startDate));
					retBuf.append("</td>");

					/* End Date */
					Date endDate = testObj.getEndDate();
					retBuf.append("<td>");
					retBuf.append(this.getTimeInStringFormat(endDate));
					retBuf.append("</td>");

					/* Execute Time */
					long deltaTime = endDate.getTime() - startDate.getTime();
					String deltaTimeStr = this.convertDeltaTimeToStringInHhMmSs(deltaTime);
					retBuf.append("<td>");
					retBuf.append(deltaTimeStr);
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
		long seconds = deltaTime / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		retBuf.append(hours + ":" + minutes + ":" + seconds + "." + milli);
		return retBuf.toString();
	}
	
	private String convertDeltaTimeToStringInHhMmSs(long deltaTime) {
		StringBuffer retBuf = new StringBuffer();
		long milli = deltaTime;
		long seconds = deltaTime / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		retBuf.append(hours + ":" + minutes + ":" + seconds);
		return retBuf.toString();
	}

	/* Get test method summary info. */
	private String getTestMehodSummary(List<ISuite> suites) {
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

		String color = "#3cb353";

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

		for (ITestResult testResult : testResultSet) {
			String testClassName = "";
			String testMethodName = "";
			String startDateStr = "";
			String endDateStr = "";
			String executeTimeStr = "";
			String paramStr = "";
			String reporterMessage = "";
			String exceptionMessage = "";

			// Get testClassName
			testClassName = testResult.getTestClass().getName();

			// Get testMethodName
			testMethodName = testResult.getMethod().getMethodName();

			// Get startDateStr
			long startTimeMillis = testResult.getStartMillis();
			startDateStr = this.getTimeInStringFormat(new Date(startTimeMillis));
			
			// Get startDateStr
			long endTimeMillis = testResult.getEndMillis();
			endDateStr = this.getTimeInStringFormat(new Date(endTimeMillis));

			// Get Execute time.
			long deltaMillis = testResult.getEndMillis() - testResult.getStartMillis();
			executeTimeStr = this.convertDeltaTimeToString(deltaMillis);

			// Get parameter list.
			/*
			 * Object paramObjArr[] = testResult.getParameters(); for(Object paramObj :
			 * paramObjArr) { paramStr += (String)paramObj; paramStr += " "; }
			 */

			// Get reporter message list.
			/*
			 * List<String> repoterMessageList = Reporter.getOutput(testResult); for(String
			 * tmpMsg : repoterMessageList) { reporterMessage += tmpMsg; reporterMessage +=
			 * " "; }
			 */

			// Get exception message.
			Throwable exception = testResult.getThrowable();
			if (exception != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				exception.printStackTrace(pw);

				exceptionMessage = sw.toString();
			}

			retStrBuf.append("<tr bgcolor=" + color + ">");

			/* Add tests name. */
			retStrBuf.append("<td>");
			retStrBuf.append(testClassName);
			retStrBuf.append("</td>");

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
	
	private int getStringCount(String whatToFind,String content)
	{
		int M = whatToFind.length();         
        int N = content.length();         
        int count = 0; 
  
        /* A loop to slide pat[] one by one */
        for (int i = 0; i <= N - M; i++) { 
            /* For current index i, check for  
        pattern match */
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
	
	@SuppressWarnings("deprecation")
	private String encodeDefaultTestngReportFile() throws IOException
	{
		String content=FileUtils.readFileToString(new File("./target/surefire-reports/emailable-report.html").getAbsoluteFile()); 
		String base64encodedString = Base64.getEncoder().encodeToString(
				content.getBytes("utf-8"));
		return base64encodedString;
	}

}