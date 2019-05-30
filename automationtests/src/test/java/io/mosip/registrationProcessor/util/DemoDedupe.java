package io.mosip.registrationProcessor.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import io.mosip.dbaccess.RegProcStageDb;
import io.mosip.dbdto.DemoDedupeDto;

/**
 * This class is use for demo dedupe stage validations
 * 
 * @author Sayeri Mishra
 *
 */
public class DemoDedupe {

	private static Logger logger = Logger.getLogger(DemoDedupe.class);
	final static String configPath= "src/test/resources/regProc/StageValidation";
	final static String fileName = "/DummyDecryptedPacket/10011100110002020190326090045";

	/**
	 * This method contains the validation steps for demo dedupe stage
	 * @param dummyDecryptFile
	 * @return boolean true if demo dedupe is success, else false
	 */
	public boolean demoDedupeStage (File dummyDecryptFile){

		boolean isDemoDedupe = false;	
		boolean isAuth = false; 
		String regId = dummyDecryptFile.getName();
		RegProcStageDb dbConnect = new RegProcStageDb();

		//fetching individual record from db based on regId
		List<DemoDedupeDto> applicantDemoDto = dbConnect.regproc_IndividualDemoghraphicDedupe(regId);
		List<DemoDedupeDto> duplicateDtos = new ArrayList<>();

		//Fetching duplicate records for same name, gender, dob and lang code
		for (DemoDedupeDto demoDto : applicantDemoDto) {
			duplicateDtos.addAll(dbConnect.regproc_AllIndividualDemoghraphicDedupe(demoDto.getName(),
					demoDto.getGenderCode(), demoDto.getDob(), demoDto.getLangCode()));
		}
		logger.info("applicantDemoDto : "+applicantDemoDto.toString());
		logger.info("duplicateDtos : "+duplicateDtos.toString());

		Set<String> uniqueUins = new HashSet<>();
		Set<String> uniqueMatchedRefIds = new HashSet<>();
		List<String> uniqueMatchedRefIdList = new ArrayList<>();
		for (DemoDedupeDto demographicInfoDto : duplicateDtos) {
			uniqueUins.add(demographicInfoDto.getUin());
			uniqueMatchedRefIds.add(demographicInfoDto.getRegId());
		}
		uniqueMatchedRefIdList.addAll(uniqueMatchedRefIds);
		List<String> duplicateUINList = new ArrayList<>(uniqueUins);

		//authentication of biometric is not implemented, as of now, validation of biometric
		//is mocked
		try {
			if (!duplicateDtos.isEmpty()) {
				String dob = null;
				File[] folders = dummyDecryptFile.listFiles();
				for (int j = 0; j < folders.length; j++) {
					if(folders[j].getName().matches("Demographic")){
						File[] listOfDocsInDemographics = folders[j].listFiles();
						for (File d : listOfDocsInDemographics){
							if(d.getName().matches("ID.json")){
								JSONObject idData = (JSONObject) new JSONParser().parse(new FileReader(d.getPath()));
								JSONObject identity = (JSONObject) idData.get("identity");
								dob =  (String) identity.get("dateOfBirth");
								logger.info("dob : "+dob);
							}
						}
					}
				}
				//mocked logic for auth, if year in dob is even,
				//auth is validated, else not validated 
				int year =Integer.parseInt(dob.substring(0,4));
				logger.info("Year : "+year);
				if(year%2 ==0)
					isAuth = true;

				if(!isAuth)
					isDemoDedupe = true;
				else{
					logger.info("POTENTIAL MATCH");
					isDemoDedupe = false;
				}							
			}else{
				logger.info("DUPLICATE RECORD IS NOT THERE");
				isDemoDedupe = true;
			}
		} catch (IOException | ParseException e) {
			logger.error("Exception occurred in DemoDedupe class in demoDedupeStage method "+e);
		}
		logger.info("isDemoDedupe : "+isDemoDedupe);
		return isDemoDedupe;
	}

	@Test
	public void testRun(){
		DemoDedupe dd= new DemoDedupe();
		File dummyDecryptFile = new File(configPath+fileName);
		dd.demoDedupeStage(dummyDecryptFile);
	}
}
