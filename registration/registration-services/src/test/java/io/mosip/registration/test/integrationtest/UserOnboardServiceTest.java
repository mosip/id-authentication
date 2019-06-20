package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bridj.cpp.std.list;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SyncRegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.service.operator.UserOnboardService;
/**
 * @author Leona Mary S
 *
 *Validating whether UserOnboard service is working as expected for invalid and valid inputs
 */
public class UserOnboardServiceTest  extends BaseIntegrationTest{
	
	@Autowired
	UserOnboardService userOBservice;
	
	@Before
	public void setvalue()
	{
	RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
	registrationCenter.setRegistrationCenterId(IntegrationTestConstants.RegistrationCenterId_val);
	SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
	SessionContext.getInstance().getUserContext().setUserId(IntegrationTestConstants.userId_val);
	SessionContext.map().put(IntegrationTestConstants.stationId,IntegrationTestConstants.stationId_val);
	ApplicationContext.map().put(IntegrationTestConstants.USER_ON_BOARD_THRESHOLD_LIMIT,IntegrationTestConstants.USER_ON_BOARD_THRESHOLD_LIMIT_val);
	}
	
	
	@Test
	public void validate_getMachineCenterId()
	{
		String expectedCenterID=null;
		String expectedStatinID=null;
		Map<String,String> getres=userOBservice.getMachineCenterId();
		Set<Entry<String,String>> hashSet=getres.entrySet();
        for(Entry entry:hashSet ) {

        	if(entry.getKey().equals(IntegrationTestConstants.centerID))
        	{
        		expectedCenterID=entry.getValue().toString();
        	}
        	else {
				expectedStatinID=entry.getValue().toString();
			}
    
        	}
		System.out.println("expectedCenterID"+expectedCenterID);
		System.out.println("expectedStatinID"+expectedStatinID);
        assertEquals(expectedCenterID,IntegrationTestConstants.stationId_val);
        assertEquals(expectedStatinID, IntegrationTestConstants.stationId_val);
		
	}
	
	@Test
	public void Validate_userOB_null() {
		
		String expectedmsg=IntegrationTestConstants.userOBErrormsg;
		BiometricDTO biodto = null;
		try {
			biodto = testData(IntegrationTestConstants.UOB_Validate_null_path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResponseDTO dto=userOBservice.validate(biodto);
		String actualmsg=dto.getErrorResponseDTOs().get(0).getMessage();
		
		assertEquals(expectedmsg,actualmsg);
		
	
	}
	
	@Test
	public void Validate_userOB_9() {
		
		String expectedmsg=IntegrationTestConstants.userOBErrormsg;
		BiometricDTO biodto = null;
		try {
			biodto = testData(IntegrationTestConstants.UOB_NO_IRIS_NoTHUMB_9);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResponseDTO dto=userOBservice.validate(biodto);
		String actualmsg=dto.getErrorResponseDTOs().get(0).getMessage();
		
		assertEquals(expectedmsg,actualmsg);
		
	
	}
	
	
	@Test
	public void Validate_userOB_Positive() {
		
		String expectedmsg=IntegrationTestConstants.UOB_success_msg;
		BiometricDTO biodto = null;
		try {
			biodto = testData(IntegrationTestConstants.IRIS_FP_NoLeftThumbs_path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResponseDTO dto=userOBservice.validate(biodto);
		String actualmsg=dto.getSuccessResponseDTO().getMessage();
		assertEquals(expectedmsg,actualmsg);
		
	
	}
	@Test
	public void Validate_userOB_FP() {
		
		String expectedmsg=IntegrationTestConstants.UOB_success_msg;
		BiometricDTO biodto = null;
		try {
			biodto = testData(IntegrationTestConstants.FP_NoIRIS_path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResponseDTO dto=userOBservice.validate(biodto);
		String actualmsg=dto.getSuccessResponseDTO().getMessage();
		assertEquals(expectedmsg,actualmsg);
		
	
	}
	
	public static BiometricDTO testData(String Path) throws IOException, ParseException {
		
		ObjectMapper mapper = new ObjectMapper();
		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(Path);
	        //Read JSON file
	        Object obj = jsonParser.parse(reader);
	       // JSONArray jArray = (JSONArray) obj;
	    
	        String s=obj.toString();
	        
	        BiometricDTO biodto = mapper.readValue(s, BiometricDTO.class);
	        //		mapper.readValue(s,mapper.getTypeFactory().constructCollectionType(List.class, BiometricDTO.class));
	        biodto.getApplicantBiometricDTO();
	        
	     return biodto;
	
	}	

	
	

}
