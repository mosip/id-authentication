package io.mosip.registration.processor.stages.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.applicantcategory.ApplicantTypeDocument;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
public class ApplicantDocumentValidationTest {

	@Mock
	Utilities utility;

	@Mock
	ObjectMapper mapIdentityJsonStringToObject;

	@Mock
	private Environment env;

	@Mock
	ApplicantTypeDocument applicantTypeDocument;
	MessageDTO dto = new MessageDTO();
	String jsonStringID = null;
	String jsonStringIdMapping = null;
	String jsonStringApp = null;
	private static final String AGE_THRESHOLD = "mosip.kernel.applicant.type.age.limit";
	ApplicantDocumentValidation applicantDocumentValidation;

	@Before
	public void setUp() throws IOException, ApisResourceAccessException, ParseException {

		dto.setRid("2018701130000410092018110735");

		Mockito.when(env.getProperty(AGE_THRESHOLD)).thenReturn("5");
		Mockito.when(utility.getApplicantAge(any())).thenReturn(18);

		PowerMockito.mockStatic(Utilities.class);
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");
		InputStream inputStream = new FileInputStream("src/test/resources/ID.json");
		byte[] bytes = IOUtils.toByteArray(inputStream);
		jsonStringID = new String(bytes);

		InputStream inputStream2 = new FileInputStream("src/test/resources/IDObject_DocumentCategory_Mapping.json");
		byte[] bytes2 = IOUtils.toByteArray(inputStream2);
		jsonStringIdMapping = new String(bytes2);
		Mockito.when(Utilities.getJson(any(), any())).thenReturn(jsonStringIdMapping);

		InputStream inputStream3 = new FileInputStream("src/test/resources/ApplicantType_Document_Mapping.json");
		byte[] bytes3 = IOUtils.toByteArray(inputStream3);
		jsonStringApp = new String(bytes3);

		Mockito.when(utility.getGetRegProcessorApplicantType()).thenReturn("type");
		Mockito.when(Utilities.getJson(null, "type")).thenReturn(jsonStringApp);

		applicantDocumentValidation = new ApplicantDocumentValidation(utility, env, applicantTypeDocument);
	}

	@Test
	public void testApplicantDocumentValidationAdultSuccess() throws ApisResourceAccessException, NoSuchFieldException,
			IllegalAccessException, IOException, ParseException, org.json.simple.parser.ParseException, JSONException {
		boolean isApplicantDocumentValidated = applicantDocumentValidation.validateDocument("1234", jsonStringID);
		assertTrue("Test for successful Applicant Document Validation success for adult", isApplicantDocumentValidated);
	}

	@Test
	public void testApplicantDocumentValidationChildSuccess() throws ApisResourceAccessException, NoSuchFieldException,
			IllegalAccessException, IOException, ParseException, org.json.simple.parser.ParseException, JSONException {
		Mockito.when(utility.getApplicantAge(any())).thenReturn(4);
		boolean isApplicantDocumentValidated = applicantDocumentValidation.validateDocument("1234", jsonStringID);
		assertTrue("Test for successful Applicant Document Validation for child", isApplicantDocumentValidated);
	}

	@Test(expected = IdentityNotFoundException.class)
	public void testApplicantDocumentValidationIDJSONNull() throws ApisResourceAccessException, NoSuchFieldException,
			IllegalAccessException, IOException, ParseException, org.json.simple.parser.ParseException, JSONException {

		boolean isApplicantDocumentValidated = applicantDocumentValidation.validateDocument("1234", "{}");

	}

	@Test
	public void testInvalidType() throws ApisResourceAccessException, NoSuchFieldException, IllegalAccessException,
			IOException, ParseException, org.json.simple.parser.ParseException, JSONException {

		InputStream inputStream = new FileInputStream("src/test/resources/ID2.json");
		byte[] bytes = IOUtils.toByteArray(inputStream);
		jsonStringID = new String(bytes);
		boolean isApplicantDocumentValidated = applicantDocumentValidation.validateDocument("1234", jsonStringID);
		assertFalse("Test for successful Applicant Document Validation for child", isApplicantDocumentValidated);

	}
}
