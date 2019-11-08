package io.mosip.registration.processor.print.service.Impl.test;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.registration.processor.core.constant.CardType;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.core.idrepo.dto.Documents;
import io.mosip.registration.processor.core.idrepo.dto.ErrorDTO;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO1;
import io.mosip.registration.processor.core.idrepo.dto.ResponseDTO;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.vid.VidResDTO;
import io.mosip.registration.processor.core.packet.dto.vid.VidResponseDTO;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.spi.uincardgenerator.UinCardGenerator;
import io.mosip.registration.processor.message.sender.template.TemplateGenerator;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.exception.VidCreationException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.service.impl.PrintServiceImpl;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, CryptoUtil.class, FileUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class PrintServiceImplTest {

	/** The rest client service. */
	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The id response. */
	private IdResponseDTO1 idResponse = new IdResponseDTO1();

	/** The response. */
	private ResponseDTO response = new ResponseDTO();

	/** The template generator. */
	@Mock
	private TemplateGenerator templateGenerator;

	/** The uin card generator. */
	@Mock
	private UinCardGenerator<ByteArrayOutputStream> uinCardGenerator;

	/** The utility. */
	@Mock
	private Utilities utility;

	@Mock
	private QrCodeGenerator<QrVersion> qrCodeGenerator;

	@Mock
	private CbeffUtil cbeffutil;

	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	/** The stage. */
	@InjectMocks
	private PrintService<Map<String, byte[]>> printService = new PrintServiceImpl();

	@Mock
	private Environment env;

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception {
		when(env.getProperty("mosip.reigstration.processor.print.service.uincard.password")).thenReturn("postalCode");
		when(env.getProperty("mosip.registration.processor.datetime.pattern"))
				.thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		ReflectionTestUtils.setField(printService, "primaryLang", "eng");
		ReflectionTestUtils.setField(printService, "secondaryLang", "ara");
		ReflectionTestUtils.setField(printService, "unMaskedLength", 4);
		ReflectionTestUtils.setField(printService, "uinLength", 10);

		Map<String, String> map1 = new HashMap<>();
		map1.put("UIN", "4238135072");
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);

		LinkedHashMap<String, Object> identityMap = new LinkedHashMap<>();
		Map<String, String> map = new HashMap<>();
		map.put("language", "eng");
		map.put("value", "Alok");
		JSONObject j1 = new JSONObject(map);

		Map<String, String> map2 = new HashMap<>();
		map2.put("language", "ara");
		map2.put("value", "Alok");
		JSONObject j2 = new JSONObject(map2);
		JSONArray array = new JSONArray();
		array.add(j1);
		array.add(j2);
		identityMap.put("fullName", array);
		identityMap.put("gender", array);
		identityMap.put("addressLine1", array);
		identityMap.put("addressLine2", array);
		identityMap.put("addressLine3", array);
		identityMap.put("city", array);
		identityMap.put("province", array);
		identityMap.put("region", array);
		identityMap.put("dateOfBirth", "1980/11/14");
		identityMap.put("phone", "9967878787");
		identityMap.put("email", "raghavdce@gmail.com");
		identityMap.put("postalCode", "900900");
		identityMap.put("proofOfAddress", j2);

		Object identity = identityMap;
		response.setIdentity(identity);

		Documents doc1 = new Documents();
		doc1.setCategory("individualBiometrics");
		doc1.setValue("individual biometric value");
		List<Documents> docList = new ArrayList<>();
		docList.add(doc1);

		byte[] bioBytes = "individual biometric value".getBytes();
		PowerMockito.mockStatic(CryptoUtil.class);
		PowerMockito.when(CryptoUtil.class, "decodeBase64", anyString()).thenReturn(bioBytes);

		List<SingleType> singleList = new ArrayList<>();
		singleList.add(SingleType.FACE);
		BIRType type = new BIRType();
		type.setBDB(bioBytes);
		BDBInfoType bdbinfotype = new BDBInfoType();
		bdbinfotype.setType(singleList);
		type.setBDBInfo(bdbinfotype);
		List<BIRType> birtypeList = new ArrayList<>();
		birtypeList.add(type);
		Mockito.when(cbeffutil.getBIRDataFromXML(any())).thenReturn(birtypeList);

		response.setDocuments(docList);
		idResponse.setResponse(response);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponse);

		String artifact = "UIN Card Template";
		InputStream artifactStream = new ByteArrayInputStream(artifact.getBytes());
		Mockito.when(templateGenerator.getTemplate(any(), any(), anyString())).thenReturn(artifactStream);

		byte[] buffer = new byte[8192];
		int bytesRead;

		while ((bytesRead = artifactStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		Mockito.when(uinCardGenerator.generateUinCard(any(), any(), any())).thenReturn(outputStream);

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");
		PowerMockito.mockStatic(Utilities.class);
		byte[] qrcode = "QRCODE GENERATED".getBytes();
		Mockito.when(qrCodeGenerator.generateQrCode(any(), any())).thenReturn(qrcode);
		ClassLoader classLoader = getClass().getClassLoader();
		File printTextFile = new File(classLoader.getResource("printTextFileJson.json").getFile());
		File mappingFile = new File(classLoader.getResource("RegistrationProcessorIdentity.json").getFile());
		String mappingFileJson = FileUtils.readFileToString(mappingFile, StandardCharsets.UTF_8);
		String printTextFileJson = FileUtils.readFileToString(printTextFile, StandardCharsets.UTF_8);
		//PowerMockito.when(Utilities.class, "getJson", "", any()).thenReturn(value);
		PowerMockito.when(utility.getConfigServerFileStorageURL()).thenReturn("configUrl");
		PowerMockito.when(utility.getGetRegProcessorIdentityJson()).thenReturn("mappingJson");
		PowerMockito.when(utility.getRegistrationProcessorPrintTextFile()).thenReturn("printFile");

		PowerMockito.when(Utilities.class, "getJson", utility.getConfigServerFileStorageURL(),
				utility.getRegistrationProcessorPrintTextFile()).thenReturn(printTextFileJson);
		PowerMockito.when(Utilities.class, "getJson", utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson()).thenReturn(mappingFileJson);

		
	}

	@Test
	public void testPdfGeneratedwithUINSuccess() {
		String uin = "2046958192";
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getDocuments(IdType.UIN, uin, CardType.UIN.toString(), false).get("uinPdf");
		assertArrayEquals(expected, result);
	}

	@Test
	public void testPdfGeneratedwithRIDSuccess() throws IdRepoAppException, ApisResourceAccessException, IOException {
		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Map<String, Long> map1 = new HashMap<>();
		map1.put("UIN", 2046958192L);
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getDocuments(IdType.RID, uinList.get(0), CardType.UIN.toString(), false)
				.get("uinPdf");
		assertArrayEquals(expected, result);
	}

	/**
	 * Test UIN not found.
	 * 
	 * @throws ApisResourceAccessException
	 * @throws IdRepoAppException
	 * @throws IOException
	 */
	@Test(expected = PDFGeneratorException.class)
	public void testUINNotFound() throws IdRepoAppException, ApisResourceAccessException, IOException {
		List<String> uinList = new ArrayList<>();
		uinList.add(null);
		Map<String, String> map1 = new HashMap<>();
		map1.put("UIN", null);
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);
		printService.getDocuments(IdType.RID, "2046958192", CardType.UIN.toString(), false);
	}

	/**
	 * Test template processing failure.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test(expected = TemplateProcessingFailureException.class)
	public void testTemplateProcessingFailure() throws ApisResourceAccessException, IOException {
		TemplateProcessingFailureException e = new TemplateProcessingFailureException();
		Mockito.doThrow(e).when(templateGenerator).getTemplate(any(), any(), anyString());

		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Map<String, String> map1 = new HashMap<>();
		map1.put("UIN", "2046958192");
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);
		printService.getDocuments(IdType.UIN, uinList.get(0), CardType.UIN.toString(), false);
	}

	/**
	 * Test PDF generator exception.
	 * 
	 * @throws ApisResourceAccessException
	 * @throws IdRepoAppException
	 * @throws IOException
	 */
	@Test(expected = PDFGeneratorException.class)
	public void testPDFGeneratorException() throws IdRepoAppException, ApisResourceAccessException, IOException {
		PDFGeneratorException e = new PDFGeneratorException(null, null);
		Mockito.doThrow(e).when(uinCardGenerator).generateUinCard(any(), any(), any());

		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Map<String, String> map1 = new HashMap<>();
		map1.put("UIN", "2046958192");
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);
		printService.getDocuments(IdType.UIN, uinList.get(0), CardType.UIN.toString(), false);
	}

	/**
	 * Test api resource exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 * @throws IdRepoAppException
	 */
	@Test(expected = PDFGeneratorException.class)
	public void testApiResourceException() throws ApisResourceAccessException, IdRepoAppException, IOException {
		ApisResourceAccessException e = new ApisResourceAccessException();
		Mockito.doThrow(e).when(restClientService).getApi(any(), any(), any(), any(), any());

		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Map<String, String> map1 = new HashMap<>();
		map1.put("UIN", "2046958192");
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);
		printService.getDocuments(IdType.UIN, uinList.get(0), CardType.UIN.toString(), false);
	}

	@Test(expected = PDFGeneratorException.class)
	public void testQRCodeGenerationException()
			throws QrcodeGenerationException, IOException, IdRepoAppException, ApisResourceAccessException {
		QrcodeGenerationException e = new QrcodeGenerationException(null, null, null);
		Mockito.doThrow(e).when(qrCodeGenerator).generateQrCode(any(), any());

		List<String> uinList = new ArrayList<>();
		uinList.add("2046958192");
		Map<String, String> map1 = new HashMap<>();
		map1.put("UIN", "2046958192");
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);
		printService.getDocuments(IdType.UIN, uinList.get(0), CardType.UIN.toString(), false);

	}

	@Test
	public void testPhotoNotSet() throws Exception {
		Documents doc1 = new Documents();
		doc1.setCategory("individualBiometrics");
		doc1.setValue("individual biometric value");
		List<Documents> docList = new ArrayList<>();
		docList.add(doc1);

		byte[] bioBytes = "individual biometric value".getBytes();
		PowerMockito.mockStatic(CryptoUtil.class);
		PowerMockito.when(CryptoUtil.class, "decodeBase64", anyString()).thenReturn(bioBytes);

		List<SingleType> singleList = new ArrayList<>();
		singleList.add(SingleType.FINGER);
		BIRType type = new BIRType();
		type.setBDB(bioBytes);
		BDBInfoType bdbinfotype = new BDBInfoType();
		bdbinfotype.setType(singleList);
		type.setBDBInfo(bdbinfotype);
		List<BIRType> birtypeList = new ArrayList<>();
		birtypeList.add(type);
		Mockito.when(cbeffutil.getBIRDataFromXML(any())).thenReturn(birtypeList);

		String uin = "2046958192";
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getDocuments(IdType.UIN, uin, CardType.UIN.toString(), false).get("uinPdf");
		assertArrayEquals(expected, result);
	}

	@Test(expected = PDFGeneratorException.class)
	public void testException() throws QrcodeGenerationException, IOException {
		NullPointerException e = new NullPointerException();
		Mockito.doThrow(e).when(qrCodeGenerator).generateQrCode(any(), any());

		String uin = "2046958192";
		printService.getDocuments(IdType.UIN, uin, CardType.UIN.toString(), false).get("uinPdf");
	}

	@Test
	public void testQrCodeNotSet() throws Exception {
		Mockito.when(qrCodeGenerator.generateQrCode(any(), any())).thenReturn(null);

		String uin = "2046958192";
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getDocuments(IdType.UIN, uin, CardType.UIN.toString(), false).get("uinPdf");
		assertArrayEquals(expected, result);
	}

	@Test(expected = PDFGeneratorException.class)
	public void testIdResponseNull() throws ApisResourceAccessException {
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(null);

		String uin = "2046958192";
		printService.getDocuments(IdType.UIN, uin, CardType.UIN.toString(), false).get("uinPdf");
	}

	@Test(expected = TemplateProcessingFailureException.class)
	public void testTemplateFailure() throws ApisResourceAccessException, IOException {
		Mockito.when(templateGenerator.getTemplate(any(), any(), anyString())).thenReturn(null);

		String uin = "2046958192";
		printService.getDocuments(IdType.UIN, uin, CardType.UIN.toString(), false).get("uinPdf");
	}

	@Test
	public void testPdfGeneratedwithVIDSuccess()
			throws IdRepoAppException, ApisResourceAccessException, IOException, VidCreationException {
		List<Long> uinList = new ArrayList<>();
		uinList.add(2046958192L);
		Map<String, Long> map1 = new HashMap<>();
		map1.put("UIN", 2046958192L);
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);
		Mockito.when(utility.getUinByVid(any())).thenReturn("2046958192");
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getDocuments(IdType.VID, "2046958192", CardType.UIN.toString(), false)
				.get("uinPdf");
		assertArrayEquals(expected, result);
	}

	@Test
	public void testPdfGeneratedwithVIDWithPasswordSuccess()
			throws IdRepoAppException, ApisResourceAccessException, IOException, VidCreationException {
		List<Long> uinList = new ArrayList<>();
		uinList.add(2046958192L);
		Map<String, Long> map1 = new HashMap<>();
		map1.put("UIN", 2046958192L);
		JSONObject jsonObject = new JSONObject(map1);
		Mockito.when(utility.retrieveUIN(any())).thenReturn(jsonObject);
		Mockito.when(utility.getUinByVid(any())).thenReturn("2046958192");
		JSONArray arr = new JSONArray();
		arr.add("name");

		JSONObject obj = new JSONObject();
		obj.put("fullName", arr);
		obj.put("postalCode", "12345");
		Mockito.when(utility.retrieveIdrepoJson(Long.parseLong("2046958192"))).thenReturn(obj);

		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getDocuments(IdType.VID, "2046958192", CardType.MASKED_UIN.toString(), true)
				.get("uinPdf");
		assertArrayEquals(expected, result);
	}

	@Test
	public void testPdfGeneratedwithUINWithMasked_Uin_Card_Success() throws ApisResourceAccessException {
		VidResponseDTO vidResponse = new VidResponseDTO();
		VidResDTO vidresdto = new VidResDTO();
		vidresdto.setVid("2046958192");
		vidResponse.setResponse(vidresdto);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any())).thenReturn(vidResponse);
		String uin = "2046958192";
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getDocuments(IdType.UIN, uin, CardType.MASKED_UIN.toString(), false).get("uinPdf");
		assertArrayEquals(expected, result);
	}

	/**
	 * Test template processing failure.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test(expected = PDFGeneratorException.class)
	public void testVidCreationException() throws ApisResourceAccessException, IOException {
		VidResponseDTO vidResponse = new VidResponseDTO();
		List<ErrorDTO> errors = new ArrayList<ErrorDTO>();

		ErrorDTO error = new ErrorDTO();
		errors.add(error);
		vidResponse.setErrors(errors);
		VidResDTO vidresdto = new VidResDTO();
		vidresdto.setVid("2046958192");
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any())).thenReturn(vidResponse);
		String uin = "2046958192";
		byte[] expected = outputStream.toByteArray();
		byte[] result = printService.getDocuments(IdType.UIN, uin, CardType.MASKED_UIN.toString(), false).get("uinPdf");
		assertArrayEquals(expected, result);
	}
}
