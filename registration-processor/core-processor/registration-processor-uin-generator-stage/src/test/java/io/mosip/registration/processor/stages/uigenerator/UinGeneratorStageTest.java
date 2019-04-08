package io.mosip.registration.processor.stages.uigenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.ApplicantDocument;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.Documents;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.ErrorDTO;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdRequestDto;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.ResponseDTO;
import io.mosip.registration.processor.stages.uingenerator.stage.UinGeneratorStage;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOUtils.class, HMACUtils.class, Utilities.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class UinGeneratorStageTest {

	@InjectMocks
	private UinGeneratorStage uinGeneratorStage = new UinGeneratorStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
	};

	/** The adapter. */
	@Mock
	private FileSystemAdapter adapter;

	/** The input stream. */
	@Mock
	private InputStream inputStream;

	@Mock
	private Object identity;

	/** The registration status service. */
	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The identity json. */
	@Mock
	private JSONObject identityJson;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	@Mock
	private List<Documents> documents;

	@Mock
	private JSONObject demographicIdentity;

	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The registration status dto. */
	private InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	/** The id request DTO. */
	private IdRequestDto idRequestDTO = new IdRequestDto();

	private IdResponseDTO idResponseDTO = new IdResponseDTO();

	@Mock
	private Utilities utility;

	@Mock
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The identitydemoinfo. */
	Identity identitydemoinfo = new Identity();

	/** The Constant CONFIG_SERVER_URL. */
	private static final String CONFIG_SERVER_URL = "url";

	private PacketMetaInfo packetMetaInfo;

	private String identityMappingjsonString;

	@Before
	public void setup() throws Exception {

		Field auditLog = AuditLogRequestBuilder.class.getDeclaredField("registrationProcessorRestService");
		auditLog.setAccessible(true);
		@SuppressWarnings("unchecked")
		RegistrationProcessorRestClientService<Object> mockObj = Mockito
				.mock(RegistrationProcessorRestClientService.class);
		auditLog.set(auditLogRequestBuilder, mockObj);
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_401.toString(), EventName.ADD.toString(),
				EventType.BUSINESS.toString(), "1234testcase", ApiName.AUDIT);

		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID1.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);
		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(idJsonStream);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);

		File identityMappingjson = new File(classLoader.getResource("RegistrationProcessorIdentity.json").getFile());
		InputStream identityMappingjsonStream = new FileInputStream(identityMappingjson);

		try {
			identityMappingjsonString = IOUtils.toString(identityMappingjsonStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", CONFIG_SERVER_URL, "RegistrationProcessorIdentity.json")
				.thenReturn(identityMappingjsonString);
		Mockito.when(utility.getConfigServerFileStorageURL()).thenReturn(CONFIG_SERVER_URL);
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");
		Mockito.when(utility.getGetRegProcessorIdentityJson()).thenReturn("RegistrationProcessorIdentity.json");

		Mockito.when(identityJson.get(anyString())).thenReturn(demographicIdentity);
		List<ApplicantDocument> applicantDocument = new ArrayList<>();
		ApplicantDocument appDocument = new ApplicantDocument();
		appDocument.setIsActive(true);
		appDocument.setDocName("POA");
		appDocument.setDocStore("ProofOfAddress".getBytes());
		applicantDocument.add(appDocument);

		Mockito.when(packetInfoManager.getDocumentsByRegId(Matchers.anyString())).thenReturn(applicantDocument);

	}

	@Test
	public void testUinGenerationSuccessWithoutUIN() throws Exception {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");

		String Str = "{\"uin\":\"6517036426\"}";
		String response = "{\"uin\":\"6517036426\",\"status\":\"ASSIGNED\"}";
		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(Str);
		Mockito.when(registrationProcessorRestClientService.putApi(any(), any(), any(), any(), any(), any())).thenReturn(response);

		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);

		ClassLoader classLoader1 = getClass().getClassLoader();
		File idJsonFile1 = new File(classLoader1.getResource("packet_meta_info.json").getFile());
		InputStream idJsonStream1 = new FileInputStream(idJsonFile1);

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(adapter.getFile("27847657360002520181210094052", PacketFiles.PACKET_META_INFO.name()))
				.thenReturn(idJsonStream1);

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(null);
		idResponseDTO.setId("mosip.id.create");
		idResponseDTO.setResponse(responseDTO);
		idResponseDTO.setStatus("ACTIVATED");
		idResponseDTO.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO.setVersion("1.0");

		Mockito.when(registrationProcessorRestClientService.postApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertFalse(result.getInternalError());

	}

	@Test
	public void testUinGenerationSuccessWithoutUINAndUinUnused() throws Exception {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");

		String Str = "{\"uin\":\"6517036426\"}";
		String response = "{\"timestamp\":1553771083721,\"status\":404,\"errors\":[{\"errorCode\":\"KER-UIG-004\",\"errorMessage\":\"Given UIN is not in ISSUED status\"}]}";

		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(Str);
		Mockito.when(registrationProcessorRestClientService.putApi(any(), any(), any(), any(), any(), any())).thenReturn(response);

		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);

		ClassLoader classLoader1 = getClass().getClassLoader();
		File idJsonFile1 = new File(classLoader1.getResource("packet_meta_info.json").getFile());
		InputStream idJsonStream1 = new FileInputStream(idJsonFile1);

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(adapter.getFile("27847657360002520181210094052", PacketFiles.PACKET_META_INFO.name()))
				.thenReturn(idJsonStream1);

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(null);
		idResponseDTO.setId("mosip.id.create");
		idResponseDTO.setResponse(responseDTO);
		idResponseDTO.setStatus("ACTIVATED");
		idResponseDTO.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO.setVersion("1.0");

		Mockito.when(registrationProcessorRestClientService.postApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertFalse(result.getInternalError());

	}

	@Test
	@Ignore
	public void testUinReActivationifAlreadyActivatedSuccess() throws Exception {

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		messageDTO.setReg_type("ACTIVATED");

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(null);
		idResponseDTO.setId("mosip.id.update");
		idResponseDTO.setResponse(responseDTO);
		idResponseDTO.setStatus("ACTIVATED");
		idResponseDTO.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO.setVersion("1.0");


		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponseDTO);

		String idJsonData="{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream = new ByteArrayInputStream(idJsonData.getBytes(StandardCharsets.UTF_8));

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(registrationProcessorRestClientService.postApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertFalse(result.getIsValid());

	}


	@Test
	public void testUinReActivationIfNotActivatedSuccess() throws Exception {

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		messageDTO.setReg_type("ACTIVATED");

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(null);
		idResponseDTO.setId("mosip.id.update");
		idResponseDTO.setResponse(responseDTO);
		idResponseDTO.setStatus("ANY");
		idResponseDTO.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO.setVersion("1.0");



		IdResponseDTO idResponseDTO1 = new IdResponseDTO();
		ResponseDTO responseDTO1 = new ResponseDTO();
		responseDTO1.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO1.setErrors(null);
		idResponseDTO1.setId("mosip.id.update");
		idResponseDTO1.setResponse(responseDTO1);
		idResponseDTO1.setStatus("ACTIVATED");
		idResponseDTO1.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO1.setVersion("1.0");


		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponseDTO);

		String idJsonData="{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream = new ByteArrayInputStream(idJsonData.getBytes(StandardCharsets.UTF_8));

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(registrationProcessorRestClientService.patchApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO1);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertTrue(result.getIsValid());

	}


	@Test
	@Ignore
	public void testUinReActivationIfNotGotActivatedStaus() throws Exception {

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		messageDTO.setReg_type("ACTIVATED");

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(null);
		idResponseDTO.setId("mosip.id.update");
		idResponseDTO.setResponse(responseDTO);
		idResponseDTO.setStatus("ANY");
		idResponseDTO.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO.setVersion("1.0");



		IdResponseDTO idResponseDTO1 = new IdResponseDTO();
		ResponseDTO responseDTO1 = new ResponseDTO();
		responseDTO1.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO1.setErrors(null);
		idResponseDTO1.setId("mosip.id.update");
		idResponseDTO1.setResponse(responseDTO1);
		idResponseDTO1.setStatus("ANY");
		idResponseDTO1.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO1.setVersion("1.0");


		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponseDTO);

		String idJsonData="{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream = new ByteArrayInputStream(idJsonData.getBytes(StandardCharsets.UTF_8));

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(registrationProcessorRestClientService.patchApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO1);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertFalse(result.getIsValid());

	}

	@Test
	@Ignore
	public void testUinReActivationFailure() throws Exception {

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		messageDTO.setReg_type("ACTIVATED");


		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(null);
		idResponseDTO.setId("mosip.id.update");
		idResponseDTO.setResponse(responseDTO);
		idResponseDTO.setStatus("ANY");
		idResponseDTO.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO.setVersion("1.0");


		IdResponseDTO idResponseDTO1 = new IdResponseDTO();
		List<ErrorDTO> errors=new ArrayList<>();
		ErrorDTO errorDTO= new ErrorDTO("tets","error");
		errors.add(errorDTO);
		idResponseDTO1.setErrors(errors);
		idResponseDTO1.setId("mosip.id.update");
		idResponseDTO1.setResponse(null);
		idResponseDTO1.setStatus("ANY");
		idResponseDTO1.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO1.setVersion("1.0");


		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponseDTO);

		String idJsonData="{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream = new ByteArrayInputStream(idJsonData.getBytes(StandardCharsets.UTF_8));

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(registrationProcessorRestClientService.patchApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO1);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertFalse(result.getIsValid());

	}



	@Test
	public void testUinUpdationFaliure() throws Exception {

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		ErrorDTO errorDto = new ErrorDTO();
		errorDto.setErrorCode("KER-IDR-001");
		errorDto.setErrorMessage("Record already Exists in DB");
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		List<ErrorDTO> errors = new ArrayList<>();
		errors.add(errorDto);
		idResponseDTO.setErrors(errors);
		idResponseDTO.setId("mosip.id.error");
		idResponseDTO.setResponse(null);
		idResponseDTO.setTimestamp("2019-01-17T06:29:01.940Z");
		idResponseDTO.setVersion("1.0");

		// String response = new String(
		// "{\"id\":\"mosip.id.create\",\"version\":\"1.0\",\"timestamp\":\"2019-01-17T06:29:01.940Z\",\"status\":\"ACTIVATED\",\"response\":{\"entity\":\"https://dev.mosip.io/idrepo/v1.0/identity/203560486746\"}}");

		Mockito.when(registrationProcessorRestClientService.postApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO);

		String Str = "{\"uin\":\"6517036426\"}";
		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(Str);

		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);

		ClassLoader classLoader1 = getClass().getClassLoader();
		File idJsonFile1 = new File(classLoader1.getResource("packet_meta_info.json").getFile());
		InputStream idJsonStream1 = new FileInputStream(idJsonFile1);

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(adapter.getFile("27847657360002520181210094052", PacketFiles.PACKET_META_INFO.name()))
				.thenReturn(idJsonStream1);
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertTrue(result.getInternalError());
	}

	@Test
	public void testExceptionInProcessTest() throws Exception {
		ApisResourceAccessException exp = new ApisResourceAccessException(
				HibernateErrorCode.ERR_DATABASE.getErrorCode());

		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);
		ClassLoader classLoader1 = getClass().getClassLoader();
		File idJsonFile1 = new File(classLoader1.getResource("packet_meta_info.json").getFile());
		InputStream idJsonStream1 = new FileInputStream(idJsonFile1);

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(adapter.getFile("27847657360002520181210094052", PacketFiles.PACKET_META_INFO.name()))
				.thenReturn(idJsonStream1);
		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(exp);
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		uinGeneratorStage.process(messageDTO);
	}

	@Test
	public void testApiResourceExceptionInSendIdRepoTest() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);
		ClassLoader classLoader1 = getClass().getClassLoader();
		File idJsonFile1 = new File(classLoader1.getResource("packet_meta_info.json").getFile());
		InputStream idJsonStream1 = new FileInputStream(idJsonFile1);

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(adapter.getFile("27847657360002520181210094052", PacketFiles.PACKET_META_INFO.name()))
				.thenReturn(idJsonStream1);
		ApisResourceAccessException exp = new ApisResourceAccessException(
				HibernateErrorCode.ERR_DATABASE.getErrorCode());
		String Str = "{\"uin\":\"6517036426\"}";
		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenReturn(Str);
		Mockito.when(registrationProcessorRestClientService.postApi(any(), any(), any(), any(), any(), any()))
				.thenThrow(exp);
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		uinGeneratorStage.process(messageDTO);
	}

	@Test
	public void testApiResourceExceptionInUpdateIdRepoTest() throws Exception {
		ApisResourceAccessException exp = new ApisResourceAccessException(
				HibernateErrorCode.ERR_DATABASE.getErrorCode());
		ClassLoader classLoader = getClass().getClassLoader();
		File idJsonFile = new File(classLoader.getResource("ID1.json").getFile());
		InputStream idJsonStream = new FileInputStream(idJsonFile);

		ClassLoader classLoader1 = getClass().getClassLoader();
		File idJsonFile1 = new File(classLoader1.getResource("packet_meta_info.json").getFile());
		InputStream idJsonStream1 = new FileInputStream(idJsonFile1);

		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream);

		Mockito.when(adapter.getFile("27847657360002520181210094052", PacketFiles.PACKET_META_INFO.name()))
				.thenReturn(idJsonStream1);
		Mockito.when(registrationProcessorRestClientService.postApi(any(), any(), any(), any(), any(), any()))
				.thenThrow(exp);
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		uinGeneratorStage.process(messageDTO);
	}

	@Test
	public void deactivateTestSuccess() throws ApisResourceAccessException {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("10031100110005020190313110030");
		messageDTO.setReg_type("DEACTIVATED");

		IdResponseDTO responsedto = new IdResponseDTO();
		responsedto.setStatus("ACTIVATED");

		String idJson = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream1 = new ByteArrayInputStream(idJson.getBytes(StandardCharsets.UTF_8));

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(null);
		idResponseDTO.setId("mosip.id.update");
		idResponseDTO.setStatus("DEACTIVATED");
		idResponseDTO.setResponse(responseDTO);
		idResponseDTO.setTimestamp("2019-03-12T06:49:30.779Z");
		idResponseDTO.setVersion("1.0");

		Mockito.when(adapter.getFile("10031100110005020190313110030",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream1);
		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(responsedto);
		Mockito.when(registrationProcessorRestClientService.patchApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertTrue(result.getIsValid());
	}

	@Test
	@Ignore
	public void checkIsUinDeactivatedSuccess() throws ApisResourceAccessException {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("10031100110005020190313110030");
		messageDTO.setReg_type("DEACTIVATED");

		String idJson = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		InputStream idJsonStream1 = new ByteArrayInputStream(idJson.getBytes(StandardCharsets.UTF_8));

		IdResponseDTO responsedto = new IdResponseDTO();
		responsedto.setResponse(responseDTO);
		responsedto.setStatus("DEACTIVATED");

		Mockito.when(adapter.getFile("10031100110005020190313110030",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream1);
		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(responsedto);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertFalse(result.getIsValid());
	}

	@Test
	@Ignore
	public void deactivateTestForExistingUinTestSuccess() throws ApisResourceAccessException {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("10031100110005020190313110030");
		messageDTO.setReg_type("DEACTIVATED");

		String idJson = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream1 = new ByteArrayInputStream(idJson.getBytes(StandardCharsets.UTF_8));

		ErrorDTO errorDto = new ErrorDTO();
		errorDto.setErrorCode("KER-IDR-001");
		errorDto.setErrorMessage("Record already Exists in DB");

		List<ErrorDTO> errors = new ArrayList<>();
		errors.add(errorDto);

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(errors);
		idResponseDTO.setId("mosip.id.update");
		idResponseDTO.setStatus("DEACTIVATED");
		idResponseDTO.setResponse(null);
		idResponseDTO.setTimestamp("2019-03-12T06:49:30.779Z");
		idResponseDTO.setVersion("1.0");

		Mockito.when(adapter.getFile("10031100110005020190313110030",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream1);
		Mockito.when(registrationProcessorRestClientService.patchApi(any(), any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO);
		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO);

		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertFalse(result.getIsValid());
	}

	@Test
	public void deactivateTestFailure() throws ApisResourceAccessException {

		ApisResourceAccessException exp = new ApisResourceAccessException(
				HibernateErrorCode.ERR_DATABASE.getErrorCode());

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("10031100110005020190313110030");
		messageDTO.setReg_type("DEACTIVATED");

		String idJson = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream1 = new ByteArrayInputStream(idJson.getBytes(StandardCharsets.UTF_8));

		ErrorDTO errorDto = new ErrorDTO();
		errorDto.setErrorCode("KER-IDR-001");
		errorDto.setErrorMessage("Record already Exists in DB");

		List<ErrorDTO> errors = new ArrayList<>();
		errors.add(errorDto);

		IdResponseDTO idResponseDTO = new IdResponseDTO();
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setEntity("https://dev.mosip.io/idrepo/v1.0/identity/203560486746");
		idResponseDTO.setErrors(errors);
		idResponseDTO.setId("mosip.id.update");
		idResponseDTO.setStatus("DEACTIVATED");
		idResponseDTO.setResponse(null);
		idResponseDTO.setTimestamp("2019-03-12T06:49:30.779Z");
		idResponseDTO.setVersion("1.0");

		Mockito.when(adapter.getFile("10031100110005020190313110030",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream1);

		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(idResponseDTO);

		Mockito.when(registrationProcessorRestClientService.patchApi(any(), any(), any(), any(), any(), any())).thenThrow(exp);
		uinGeneratorStage.process(messageDTO);
	}

	@Test
	public void apisResourceAccessExceptionTest() throws ApisResourceAccessException {

		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);
		HttpServerErrorException httpServerErrorException = new HttpServerErrorException(
				HttpStatus.INTERNAL_SERVER_ERROR, "KER-FSE-004:encrypted data is corrupted or not base64 encoded");
		Mockito.when(apisResourceAccessException.getCause()).thenReturn(httpServerErrorException);

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("10031100110005020190313110030");
		messageDTO.setReg_type("DEACTIVATED");

		String idJson = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream1 = new ByteArrayInputStream(idJson.getBytes(StandardCharsets.UTF_8));


		Mockito.when(adapter.getFile("10031100110005020190313110030",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream1);

		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenThrow(apisResourceAccessException);
		uinGeneratorStage.process(messageDTO);
	}

	@Test
	public void clientErrorExceptionTest() throws ApisResourceAccessException {

		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);
		HttpClientErrorException httpErrorErrorException = new HttpClientErrorException(
				HttpStatus.INTERNAL_SERVER_ERROR, "KER-FSE-004:encrypted data is corrupted or not base64 encoded");
		Mockito.when(apisResourceAccessException.getCause()).thenReturn(httpErrorErrorException);

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("10031100110005020190313110030");
		messageDTO.setReg_type("DEACTIVATED");

		String idJson = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream1 = new ByteArrayInputStream(idJson.getBytes(StandardCharsets.UTF_8));


		Mockito.when(adapter.getFile("10031100110005020190313110030",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream1);

		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenThrow(apisResourceAccessException);
		uinGeneratorStage.process(messageDTO);
	}

	@Test
	public void getApiExceptionTest() throws ApisResourceAccessException {

		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("10031100110005020190313110030");
		messageDTO.setReg_type("DEACTIVATED");

		String idJson = "{\"identity\":{\"IDSchemaVersion\":1.0,\"UIN\":4215839851}}";
		InputStream idJsonStream1 = new ByteArrayInputStream(idJson.getBytes(StandardCharsets.UTF_8));


		Mockito.when(adapter.getFile("10031100110005020190313110030",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenReturn(idJsonStream1);

		Mockito.when(registrationProcessorRestClientService.getApi(any(), any(), any(), any(), any())).thenThrow(apisResourceAccessException);
		uinGeneratorStage.process(messageDTO);
	}

	@Test
	public void testFSAdapterException() throws FileNotFoundException, ApisResourceAccessException {
		FSAdapterException fsAdapterException = new FSAdapterException("RPR-1001", "Unable to connect to HDFS");
		Mockito.when(adapter.getFile("27847657360002520181210094052",
				PacketFiles.DEMOGRAPHIC.name() + "\\" + PacketFiles.ID.name())).thenThrow(fsAdapterException);

		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setRid("27847657360002520181210094052");
		MessageDTO result = uinGeneratorStage.process(messageDTO);
		assertTrue(result.getInternalError());
	}

	@Test
	public void testDeployVerticle() {
		uinGeneratorStage.deployVerticle();
	}
}