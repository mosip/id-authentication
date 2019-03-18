package io.mosip.authentication.fw.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;

/**
 * Class to show the result in table and text area format in testng report
 * 
 * @author Vignesh
 *
 */
public class ReportUtil {
	
	private static JsonPrecondtion objJsonPrecondtion = new JsonPrecondtion();
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final Charset ISO = Charset.forName("ISO-8859-1");
	private static Logger logger = Logger.getLogger(ReportUtil.class);

	/**
	 * Method to show the output validation result in table format in testng report
	 * 
	 * @param outputresult
	 * @return html table
	 */
	public String getOutputValiReport(Map<String, List<OutputValidationDto>> outputresult) {
		String htmlforReport = "<table width='90%' charset='UTF8'>\r\n" + "  <tr>\r\n" + "    <th>FieldName</th>\r\n"
				+ "    <th>Expected Value</th> \r\n" + "    <th>Actual Value</th>\r\n" + "    <th>Status</th>\r\n"
				+ "  </tr>\r\n";

		for (Entry<String, List<OutputValidationDto>> entry : outputresult.entrySet()) {
			Reporter.log("<b> Output validation for: </b>" + entry.getKey());
			for (OutputValidationDto dto : entry.getValue()) {
				if (dto.getStatus().equals("PASS")) {
					htmlforReport = htmlforReport + "  <tr>\r\n" + "    <td>" + dto.getFieldName() + "</td>\r\n"
							+ "    <td>" + dto.getExpValue() + "</td>\r\n" + "    <td>" + dto.getActualValue()
							+ "</td>\r\n" + "    <td bgcolor='Green'>" + dto.getStatus() + "</td>\r\n" + "  </tr>\r\n";
				} else if (dto.getStatus().equals("FAIL")) {
					htmlforReport = htmlforReport + "  <tr>\r\n" + "    <td>" + dto.getFieldName() + "</td>\r\n"
							+ "    <td>" + dto.getExpValue() + "</td>\r\n" + "    <td>" + dto.getActualValue()
							+ "</td>\r\n" + "    <td bgcolor='RED'>" + dto.getStatus() + "</td>\r\n" + "  </tr>\r\n";
				}
				else if (dto.getStatus().equals("WARNING")) {
					htmlforReport = htmlforReport + "  <tr>\r\n" + "    <td>" + dto.getFieldName() + "</td>\r\n"
							+ "    <td>" + dto.getExpValue() + "</td>\r\n" + "    <td>" + dto.getActualValue()
							+ "</td>\r\n" + "    <td bgcolor='LIGHTYELLOW'>" + dto.getStatus() + "</td>\r\n" + "  </tr>\r\n";
				}
			}
		}
		htmlforReport = htmlforReport + "</table>";
		return htmlforReport;
	}


	/**
	 * Publish the request and response message in textarea 
	 * 
	 * @param content
	 * @return test area html
	 */
	public String getTextAreaJsonMsgHtml(String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<textarea style='border:solid 1px white;' name='message' rows='20' cols='160' readonly='true'>");
		sb.append(objJsonPrecondtion.toPrettyFormat(content));
		sb.append("</textarea>");
		return sb.toString();
	}
	
	public void moveReport(String currentModule) {
		Path temp = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmssSSS");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		String date = sdf.format(c.getTime());
		try {
			Path sourcePath = Paths.get("test-output/" + "emailable-report.html");
			Path DesPath = Paths.get(
					"src/test/resources/" + "Reports" + "/" + currentModule + "-emailable-report-" + date + ".html");
			boolean createCurrentPathStatus = new File("src/test/resources/Reports/current-build-reports").mkdirs();
			boolean createBackupPathStatus = new File("src/test/resources/Reports/backup-build-reports").mkdirs();
			Path currentPathWithFileName = Paths.get(
					"src/test/resources/Reports/current-build-reports/" + currentModule + "-emailable-report.html");
			Path backupPathWithFileName = Paths.get("src/test/resources/Reports/backup-build-reports/" + currentModule
					+ "-emailable-report-" + date + ".html");
			logger.info("createCurrentPathStatus---->" + createCurrentPathStatus);
			logger.info("backupPathWithFileName---->" + backupPathWithFileName);
			temp = Files.copy(sourcePath, currentPathWithFileName, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			temp = Files.copy(sourcePath, backupPathWithFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (temp != null) {
			logger.error("File renamed and moved successfully");
		} else {
			logger.error("Failed to move the file");
		}
	}
}

