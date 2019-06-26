package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;
import io.mosip.dbaccess.AbisDbTransactions;
import io.mosip.registrationProcessor.service.IntegMethods;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.service.BaseTestCase;

public class AbisMiddlewareTests extends BaseTestCase implements ITest{
	IntegMethods regProcRequests = new IntegMethods();
	AbisDbTransactions abisDbTransactions = new AbisDbTransactions();
	String regId="";
	String ref_id = "";
	List<String> abisRequestTransactionIds = new ArrayList<String>();
	List<String> abisResponseTransactionIds = new ArrayList<String>();
	Map<String, String> getMatchedId = new HashMap<String, String>();
	Map<String, Set<String>> mapOfIds=new HashMap<String,Set<String>>();
	private static Logger logger = Logger.getLogger(AbisMiddlewareTests.class);
	String moduleName = "RegProc";
	String apiName = "AbisMiddleWareTests";
	protected static String testCaseName = "";
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	@DataProvider(name = "AbisTestPackets")
	public File[] getBioDedupePackets() {
	//	File file = new File(System.getProperty("user.dir") + "/src/test/resources/regProc/AbisTestPackets");
		File file = new File(apiRequests.getResourcePath()+"regProc/AbisTestPackets");

		File[] listOfPackets = file.listFiles();
		List<File> insideFiles = new ArrayList<File>();

		for (File file1 : listOfPackets) {
			insideFiles.add(file1);
		}
		File[] objArray = new File[insideFiles.size()];
		for (int i = 0; i < insideFiles.size(); i++) {
			objArray[i] = insideFiles.get(i);
		}
		return objArray;
	}

	@Test(dataProvider = "AbisTestPackets", priority = 1)
	public void syncAndUploadPacket(File[] bioDedupePackets) {
		File file = new File(bioDedupePackets[0].getAbsolutePath());
		File[] listOfPackets = file.listFiles();
		for (File packets : listOfPackets) {
			if (packets.getName().contains(".zip")) {

				try {
					boolean syncResponse = regProcRequests.syncList(packets);
					if (syncResponse) {
						regProcRequests.UploadPacket(packets);
					}
				} catch (IOException | ParseException e) {
					logger.error("Could Not Sync", e);
				}
			}
		}

	}

	@Test(dataProvider = "AbisTestPackets", priority = 2)
	public void dedupeMatchStatus(File[] bioDedupePackets) {
		File file = new File(bioDedupePackets[0].getAbsolutePath());
		File[] listOfPackets = file.listFiles();
		for (File packets : listOfPackets) {
			if (packets.getName().contains("zip")) {
				regId = packets.getName().substring(0, packets.getName().lastIndexOf("."));
				try {
					ref_id = abisDbTransactions.bioDedupeFailedCheck(regId);
					abisRequestTransactionIds = abisDbTransactions.sendToAbisRequest(ref_id);
				} catch (Exception e) {
					logger.error("Could Not Extract Any Transaction For The Given Packet", e);
				}
			}

			if (ref_id.equals("")) {
				Assert.fail();
			} else
				Assert.assertTrue(true);
		}
	}

	@Test(dataProvider = "AbisTestPackets", priority = 3)
	public void assertAbisRequestTransactionIds(File[] bioDedupePackets) {
		if (abisRequestTransactionIds.isEmpty()) {
			Assert.fail();

		} else {
			Assert.assertTrue(true);
			abisResponseTransactionIds = abisDbTransactions.sendToAbisResponse(abisRequestTransactionIds);
		}
	}

	@Test(dataProvider = "AbisTestPackets", priority = 4)
	public void assertAbisResponseTransactionIds(File[] bioDedupePackets) {
		if (abisResponseTransactionIds.isEmpty()) {
			Assert.fail();
		} else {
			Assert.assertTrue(true);
			getMatchedId = abisDbTransactions.sendToAbisDetail(abisResponseTransactionIds);
		}
	}
	@Test(dataProvider = "AbisTestPackets", priority = 5)
	public void assertMatchedTransactionIds(File[] bioDedupePackets) {
		if(getMatchedId.isEmpty()) {
			Assert.fail();
		}else {
			Assert.assertTrue(true);
			mapOfIds=abisDbTransactions.extractIds(getMatchedId);
		}
	}
	@Test(dataProvider = "AbisTestPackets", priority = 6)
	public void assertBioDedupeIdsWithManualAdjudicationIds(File[] bioDedupePackets) {
		Set<String> setOfBioMatchIds = mapOfIds.get("matchedBioRefId");
		List<String> regIds = abisDbTransactions.extractRegIds(setOfBioMatchIds);
		List<String> bioMatchIds=abisDbTransactions.getStatusOfBiOmAtchIds(regIds);
		List<String> bioMatchIdsManualAdjudication=abisDbTransactions.getMatchedRegIds(regId);
		try {
			
			Assert.assertTrue(CollectionUtils.isEqualCollection(bioMatchIds, bioMatchIdsManualAdjudication));
		}catch (AssertionError e) {
			
			logger.error("The Ids in The Abis Table And ManualAdjudication Table Don't Match",e);
			logger.info("Id in the Abis Table are :: "+ bioMatchIds);
			logger.info("Ids in the Manual Adjudication Tables are :: "+ bioMatchIdsManualAdjudication);
			Assert.fail(); 
		}
	}
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
		Object[] obj =  (Object[]) testdata[0];
		String name="";
		String stageName="";
		if(obj[0] instanceof File) {
			File file=(File) obj[0];
			name=file.getName();
			stageName=file.getParentFile().getName();
		} 
		testCaseName =moduleName+"_"+apiName+"_"+ name.toString()+"_"+method.getName();
		
	}
 
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {

		Field method;
		try {
			method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, AbisMiddlewareTests.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in Sync class in setResultTestName method " + e);
		}
		
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}
}