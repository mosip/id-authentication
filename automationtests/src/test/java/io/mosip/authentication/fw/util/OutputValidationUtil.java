package io.mosip.authentication.fw.util;

import java.nio.file.Files; 
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.testng.Reporter;

import com.google.common.base.Verify;

import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;

/**
 * Perform output validation between expected and actual json file or message
 * 
 * @author Vignesh
 *
 */
public class OutputValidationUtil extends IdaScriptsUtil{

	private static Logger logger = Logger.getLogger(OutputValidationUtil.class);
	private static FileUtil objFileUtil = new FileUtil();
	private JsonPrecondtion objJsonPrecondtion = new JsonPrecondtion();
	private static ReportUtil objReportUtil = new ReportUtil();
	private UinVidNumberUtil objUinVidNumberUtil = new UinVidNumberUtil();
	
	public Map<String, List<OutputValidationDto>> doOutputValidation(String actualOutputFile, String expOutputFile) {
		try {
			objJsonPrecondtion = new JsonPrecondtion(new String(Files.readAllBytes(Paths.get(actualOutputFile))));
			Map<String, String> actual = objJsonPrecondtion.getJsonFieldValue(actualOutputFile,
					objJsonPrecondtion.getPathList(actualOutputFile));
			objJsonPrecondtion = new JsonPrecondtion(new String(Files.readAllBytes(Paths.get(expOutputFile))));
			Map<String, String> exp = objJsonPrecondtion.getJsonFieldValue(expOutputFile,
					objJsonPrecondtion.getPathList(expOutputFile));
			actualOutputFile=actualOutputFile.substring(actualOutputFile.lastIndexOf("/")+1,actualOutputFile.length());
			expOutputFile=expOutputFile.substring(expOutputFile.lastIndexOf("/")+1, expOutputFile.length());
			return compareActuExpValue(actual,exp,actualOutputFile+ " vs "+expOutputFile);
		} catch (Exception e) {
			logger.error("Exceptione occured " + e.getMessage());
			return null;
		}
	}
	
