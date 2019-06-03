package io.mosip.registrationProcessor.tests;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import io.mosip.service.BaseTestCase;
import io.mosip.service.IntegMethods;
import io.restassured.response.Response;
/**
 * 
 * @author M1047227
 *
 */
public class IntegrationScenarios extends BaseTestCase {
	IntegMethods scenario=new IntegMethods();
	@Test
	public void syncSmoke_packetUploadSmoke() throws FileNotFoundException, IOException, ParseException {
		Response syncResponse=scenario.syncList("ValidPacket");
		scenario.UploadPacket(syncResponse,"ValidPacket");
		scenario.getStatus("ValidPacket");
	}
	/*@Test
	public void syncSmoke_invalidDuplicatePacket() throws FileNotFoundException, IOException, ParseException {
		Response syncResponse=scenario.syncList("Sync_smoke");
		scenario.UploadPacket(syncResponse,"Invalid_duplicatePacket");
	}
	@Test
	public void invalidEmptyLangCode_packetUploadSmoke() throws FileNotFoundException, IOException, ParseException {
		Response response= scenario.syncList("Invalid_EmptyLangCode");
		scenario.UploadPacket(response, "PacketReceiver_smoke");
	}
	@Test
	public void invalidEmptyRegistrationId_packetUploadSmoke() throws FileNotFoundException, IOException, ParseException {
		Response syncResponse=scenario.syncList("Invalid_EmptyRegistrationId");
		scenario.UploadPacket(syncResponse,"PacketReceiver_smoke");
	}
	@Test
	public void invalidSyncType_packetUploadSmoke() throws FileNotFoundException, IOException, ParseException {
		Response syncResponse=scenario.syncList("Invalid_SyncType");
		scenario.UploadPacket(syncResponse,"PacketReceiver_smoke");
	}
	@Test
	public void syncSmoke_invalidpacketWithoutSync() throws FileNotFoundException, IOException, ParseException {
		Response syncResponse=scenario.syncList("Sync_smoke");
		scenario.UploadPacket(syncResponse,"Invalid_packetWithoutSync");
	}*/
	
}
