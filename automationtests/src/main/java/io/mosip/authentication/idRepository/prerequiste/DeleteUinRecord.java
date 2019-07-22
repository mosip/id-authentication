package io.mosip.authentication.idRepository.prerequiste;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.authentication.fw.dto.RidDto;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.fw.util.DbConnection;
import io.mosip.authentication.fw.util.IdRepoUtil;
import io.mosip.authentication.fw.util.RIDUtil;
import io.mosip.authentication.fw.util.RunConfigUtil;

public class DeleteUinRecord extends AuthTestsUtil implements ITest{
	
	private  Logger logger = Logger.getLogger(DeleteUinRecord.class);
	
	protected static String testCaseName = "";
	
	@BeforeClass
	public void setConfig() {
		RunConfigUtil.getRunConfigObject("ida");
		RunConfigUtil.objRunConfig.setConfig("ida/TestData", "dummyFile", "dummy");
	}
	
	@Test
	public void deleteIdentityRecord() {
		DeleteUinRecord.testCaseName = "Delete generated Identity from DataBase";
		RIDUtil.setRidDto();
		Set<String> deletedRID = new HashSet<String>();
		for (Entry<String, String> ridEntry : RidDto.getRidData().entrySet()) {
			String rid = ridEntry.getKey();
			String getUinRefIdQuery = IdRepoUtil.getIdrepoUinRefIdQuery(rid);
			String uinRefId = DbConnection.getDataForQuery(getUinRefIdQuery, "IDREPO").get("uin_ref_id");
			String deleteBioMetricQuery = IdRepoUtil.deleteIdrepoBiometricQuery(uinRefId);
			DbConnection.getDataForQuery(deleteBioMetricQuery, "IDREPO");
			String deleteUinRecord = IdRepoUtil.deleteIdrepoUin(uinRefId);
			DbConnection.getDataForQuery(deleteUinRecord, "IDREPO");
			deletedRID.add(rid);
		}
		logger.info("Deleted Identity from DB : RID->" + deletedRID);
		logger.info("Cleaned all the generated data in database..!");
	}

	@Override
	public String getTestName() {
		return DeleteUinRecord.testCaseName;
	}
	
	/**
	 * The method ser current test name to result
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, DeleteUinRecord.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

}
