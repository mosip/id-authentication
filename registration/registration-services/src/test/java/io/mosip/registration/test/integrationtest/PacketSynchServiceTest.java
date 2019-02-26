package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
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

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.sync.PacketSynchService;
import io.mosip.registration.test.integrationtest.Utils.DBUtil;
import io.mosip.registration.test.integrationtest.Utils.UserLibrary;
/**
 * @author Leona Mary S
 *
 *Validating whether Packet Sync service is working as expected for invalid and valid inputs
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PacketSynchServiceTest extends BaseIntegrationTest{
		
		@Autowired
		PacketSynchService PsyncService;
	
		private static Properties prop = UserLibrary.loadPropertiesFile();
		static List<String> a=new ArrayList<String>(100);
	
		@BeforeClass
		public static void getdbdata() {
			System.out.println("------- Before Class ----");
			DBUtil.createConnection();
			a=DBUtil.get_selectQuery(prop.getProperty("GET_SYNC_PACKETIDs"));
		}
		
		@Test
		public void validate_fetchPacketsToBeSynched_1()
		{
			System.out.println("Test case 1");
			List<String> actualres=a;
			List<String> expectedres=new ArrayList<String>(100);
 			//Fetching Data from database through JAVA API
			List<Registration> details=PsyncService.fetchPacketsToBeSynched();
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
		public void validate_packetSync_2() {
			System.out.println("Test case 2");
			try {
				String res=PsyncService.packetSync("10031100110016920190225130805");
				if (res.isEmpty()) {
					System.out.println("Error while packet sync");
				}
				System.out.println("packetSync== "+res);
			} catch (RegBaseCheckedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		@Test public void validate_updateStatus_4() {
			System.out.println("Test case 4");
			Boolean expectedval=true;
			List<Registration> details = PsyncService.fetchPacketsToBeSynched();
			Boolean actualval=PsyncService.updateSyncStatus(details);
			System.out.println("validate_updateStatus== "+actualval);
			assertEquals(expectedval, actualval);
			
			}
			
		
		
			@Test
			public void validate_syncEODPackets_3()
			{
				System.out.println("Test case 3");
				List<String> regIds = null;
				try {
					regIds = PacketSynchServiceTest.gettestData("src/test/resources/testData/PacketUploadServiceData/PacketSyncService_syncEODPackets_regIds.json");
				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					String res=PsyncService.syncEODPackets(regIds);
					if (res.isEmpty()) {
						System.out.println("Error while packet sync");
					}
					System.out.println("packetSync== "+res);
				} catch (RegBaseCheckedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
			
			@Test
			public void zvalidate_syncPacketsToServer_6()
				{
				System.out.println("Test case 6");
				List<SyncRegistrationDTO> dtoList=null;
				String expectedmsg="success";
				
				try {
					dtoList = testData("src/test/resources/testData/PacketSynchServiceData/PacketSyncService__syncPacketsToServer_syncDtoList_pos.json");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				try {
					Object response=PsyncService.syncPacketsToServer(dtoList);
					Map<String,Object> m1=(Map<String, Object>)response;
				//	Map<String,String>m2=(Map<String, String>) m1.get("error");
					String actualmsg=(String) m1.get("response");
					assertEquals(expectedmsg, actualmsg);
			
					
				} catch (RegBaseCheckedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
					
			}
		
		
			@Test
			public void zvalidate_syncPacketsToServer_5()
				{
				System.out.println("Test case 5");
				List<SyncRegistrationDTO> dtoList=null;
				String expectedmsg="Json Data Mapping Exception";
				
				try {
					dtoList = testData("src/test/resources/testData/PacketSynchServiceData/PacketSyncService__syncPacketsToServer_syncDtoList.json");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				try {
					Object response=PsyncService.syncPacketsToServer(dtoList);
					Map<String,Object> m1=(Map<String, Object>)response;
					Map<String,String>m2=(Map<String, String>) m1.get("error");
					String actualmsg=m2.get("message");
					assertEquals(expectedmsg, actualmsg);
			
					
				} catch (RegBaseCheckedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
					
			}
		
			
			
			
			public static List<SyncRegistrationDTO> testData(String Path) throws IOException, ParseException {
				
				ObjectMapper mapper = new ObjectMapper();
				JSONParser jsonParser = new JSONParser();
				FileReader reader = new FileReader(Path);
			    {
			        //Read JSON file
			        Object obj = jsonParser.parse(reader);

			        JSONArray jArray = (JSONArray) obj;
			      
			        String s=jArray.toString();
			        
			        List<SyncRegistrationDTO> SyncRegData = mapper.readValue(
			                s,mapper.getTypeFactory().constructCollectionType(
			                        List.class, SyncRegistrationDTO.class));
			     return SyncRegData;
			}}
			 
			    public static List<String> gettestData(String Path) throws IOException, ParseException {
					
					ObjectMapper mapper = new ObjectMapper();
					JSONParser jsonParser = new JSONParser();
					FileReader reader = new FileReader(Path);
				    {
				        //Read JSON file
				        Object obj = jsonParser.parse(reader);

				        JSONArray jArray = (JSONArray) obj;
				      
				        String s=jArray.toString();
				        
				        List<String> getIds = mapper.readValue(
				                s,mapper.getTypeFactory().constructCollectionType(
				                        List.class, String.class));
				     return getIds;
				}
			    
			}	


}