	public Map<String, List<OutputValidationDto>> compareActuExpValue(Map<String, String> actual,
			Map<String, String> exp, String actVsExp) {
		Map<String, List<OutputValidationDto>> objMap = new HashMap<String, List<OutputValidationDto>>();
		List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
		for (Entry<String, String> actualEntry : actual.entrySet()) {
			OutputValidationDto objOpDto = new OutputValidationDto();
			if (!exp.containsKey(actualEntry.getKey())) {
				objOpDto.setFieldName(actualEntry.getKey());
				objOpDto.setFiedlHierarchy(actualEntry.getKey());
				objOpDto.setActualValue(actualEntry.getValue());
				objOpDto.setExpValue("NOT VERIFIED");
				objOpDto.setStatus("WARNING");
				objList.add(objOpDto);
			}
		}
		// Comparing value with actual json
		for (Entry<String, String> expEntry : exp.entrySet()) {
			OutputValidationDto objOpDto = new OutputValidationDto();
			if (actual.containsKey(expEntry.getKey())) {
				if (!expEntry.getValue().equals("$IGNORE$") && !expEntry.getValue().contains("$DECODE$")) {
					if (expEntry.getValue().equals(actual.get(expEntry.getKey()))) {
						objOpDto.setFieldName(expEntry.getKey());
						objOpDto.setFiedlHierarchy(expEntry.getKey());
						objOpDto.setActualValue(actual.get(expEntry.getKey()));
						objOpDto.setExpValue(expEntry.getValue());
						objOpDto.setStatus("PASS");
					} else if (expEntry.getValue().equals("$TIMESTAMP$")) {
						if (validateTimestamp(actual.get(expEntry.getKey()))) {
							objOpDto.setFieldName(expEntry.getKey());
							objOpDto.setFiedlHierarchy(expEntry.getKey());
							objOpDto.setActualValue(actual.get(expEntry.getKey()));
							objOpDto.setExpValue(expEntry.getValue());
							objOpDto.setStatus("PASS");
						} else {
							objOpDto.setFieldName(expEntry.getKey());
							objOpDto.setFiedlHierarchy(expEntry.getKey());
							objOpDto.setActualValue(actual.get(expEntry.getKey()));
							objOpDto.setExpValue(expEntry.getValue());
							objOpDto.setStatus("FAIL");
						}
					}else if (expEntry.getValue().equals("$TIMESTAMPZ$")) {
						if (validateTimestampZ(actual.get(expEntry.getKey()))) {
							objOpDto.setFieldName(expEntry.getKey());
							objOpDto.setFiedlHierarchy(expEntry.getKey());
							objOpDto.setActualValue(actual.get(expEntry.getKey()));
							objOpDto.setExpValue(expEntry.getValue());
							objOpDto.setStatus("PASS");
						} else {
							objOpDto.setFieldName(expEntry.getKey());
							objOpDto.setFiedlHierarchy(expEntry.getKey());
							objOpDto.setActualValue(actual.get(expEntry.getKey()));
							objOpDto.setExpValue(expEntry.getValue());
							objOpDto.setStatus("FAIL");
						}
					}else if (expEntry.getValue().contains("TOKENID:") && expEntry.getValue().contains(".")) {
						String key = expEntry.getValue().replace("TOKENID:", "");
						String[] keys= key.split(Pattern.quote("."));
						String tokenid=objUinVidNumberUtil.getTokenId(keys[0], keys[1]);
						if(tokenid.equals(actual.get(expEntry.getKey()))) {
							objOpDto.setFieldName(expEntry.getKey());
							objOpDto.setFiedlHierarchy(expEntry.getKey());
							objOpDto.setActualValue(actual.get(expEntry.getKey()));
							objOpDto.setExpValue(expEntry.getValue());
							objOpDto.setStatus("PASS");
						} else {
							objOpDto.setFieldName(expEntry.getKey());
							objOpDto.setFiedlHierarchy(expEntry.getKey());
							objOpDto.setActualValue(actual.get(expEntry.getKey()));
							objOpDto.setExpValue(expEntry.getValue());
							objOpDto.setStatus("FAIL");
						}
					} else if (expEntry.getValue().contains("$REGEXP")) {
						String extractRegex = expEntry.getValue().replace("$", "");
						String[] array = extractRegex.split(":");
						String regex = array[1];
						if (validateRegularExpression(actual.get(expEntry.getKey()), regex)) {
							objOpDto.setFieldName(expEntry.getKey());
							objOpDto.setFiedlHierarchy(expEntry.getKey());
							objOpDto.setActualValue(actual.get(expEntry.getKey()));
							objOpDto.setExpValue(expEntry.getValue());
							objOpDto.setStatus("PASS");
						} else {
							objOpDto.setFieldName(expEntry.getKey());
							objOpDto.setFiedlHierarchy(expEntry.getKey());
							objOpDto.setActualValue(actual.get(expEntry.getKey()));
							objOpDto.setExpValue(expEntry.getValue());
							objOpDto.setStatus("FAIL");
						}
					} else {
						objOpDto.setFieldName(expEntry.getKey());
						objOpDto.setFiedlHierarchy(expEntry.getKey());
						objOpDto.setActualValue(actual.get(expEntry.getKey()));
						objOpDto.setExpValue(expEntry.getValue());
						objOpDto.setStatus("FAIL");
					}
					objList.add(objOpDto);
				} else if (expEntry.getValue().contains("$DECODE$")) {
					String keyword = expEntry.getValue().toString();
					String content = actual.get(expEntry.getKey());
					System.out.println("checkcheck: " + content);
					String expKeyword = keyword.substring(keyword.lastIndexOf("->") + 2, keyword.length());
					String actKeyword = expKeyword.replace("expected", "actual");
					objFileUtil.createAndWriteFile(actKeyword, getDecodedData(content));
					Map<String, List<OutputValidationDto>> ouputValid = doOutputValidation(
							objFileUtil.getFilePath(getTestFolder(), actKeyword).toString(),
							objFileUtil.getFilePath(getTestFolder(), expKeyword).toString());
					Reporter.log(objReportUtil.getOutputValiReport(ouputValid));
					Verify.verify(publishOutputResult(ouputValid));
				}
			} else if(!expEntry.getValue().equals("$IGNORE$")) {
				objOpDto.setFieldName(expEntry.getKey());
				objOpDto.setFiedlHierarchy(expEntry.getKey());
				objOpDto.setActualValue("NOT AVAILABLE");
				objOpDto.setExpValue(expEntry.getValue());
				objOpDto.setStatus("FAIL");
				objList.add(objOpDto);
				logger.error("The expected json path " + expEntry.getKey() + " is not available in actual json");
			}
		}
		objMap.put(actVsExp, objList);
		return objMap;
	}
	
