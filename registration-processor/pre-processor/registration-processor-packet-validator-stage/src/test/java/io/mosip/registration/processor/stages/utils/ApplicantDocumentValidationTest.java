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
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.packet.dto.applicantcategory.ApplicantTypeDocument;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

/**
 * The Class ApplicantDocumentValidationTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
public class ApplicantDocumentValidationTest {

	/** The utility. */
	@Mock
	Utilities utility;

	/** The map identity json string to object. */
	@Mock
	ObjectMapper mapIdentityJsonStringToObject;

	/** The env. */
	@Mock
	private Environment env;

	/** The applicant type document. */
	@Mock
	ApplicantTypeDocument applicantTypeDocument;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The json string ID. */
	String jsonStringID = null;

	/** The json string id mapping. */
	String jsonStringIdMapping = null;

	/** The json string app. */
	String jsonStringApp = null;

	/** The Constant AGE_THRESHOLD. */
	private static final String AGE_THRESHOLD = "mosip.kernel.applicant.type.age.limit";

	/** The applicant document validation. */
	ApplicantDocumentValidation applicantDocumentValidation;

	/**
	 * Sets the up.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	@Before
	public void setUp() throws IOException, ApisResourceAccessException, ParseException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

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

	/**
	 * Test applicant document validation adult success.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws JSONException
	 *             the JSON exception
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	@Test
	public void testApplicantDocumentValidationAdultSuccess() throws ApisResourceAccessException, NoSuchFieldException,
			IllegalAccessException, IOException, ParseException, org.json.simple.parser.ParseException, JSONException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		boolean isApplicantDocumentValidated = applicantDocumentValidation.validateDocument("1234", jsonStringID);
		assertTrue("Test for successful Applicant Document Validation success for adult", isApplicantDocumentValidated);
	}

	/**
	 * Test applicant document validation child success.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws JSONException
	 *             the JSON exception
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	@Test
	public void testApplicantDocumentValidationChildSuccess() throws ApisResourceAccessException, NoSuchFieldException,
			IllegalAccessException, IOException, ParseException, org.json.simple.parser.ParseException, JSONException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {
		Mockito.when(utility.getApplicantAge(any())).thenReturn(4);
		boolean isApplicantDocumentValidated = applicantDocumentValidation.validateDocument("1234", jsonStringID);
		assertTrue("Test for successful Applicant Document Validation for child", isApplicantDocumentValidated);
	}

	/**
	 * Test applicant document validation IDJSON null.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws JSONException
	 *             the JSON exception
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	@Test(expected = IdentityNotFoundException.class)
	public void testApplicantDocumentValidationIDJSONNull() throws ApisResourceAccessException, NoSuchFieldException,
			IllegalAccessException, IOException, ParseException, org.json.simple.parser.ParseException, JSONException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

		boolean isApplicantDocumentValidated = applicantDocumentValidation.validateDocument("1234", "{}");

	}

	/**
	 * Test invalid type.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws NoSuchFieldException
	 *             the no such field exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws JSONException
	 *             the JSON exception
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 */
	@Test
	public void testInvalidType() throws ApisResourceAccessException, NoSuchFieldException, IllegalAccessException,
			IOException, ParseException, org.json.simple.parser.ParseException, JSONException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

		InputStream inputStream = new FileInputStream("src/test/resources/ID2.json");
		byte[] bytes = IOUtils.toByteArray(inputStream);
		jsonStringID = new String(bytes);
		boolean isApplicantDocumentValidated = applicantDocumentValidation.validateDocument("1234", jsonStringID);
		assertFalse("Test for successful Applicant Document Validation for child", isApplicantDocumentValidated);

	}
}
