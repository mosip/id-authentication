package io.mosip.registrationprocessor.print.stage.test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.spi.uincardgenerator.UinCardGenerator;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.message.sender.utility.MessageSenderUtil;
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
	
	@InjectMocks
	private PrintStage stage = new PrintStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
		
		@Override
		public void setResponse(RoutingContext ctx, Object object) {
			String registrationStatusCode = object.toString();
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
		identityMap.put("gender", "[{language=eng, value=Male},{language=ara, value=Male}]");
		identityMap.put("city", "[{language=eng, value=Maski},{language=ara, value=Maski}]");
		identityMap.put("postalCode", "900900");
		identityMap.put("fullName", "[{language=eng, value=Maski},{language=ara, value=Maski}]");
		identityMap.put("dateOfBirth", "1980/11/14");
		identityMap.put("phone", "996787887");
		identityMap.put("email", "alokabc@gmail.com");
		identityMap.put("addressLine1", "[{language=eng, value=Maski},{language=ara, value=Maski}]");
		identityMap.put("addressLine2", "[{language=eng, value=Maski},{language=ara, value=Maski}]");
		identityMap.put("addressLine3", "[{language=eng, value=Maski},{language=ara, value=Maski}]");
		identityMap.put("province", "[{language=eng, value=Maski},{language=ara, value=Maski}]");
		identityMap.put("region", "[{language=eng, value=Maski},{language=ara, value=Maski}]");
		
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
	    
	    while((bytesRead = artifactStream.read(buffer)) != -1){
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
		
	}
	
	@Test
	public void testPrintStageSuccess() {
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");
		MessageDTO result = stage.process(dto);
		assertTrue(result.getIsValid());
	}


}
