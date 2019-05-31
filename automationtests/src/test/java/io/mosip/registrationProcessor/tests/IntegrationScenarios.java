package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.mosip.dbdto.RegistrationPacketSyncDTO;
import io.mosip.registrationProcessor.service.IntegMethods;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * 
 * @author M1047227
 *
 */

public class IntegrationScenarios extends BaseTestCase {
	EncryptData encryptData = new EncryptData();

	IntegMethods scenario = new IntegMethods();
	String moduleName="RegProc";
	String apiName="Integration";
	@DataProvider(name = "IntegrationScenarios")
	public File[] getIntegrationScenarioPackets() {
		
		File file = new File(System.getProperty("user.dir") + "/src/test/resources/regProc/IntegrationScenarios");
		File[] listOfPackets = file.listFiles();
		List<File> insideFiles=new ArrayList<File>();
	
		for(File file1:listOfPackets) {
			insideFiles.add(file1);
		}
		File [] objArray = new File[insideFiles.size()];
		for(int i=0;i< insideFiles.size();i++){
		    objArray[i] = insideFiles.get(i);
		 } 
		return objArray;
			
	}

	@Test(dataProvider = "IntegrationScenarios")
	public void syncSmokepacketUploadSmoke(File[] listOfInvpackets)
			throws FileNotFoundException, IOException, ParseException {
		File file1=new File(listOfInvpackets[0].getPath());
		File[] folder=file1.listFiles();
		for (File file : folder) {
			if (file.getName().contains(".zip")) {

				boolean syncResponse = scenario.syncList(file);
				if (syncResponse) {
					boolean uploadPacket = scenario.UploadPacket(file);
					if (uploadPacket) {
						scenario.getStatus("ValidPacket");
					}
				}

			}
		}

	}
	/*
	 * @Test public void syncSmoke_invalidDuplicatePacket() throws
	 * FileNotFoundException, IOException, ParseException { Response
	 * syncResponse=scenario.syncList("Sync_smoke");
	 * scenario.UploadPacket(syncResponse,"Invalid_duplicatePacket"); }
	 * 
	 * @Test public void invalidEmptyLangCode_packetUploadSmoke() throws
	 * FileNotFoundException, IOException, ParseException { Response response=
	 * scenario.syncList("Invalid_EmptyLangCode"); scenario.UploadPacket(response,
	 * "PacketReceiver_smoke"); }
	 * 
	 * @Test public void invalidEmptyRegistrationId_packetUploadSmoke() throws
	 * FileNotFoundException, IOException, ParseException { Response
	 * syncResponse=scenario.syncList("Invalid_EmptyRegistrationId");
	 * scenario.UploadPacket(syncResponse,"PacketReceiver_smoke"); }
	 * 
	 * @Test public void invalidSyncType_packetUploadSmoke() throws
	 * FileNotFoundException, IOException, ParseException { Response
	 * syncResponse=scenario.syncList("Invalid_SyncType");
	 * scenario.UploadPacket(syncResponse,"PacketReceiver_smoke"); }
	 * 
	 * @Test public void syncSmoke_invalidpacketWithoutSync() throws
	 * FileNotFoundException, IOException, ParseException { Response
	 * syncResponse=scenario.syncList("Sync_smoke");
	 * scenario.UploadPacket(syncResponse,"Invalid_packetWithoutSync"); }
	 */

}
