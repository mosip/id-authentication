package io.mosip.util;

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

import io.mosip.dbaccess.RegProcStageDb;
import io.mosip.dbdto.DemoDedupeDto;

public class DemoDedupe {

	private static Logger logger = Logger.getLogger(DemoDedupe.class);
	final static String configPath= "src/test/resources/regProc/StageValidation";
	final static String fileName = "/DummyDecryptedPacket/10011100110002020190326090045";

	public boolean demoDedupeStage (File dummyDecryptFile) throws FileNotFoundException, IOException, ParseException{

		boolean isDemoDedupe = false;		
		String regId = dummyDecryptFile.getName();
		RegProcStageDb dbConnect = new RegProcStageDb();
		List<DemoDedupeDto> applicantDemoDto = dbConnect.regproc_IndividualDemoghraphicDedupe(regId);

		List<DemoDedupeDto> duplicateDtos = new ArrayList<>();
		for (DemoDedupeDto demoDto : applicantDemoDto) {
			duplicateDtos.addAll(dbConnect.regproc_AllIndividualDemoghraphicDedupe(demoDto.getName(),
					demoDto.getGenderCode(), demoDto.getDob(), demoDto.getLangCode()));
		}
		logger.info("applicantDemoDto : "+applicantDemoDto.toString());
		logger.info("demographicInfoDtos : "+duplicateDtos.toString());

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

		if (!duplicateDtos.isEmpty()) {
			String dob = null;
			File[] folders = dummyDecryptFile.listFiles();
			for (int j = 0; j < folders.length; j++) {
				if (folders[j].isDirectory()) {
					if(folders[j].getName().matches("Demographic")){
						File[] listOfDocsInDemographics = folders[j].listFiles();
						for (File d : listOfDocsInDemographics){
							if(d.getName().matches("ID.json")){
								JSONObject idData = (JSONObject) new JSONParser().parse(new FileReader(d.getPath()));
								JSONObject identity = (JSONObject) idData.get("identity");
								JSONObject dateOfBirth = (JSONObject) identity.get("dateOfBirth");
								dob = dateOfBirth.get("value").toString();
								logger.info("dob : "+dob);
							}	
						}
					}
				}
				int year =Integer.parseInt(dob);
				logger.info("Year : "+year);

				if(year%2 ==0)
					isDemoDedupe = true;
			}
		}
		return isDemoDedupe;
	}

	public static void main(String arg[]){
		DemoDedupe dd= new DemoDedupe();
		File dummyDecryptFile = new File(configPath+fileName);
		try {
			dd.demoDedupeStage(dummyDecryptFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
