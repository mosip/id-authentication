package testPackage;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.service.AuthenticationService;
import io.mosip.registration.service.device.impl.FingerPrintCaptureServiceImpl;
/**
 * @author Leona Mary S
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class FingerPrintServiceTest
{	
	@Autowired
	private FingerPrintCaptureServiceImpl fingerprintservice;
	@Autowired
	private AuthenticationService authenticationService;
	
	@Test
	public void Validate_FingerPrintService_positive() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the positive flow for FingerPrintService
		System.out.println("Test Case 1 == Verify the positive flow for FingerPrintService");
		String testDataPath="src/test/resources/testData/FingerPrintCaptureServiceData/Non_matchDB.json";
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
		System.out.println("=== Result of Finger Print Service===  "+actualValue);
		assertEquals(false, actualValue);
		if (!actualValue) {
			System.out.println("The Finger Print is validated with existing DB fingerPrint details == No matching Found in DB ==");
		}
	}	
	
	@Test
	public void Validate_FingerPrintService_Negative() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the Negative flow for FingerPrintService
		System.out.println("Test Case 2 == Verify the Negative flow for FingerPrintService by passing existing details from DB");
		String testDataPath="src/test/resources/testData/FingerPrintCaptureServiceData/right_index_matchDB.json";
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		Map<String, Object> mapObject=new HashMap<>();
		SessionContext.getInstance().setMapObject(mapObject);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
		FingerprintDetailsDTO errorDTO=(FingerprintDetailsDTO) SessionContext.getInstance().getMapObject().get(RegistrationConstants.DUPLICATE_FINGER);
		System.out.println(errorDTO.getFingerType()+" is already present in DB");
		assertEquals(true, actualValue);
	}
	

	@Test
	public void Validate_FingerPrintService_Positive_6prints() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the Negative flow for FingerPrintService
		System.out.println("Test Case 3 == Verify the Positive flow for FingerPrintService by passing 6 finger details");
		String testDataPath="src/test/resources/testData/FingerPrintCaptureServiceData/Non_matchDB_6.json";
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		Map<String, Object> mapObject=new HashMap<>();
		SessionContext.getInstance().setMapObject(mapObject);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
		assertEquals(false, actualValue);
	}
	
	@Test
	public void Validate_FingerPrintService_Negative_6prints() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the Negative flow for FingerPrintService
		System.out.println("Test Case 4 == Verify the Negative flow for FingerPrintService by passing existing details from DB");
		String testDataPath="src/test/resources/testData/FingerPrintCaptureServiceData/matchDB_6.json";
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		Map<String, Object> mapObject=new HashMap<>();
		SessionContext.getInstance().setMapObject(mapObject);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
		FingerprintDetailsDTO errorDTO=(FingerprintDetailsDTO) SessionContext.getInstance().getMapObject().get(RegistrationConstants.DUPLICATE_FINGER);
		System.out.println(errorDTO.getFingerType()+" is already present in DB");
		assertEquals(true, actualValue);
	}
	
	
	@Test
	public void Validate_FingerPrintService_2PrintsSame() throws JsonParseException, JsonMappingException, IOException, ParseException {
          
		//Verify the Negative flow for FingerPrintService
		System.out.println("Test Case 5 == Verify the Negative flow for FingerPrintService by passing existing details from DB");
		String testDataPath="src/test/resources/testData/FingerPrintCaptureServiceData/Non_matchDB_2printsSame.json";
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		Map<String, Object> mapObject=new HashMap<>();
		SessionContext.getInstance().setMapObject(mapObject);
		boolean actualValue=fingerprintservice.validateFingerprint(data);
//		FingerprintDetailsDTO errorDTO=(FingerprintDetailsDTO) SessionContext.getInstance().getMapObject().get(RegistrationConstants.DUPLICATE_FINGER);
	//	System.out.println(errorDTO.getFingerType()+" is already present in DB");
		assertEquals(false, actualValue);
	}
	
	@Test
	public void Validate_FingerPrintSingleAuth_false() throws IOException, ParseException
	{
	
		System.out.println("Test Case 6 == Verify fingerPrint auhtentication");
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		String testDataPath="src/test/resources/testData/FingerPrintCaptureServiceData/SingleAuth_NonmatchDB.json";
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		authenticationValidatorDTO.setFingerPrintDetails(data);
		authenticationValidatorDTO.setAuthValidationType(RegistrationConstants.VALIDATION_TYPE_FP_SINGLE);
		System.out.println(authenticationService.authValidator(RegistrationConstants.FINGERPRINT,
				authenticationValidatorDTO));
		boolean actualValue=authenticationService.authValidator(RegistrationConstants.FINGERPRINT,
				authenticationValidatorDTO);
		assertEquals(false, actualValue);
	}
	
	
	@Test
	public void Validate_FingerPrintSingleAuth_true() throws IOException, ParseException
	{
	
		System.out.println("Test Case 7 == Verify fingerPrint auhtentication");
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		String testDataPath="src/test/resources/testData/FingerPrintCaptureServiceData/SingleAuth_matchDB.json";
		List<FingerprintDetailsDTO> data=FingerPrintServiceTest.testData(testDataPath);
		//FingerprintDetailsDTO d=data.get(0);
		//System.out.println("-----"+d.getFingerType());
		authenticationValidatorDTO.setFingerPrintDetails(data);
		authenticationValidatorDTO.setAuthValidationType(RegistrationConstants.VALIDATION_TYPE_FP_SINGLE);
		System.out.println(authenticationService.authValidator(RegistrationConstants.FINGERPRINT,
				authenticationValidatorDTO));
		boolean actualValue=authenticationService.authValidator(RegistrationConstants.FINGERPRINT,
				authenticationValidatorDTO);
		assertEquals(true, actualValue);
	}
	
	@AfterClass
	public static void cleanUp() {
		System.exit(0);
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
}
} 