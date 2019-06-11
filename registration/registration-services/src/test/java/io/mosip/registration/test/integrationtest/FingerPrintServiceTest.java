package io.mosip.registration.test.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.device.fp.FingerPrintCaptureService;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.service.security.AuthenticationService;
/**
 * @author Leona Mary S
 *
 *Validating the FingerPrint Service is working as expected with valid inputs and outputs
 */

public class FingerPrintServiceTest extends BaseIntegrationTest
{	
	/*@Autowired
	private FingerPrintCaptureService fingerprintservice;
	@Autowired
	private AuthenticationService authenticationService;
	
	@Before
	public void setvalue()
	{
	SessionContext.getInstance().getUserContext().setUserId(IntegrationTestConstants.userId_val);
	}
	
	@Test
	public void Validate_FingerPrintService_positive() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the positive flow for FingerPrintService
		String testDataPath=IntegrationTestConstants.FP_Path_10_FP_NotMatchWithDB;
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
		assertEquals(false, actualValue);
	}	
	
	@Test
	public void Validate_FingerPrintService_Negative() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the Negative flow for FingerPrintService
		String testDataPath=IntegrationTestConstants.FpPath_10_FP_Match_RightIndex_WithDB;
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		Map<String, Object> mapObject=new HashMap<>();
		SessionContext.getInstance().setMapObject(mapObject);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
		FingerprintDetailsDTO errorDTO=(FingerprintDetailsDTO) SessionContext.getInstance().getMapObject().get(RegistrationConstants.DUPLICATE_FINGER);
		assertEquals(true, actualValue);
	}
	

	@Test
	public void Validate_FingerPrintService_Positive_6FP() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the Negative flow for FingerPrintService
		String testDataPath=IntegrationTestConstants.FpPath_6_FP_NotMatchWithDB;
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		Map<String, Object> mapObject=new HashMap<>();
		SessionContext.getInstance().setMapObject(mapObject);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
		assertEquals(false, actualValue);
	}
	
	@Test
	public void Validate_FingerPrintService_Negative_6FP() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the Negative flow for FingerPrintService
		String testDataPath=IntegrationTestConstants.FpPath_6_FP_Match_RightIndex_WithDB;
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		Map<String, Object> mapObject=new HashMap<>();
		SessionContext.getInstance().setMapObject(mapObject);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
		FingerprintDetailsDTO errorDTO=(FingerprintDetailsDTO) SessionContext.getInstance().getMapObject().get(RegistrationConstants.DUPLICATE_FINGER);
		assertEquals(true, actualValue);
	}
	
	@Test
	public void Validate_SingleFP_Auth_Negative() throws IOException, ParseException
	{
	
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		String testDataPath=IntegrationTestConstants.FpPath_Single_FP_Auth_Not_matchWithDB;
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		authenticationValidatorDTO.setFingerPrintDetails(data);
		authenticationValidatorDTO.setUserId(IntegrationTestConstants.userId_val);
		authenticationValidatorDTO.setAuthValidationType(RegistrationConstants.VALIDATION_TYPE_FP_SINGLE);
		boolean actualValue=authenticationService.authValidator(RegistrationConstants.FINGERPRINT,
				authenticationValidatorDTO);
		assertEquals(false, actualValue);
	}
	
	
	@Test
	public void Validate_SingleFP_Auth_Positive() throws IOException, ParseException
	{
	
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		String testDataPath=IntegrationTestConstants.FpPath_Single_FP_Auth_Match_WithDB;
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		authenticationValidatorDTO.setFingerPrintDetails(data);
		authenticationValidatorDTO.setUserId(IntegrationTestConstants.userId_val);
		authenticationValidatorDTO.setAuthValidationType(RegistrationConstants.VALIDATION_TYPE_FP_SINGLE);
		boolean actualValue=authenticationService.authValidator(RegistrationConstants.FINGERPRINT,
				authenticationValidatorDTO);
		assertEquals(true, actualValue);
	}
	
	
public static List<FingerprintDetailsDTO> testData(String Path) throws IOException, ParseException {
	
	ObjectMapper mapper = new ObjectMapper();
	JSONParser jsonParser = new JSONParser();
	FileReader reader = new FileReader(Path);
    {
        //Read JSON file
        Object obj = jsonParser.parse(reader);

        JSONArray jArray = (JSONArray) obj;
      
        String s=jArray.toString();
        
        List<FingerprintDetailsDTO> fingerprintdetailsData = mapper.readValue(
                s,mapper.getTypeFactory().constructCollectionType(
                        List.class, FingerprintDetailsDTO.class));
     return fingerprintdetailsData;
}
}*/
}