	public boolean validateTimestamp(String timestamp) {
		try {
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			String currentTimeStamp = ts.toString();
			if (!timestamp.substring(0, 4).equals(currentTimeStamp.substring(0, 4)))
				return false;
			if (!timestamp.substring(4, 5).equals("-"))
				return false;
			if (!(new Integer(timestamp.substring(5, 7)) <= 12))
				return false;
			if (!timestamp.substring(7, 8).equals("-"))
				return false;
			if (!(new Integer(timestamp.substring(8, 10)) <= 31))
				return false;
			if (!timestamp.substring(10, 11).equals("T"))
				return false;
			if (!(new Integer(timestamp.substring(11, 13)) <= 24))
				return false;
			if (!timestamp.substring(13, 14).equals(":"))
				return false;
			if (!(new Integer(timestamp.substring(14, 16)) <= 59))
				return false;
			if (!timestamp.substring(16, 17).equals(":"))
				return false;
			if (!(new Integer(timestamp.substring(17, 19)) <= 59))
				return false;
			if (!timestamp.substring(19, 20).equals("."))
				return false;
			if (!timestamp.substring(23, 24).equals("+"))
				return false;
			if (!timestamp.substring(26, 27).equals(":"))
				return false;
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	public boolean validateTimestampZ(String timestamp) {
		try {
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			String currentTimeStamp = ts.toString();
			if (!timestamp.substring(0, 4).equals(currentTimeStamp.substring(0, 4)))
				return false;
			if (!timestamp.substring(4, 5).equals("-"))
				return false;
			if (!(new Integer(timestamp.substring(5, 7)) <= 12))
				return false;
			if (!timestamp.substring(7, 8).equals("-"))
				return false;
			if (!(new Integer(timestamp.substring(8, 10)) <= 31))
				return false;
			if (!timestamp.substring(10, 11).equals("T"))
				return false;
			if (!(new Integer(timestamp.substring(11, 13)) <= 24))
				return false;
			if (!timestamp.substring(13, 14).equals(":"))
				return false;
			if (!(new Integer(timestamp.substring(14, 16)) <= 59))
				return false;
			if (!timestamp.substring(16, 17).equals(":"))
				return false;
			if (!(new Integer(timestamp.substring(17, 19)) <= 59))
				return false;
			if (!timestamp.substring(19, 20).equals("."))
				return false;
			if (!timestamp.substring(23, 24).equals("Z"))
				return false;
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	public boolean validateRegularExpression(String actValue, String regex) {
		if (Pattern.matches(regex, actValue))
			return true;
		else
			return false;
	}
	
	public boolean publishOutputResult(Map<String, List<OutputValidationDto>> outputresult) {
		boolean outputStatus = true;
		logger.info(
				"*******************************************Output validation*******************************************");
		for (Entry<String, List<OutputValidationDto>> entry : outputresult.entrySet()) {
			logger.info("* OutputValidaiton For : " + entry.getKey());
			for (OutputValidationDto dto : entry.getValue()) {
				logger.info("*");
				if (dto.getStatus().equals("PASS")) {
					logger.info("* JsonField Path :" + dto.getFieldName());
					logger.info("* Expected Value :" + dto.getExpValue());
					logger.info("* Actual value :" + dto.getActualValue());
					logger.info("* Status :" + dto.getStatus());
				}else if (dto.getStatus().equals("WARNING")) {
					logger.info("* JsonField Path :" + dto.getFieldName());
					logger.info("* Expected Value :" + dto.getExpValue());
					logger.info("* Actual value :" + dto.getActualValue());
					logger.info("* Status :" + dto.getStatus());
				}else if (dto.getStatus().equals("FAIL")) {
					logger.error("* JsonField Path :" + dto.getFieldName());
					logger.error("* Expected Value :" + dto.getExpValue());
					logger.error("* Actual value :" + dto.getActualValue());
					logger.error("* Status :" + dto.getStatus());
					outputStatus = false;
				}
			}
		}
		logger.info(
				"*******************************************************************************************************");
		return outputStatus;
	}
}
