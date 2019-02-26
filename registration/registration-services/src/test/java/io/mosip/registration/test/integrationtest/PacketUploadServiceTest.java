package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.packet.PacketUploadService;
import io.mosip.registration.test.integrationtest.Utils.DBUtil;
import io.mosip.registration.test.integrationtest.Utils.UserLibrary;
/**
 * @author Leona Mary S
 *
 *Validating whether Packet upload service is working as expected for invalid and valid inputs
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PacketUploadServiceTest extends BaseIntegrationTest {

	@Autowired
	PacketUploadService PUploadservice;

	private static Properties prop = UserLibrary.loadPropertiesFile();
	static List<String> a=new ArrayList<String>(100);

	@BeforeClass
	public static void getdbdata() {
		System.out.println("------- Before Class ----");
		DBUtil.createConnection();
		a=DBUtil.get_selectQuery(prop.getProperty("GET_SYNC_PACKETIDs"));
	}
	

	@Test
	public void validate_getSynchedPackets_1() {

		    System.out.println("Test case 1");
			List<String> actualres=a;
			List<String> expectedres=new ArrayList<String>(100);
 			//Fetching Data from database through JAVA API
			List<Registration> details=PUploadservice.getSynchedPackets();
			//System.out.println("==== "+details.size());
			for (int i = 0; i < details.size(); i++) {
				//System.out.println("==== "+details.get(i).getId());
				expectedres.add(details.get(i).getId());
			}
			 for (String i: actualres) {
		            if (expectedres.contains(i)) {
		                System.out.println("Packet ID fetched from Local database through API is equal");
		                break;
		            }
		
			 }}
			

	
	 @Test 
	 public void validate_pushPacket_2() 
	 {
		  System.out.println("Test case 2");
		  String expectedmsg="Registration packet is not in Sync with Sync table"; 
		File fileData=new File(
				
				"src/test/resources/testData/PacketUploadServiceData/10031100110016820190225124201.zip");
	 
	 try {
	 
	 
	 Object res=PUploadservice.pushPacket(fileData); 
	 Map<String,Object> m1=(Map<String, Object>)res;
	 Map<String,String>m2=(Map<String, String>) m1.get("error");
	 String actualmsg=m2.get("message");
		assertEquals(expectedmsg, actualmsg);
	} catch (RegBaseCheckedException e) { // TODO Auto-generated catch block
	e.printStackTrace(); } catch (URISyntaxException e) { // TODO Auto-generated
	e.printStackTrace(); }
	
	}
	
	 @Test 
	 public void validate_pushPacket_Neg_6() 
	 {
		  System.out.println("Test case 6");
		  String expectedmsg="success"; 
		File fileData=new File(
				"src/test/resources/testData/PacketUploadServiceData/10031100110016620190222120722.zip");
	 try {
	 Object res=PUploadservice.pushPacket(fileData); 
	 Map<String,Object> m1=(Map<String, Object>)res;
	// Map<String,String>m2=(Map<String, String>) m1.get("error");
	 String actualmsg=(String) m1.get("response");
		assertEquals(expectedmsg, actualmsg);
	} catch (RegBaseCheckedException e) { // TODO Auto-generated catch block
	e.printStackTrace(); } catch (URISyntaxException e) { // TODO Auto-generated
	e.printStackTrace(); }
	
	}

	
	@Test 
	public void validate_updateStatus_3() {
		  System.out.println("Test case 3");
	List<Registration> details = PUploadservice.getSynchedPackets();
	Boolean res=PUploadservice.updateStatus(details);
	System.out.println("validate_updateStatus== "+res);
	
	
	}
	@Test
	public void validate_uploadPacket_4() {
		
		PUploadservice.uploadPacket("10031100110016820190225124201");
		
		
	}
	
	@Test

	public void zvalidate_uploadEODPackets_5() {
		
		List<String> regIdsData = null;
		try {
			regIdsData = PacketUploadServiceTest.testData("src/test/resources/testData/PacketUploadServiceData/PacketSyncService_syncEODPackets_regIds.json");
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//packetsyncservice_syncEODPAckets_regIds
		PUploadservice.uploadEODPackets(regIdsData);
	
	
	}
	
	
	public static List<String> testData(String Path) throws IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(Path);
	    {
	        //Read JSON file
	        Object obj = jsonParser.parse(reader);

	        JSONArray jArray = (JSONArray) obj;
	      
	        String s=jArray.toString();
	        
	        List<String> regIds_data = mapper.readValue(
	                s,mapper.getTypeFactory().constructCollectionType(
	                        List.class, String.class));
	     return regIds_data;
	}
	}



}