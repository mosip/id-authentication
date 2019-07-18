package io.mosip.authentication.fw.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	/**
	 * Method to show the output validation result in table format in testng report
	 * 
	 * @param outputresultRunConfigUtil.getResourcePath()
	 * @return html table
	 */
	public static String getOutputValiReport(Map<String, List<OutputValidationDto>> outputresult) {
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
	public static String getTextAreaJsonMsgHtml(String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<textarea style='border:solid 1px white;' name='message' rows='20' cols='160' readonly='true'>");
		sb.append(JsonPrecondtion.toPrettyFormat(content));
		sb.append("</textarea>");
		return sb.toString();
	}
}
