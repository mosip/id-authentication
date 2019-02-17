package io.mosip.registrationprocessor.print.stage.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.spi.uincardgenerator.UinCardGenerator;
import io.mosip.registration.processor.message.sender.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.dto.IdResponseDTO;
import io.mosip.registration.processor.print.dto.ResponseDTO;
import io.mosip.registration.processor.print.stage.PrintStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class PrintStageTest {

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	private IdResponseDTO idResponse = new IdResponseDTO();

	private ResponseDTO response = new ResponseDTO();

	@Mock
	private TemplateGenerator templateGenerator;

	@Mock
	private UinCardGenerator<ByteArrayOutputStream> uinCardGenerator;

	@Mock
	private Utilities utility;

	@Mock
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;

	@Mock
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Mock
	private MosipQueue queue;

	@Mock
	private JSONObject object;

	@InjectMocks
	private PrintStage stage = new PrintStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void consume(MosipEventBus mosipEventBus, MessageBusAddress fromAddress) {
		}

		@Override
		public void setResponse(RoutingContext ctx, Object object) {
		}

		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}
	};

	@Test
	public void testDeployVerticle() {
		stage.deployVerticle();
	}

	@Before
	public void setup() throws Exception {
		System.setProperty("server.port", "8099");

		List<String> uinList = new ArrayList<>();
		uinList.add("4238135072");
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(uinList);
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

		Object identity = identityMap;
		response.setIdentity(identity);
		idResponse.setResponse(response);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponse);

		String artifact = "UIN Card Template";
		InputStream artifactStream = new ByteArrayInputStream(artifact.getBytes());
		Mockito.when(templateGenerator.getTemplate(any(), any(), anyString())).thenReturn(artifactStream);

		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		while ((bytesRead = artifactStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		Mockito.when(uinCardGenerator.generateUinCard(any(), any())).thenReturn(outputStream);

		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");

		String value = "{\r\n" + "	\"identity\": {\r\n" + "		\"name\": {\r\n"
				+ "			\"value\": \"fullName\",\r\n" + "			\"weight\": 20\r\n" + "		},\r\n"
				+ "		\"gender\": {\r\n" + "			\"value\": \"gender\",\r\n" + "			\"weight\": 20\r\n"
				+ "		},\r\n" + "		\"dob\": {\r\n" + "			\"value\": \"dateOfBirth\",\r\n"
				+ "			\"weight\": 20\r\n" + "		},\r\n" + "		\"pheoniticName\": {\r\n"
				+ "			\"weight\": 20\r\n" + "		},\r\n" + "		\"poa\": {\r\n"
				+ "			\"value\" : \"proofOfAddress\"\r\n" + "		},\r\n" + "		\"poi\": {\r\n"
				+ "			\"value\" : \"proofOfIdentity\"\r\n" + "		},\r\n" + "		\"por\": {\r\n"
				+ "			\"value\" : \"proofOfRelationship\"\r\n" + "		},\r\n" + "		\"pob\": {\r\n"
				+ "			\"value\" : \"proofOfDateOfBirth\"\r\n" + "		},\r\n"
				+ "		\"individualBiometrics\": {\r\n" + "			\"value\" : \"individualBiometrics\"\r\n"
				+ "		},\r\n" + "		\"age\": {\r\n" + "			\"value\" : \"age\"\r\n" + "		},\r\n"
				+ "		\"addressLine1\": {\r\n" + "			\"value\" : \"addressLine1\"\r\n" + "		},\r\n"
				+ "		\"addressLine2\": {\r\n" + "			\"value\" : \"addressLine2\"\r\n" + "		},\r\n"
				+ "		\"addressLine3\": {\r\n" + "			\"value\" : \"addressLine3\"\r\n" + "		},\r\n"
				+ "		\"region\": {\r\n" + "			\"value\" : \"region\"\r\n" + "		},\r\n"
				+ "		\"province\": {\r\n" + "			\"value\" : \"province\"\r\n" + "		},\r\n"
				+ "		\"postalCode\": {\r\n" + "			\"value\" : \"postalCode\"\r\n" + "		},\r\n"
				+ "		\"phone\": {\r\n" + "			\"value\" : \"phone\"\r\n" + "		},\r\n"
				+ "		\"email\": {\r\n" + "			\"value\" : \"email\"\r\n" + "		},\r\n"
				+ "		\"localAdministrativeAuthority\": {\r\n"
				+ "			\"value\" : \"localAdministrativeAuthority\"\r\n" + "		},\r\n"
				+ "		\"idschemaversion\": {\r\n" + "			\"value\" : \"IDSchemaVersion\"\r\n" + "		},\r\n"
				+ "		\"cnienumber\": {\r\n" + "			\"value\" : \"CNIENumber\"\r\n" + "		},\r\n"
				+ "		\"city\": {\r\n" + "			\"value\" : \"city\"\r\n" + "		}\r\n" + "	}\r\n" + "} ";

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", anyString(), anyString()).thenReturn(value);

		Mockito.when(mosipConnectionFactory.createConnection(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(queue);
		Mockito.when(mosipQueueManager.send(any(), any(), anyString())).thenReturn(true);
	}

	@Test
	public void testPrintStageSuccess() {
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getIsValid());
	}

	@Test
	public void testPrintStageFailure() {
		Mockito.when(mosipQueueManager.send(any(), any(), anyString())).thenReturn(false);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertFalse(result.getIsValid());
	}

	@Test
	public void testUINNotFound() {
		List<String> uinList = new ArrayList<>();
		uinList.add(null);
		Mockito.when(packetInfoManager.getUINByRid(anyString())).thenReturn(uinList);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	@Test
	public void testQueueConnectionNull() {
		Mockito.when(mosipConnectionFactory.createConnection(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(null);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}
	
	@Test
	public void testTemplateProcessingFailure() throws ApisResourceAccessException, IOException {
		TemplateProcessingFailureException e = new TemplateProcessingFailureException();
		Mockito.doThrow(e).when(templateGenerator).getTemplate(any(), any(), anyString());
		
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}
	
	@Test
	public void testPDFGeneratorException() {
		PDFGeneratorException e = new PDFGeneratorException(null, null);
		Mockito.doThrow(e).when(uinCardGenerator).generateUinCard(any(), any());
		
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

}
