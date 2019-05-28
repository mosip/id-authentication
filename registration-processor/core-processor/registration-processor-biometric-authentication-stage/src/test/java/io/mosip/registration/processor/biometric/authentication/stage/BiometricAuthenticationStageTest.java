package io.mosip.registration.processor.biometric.authentication.stage;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.masterdata.StatusResponseDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.repositary.RegistrationRepositary;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonUtil.class, IOUtils.class, HMACUtils.class, Utilities.class})
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class BiometricAuthenticationStageTest {
	
	
	/** The input stream. */
	@Mock
	private InputStream inputStream;

	/** The filesystem ceph adapter impl. */
	@Mock
	private FileSystemAdapter filesystemCephAdapterImpl;

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Mock
	InternalRegistrationStatusDto registrationStatusDto;
	
	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;
	
	@Mock
	private IdentityIteratorUtil identityIteratorUtil;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The BiometricAuthenticationStage stage. */
	@InjectMocks
	private BiometricAuthenticationStage biometricAuthenticationStage;

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

	@Mock
	private Environment env;

	/** The packet meta info. */
	private PacketMetaInfo packetMetaInfo;
	/** The identity. */
	Identity identity = new Identity();

	io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity identityDemo = new io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity();

	/** The dto. */
	InternalRegistrationStatusDto statusDto;
	/** The list. */
	List<InternalRegistrationStatusDto> list;

	/** The list appender. */
	private ListAppender<ILoggingEvent> listAppender;

	@Mock
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	@Mock
	JsonValidator jsonValidatorImpl;

	@Mock
	private Utilities utility;

	@Mock
	ObjectMapper mapIdentityJsonStringToObject;

	@Mock
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	@Mock
	private RegistrationRepositary<SyncRegistrationEntity, String> registrationRepositary;

	StatusResponseDto statusResponseDto;

	ValidationReport validationReport;

	private String stageName = "BiometricAuthenticationStage";

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(biometricAuthenticationStage, "ageLimit", "5");
		list = new ArrayList<InternalRegistrationStatusDto>();

		listAppender = new ListAppender<>();

		dto.setRid("2018701130000410092018110735");
		dto.setReg_type("update");
		MockitoAnnotations.initMocks(this);
		packetMetaInfo = new PacketMetaInfo();

		FieldValue registrationType = new FieldValue();
		registrationType.setLabel("registrationType");
		registrationType.setValue("update");



		
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
		Mockito.doReturn(responseWrapper).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_405.toString(), EventName.UPDATE.toString(),
				EventType.BUSINESS.toString(), "1234testcase", ApiName.AUDIT);

		String test = "1234567890";
		byte[] data = "1234567890".getBytes();
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRegistrationId("2018701130000410092018110735");
		registrationStatusDto.setStatusCode("");
		listAppender.start();
		list.add(registrationStatusDto);
		Mockito.when(registrationStatusService.getByStatus(anyString())).thenReturn(list);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(identityIteratorUtil.getFieldValue(any(), any())).thenReturn("UPDATE");
		Mockito.when(filesystemCephAdapterImpl.getFile(any(), any())).thenReturn(inputStream);
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.doNothing().when(HMACUtils.class, "update", data);
		PowerMockito.when(HMACUtils.class, "digestAsPlainText", anyString().getBytes()).thenReturn(test);

		

		
		JSONObject demographicIdentity = new JSONObject();
		PowerMockito.when(JsonUtil.getJSONObject(any(), any())).thenReturn(demographicIdentity);

		statusResponseDto = new StatusResponseDto();
		statusResponseDto.setStatus("VALID");
		Mockito.when(registrationProcessorRestService.getApi(any(), any(), any(), any(), any()))
				.thenReturn(statusResponseDto);

		JSONObject jsonObject = Mockito.mock(JSONObject.class);
		Mockito.when(utility.getUIn(any())).thenReturn(12345678l);
		Mockito.when(utility.retrieveIdrepoJson(any())).thenReturn(jsonObject);
		FieldValue fieldValue = new FieldValue();
		fieldValue.setLabel("registrationType");
		fieldValue.setValue("update");
		List<FieldValue> metadata = new ArrayList<>();
		metadata.add(fieldValue);
		identity.setMetaData(metadata);
		packetMetaInfo.setIdentity(identity);
		Mockito.when(utility.getPacketMetaInfo(any())).thenReturn(packetMetaInfo);
		Mockito.when(utility.getApplicantAge(any())).thenReturn(21);
		Mockito.when(utility.getDemographicIdentityJSONObject(any())).thenReturn(jsonObject);
		PowerMockito.when(JsonUtil.getJSONObject(jsonObject, "individualBiometrics")).thenReturn(jsonObject);
		Mockito.when(jsonObject.get("value")).thenReturn("applicantCBEF");
	}

	
	@Test
	public void biometricAuthenticationSuccessTest(){

		MessageDTO messageDto = biometricAuthenticationStage.process(dto);
		assertTrue(messageDto.getIsValid());
	}
}
