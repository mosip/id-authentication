package io.mosip.registrationprocessor.print.stage.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
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
import io.mosip.registration.processor.print.PrintStageApplication;
import io.mosip.registration.processor.print.dto.IdResponseDTO;
import io.mosip.registration.processor.print.dto.ResponseDTO;
import io.mosip.registration.processor.print.stage.PrintStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Locale;
import io.vertx.ext.web.ParsedHeaderValues;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

/**
 * The Class PrintStageTest.
 * 
 * @author M1048358 Alok
 */
@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class PrintStageTest {

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
	private IdResponseDTO idResponse = new IdResponseDTO();

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

	/** The mosip connection factory. */
	@Mock
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;

	/** The mosip queue manager. */
	@Mock
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	/** The queue. */
	@Mock
	private MosipQueue queue;
	
	@Mock
	private InternalRegistrationStatusDto registrationStatusDto;
	
	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	
	/** The ctx. */
	private RoutingContext ctx;

	/** The response object. */
	private Boolean responseObject;

	/** The stage. */
	@InjectMocks
	private PrintStage stage = new PrintStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String clusterManagerUrl) {
			return null;
		}

		@Override
		public void consume(MosipEventBus mosipEventBus, MessageBusAddress fromAddress) {
		}

		@Override
		public void setResponse(RoutingContext ctx, Object object) {
			responseObject = Boolean.TRUE;
		}

		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}
	};

	/**
	 * Setup.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception {
		System.setProperty("server.port", "8099");
		System.setProperty("primary.language", "eng");
		System.setProperty("registration.processor.queue.username", "admin");
		System.setProperty("registration.processor.queue.password", "admin");
		System.setProperty("registration.processor.queue.url", "tcp://localhost:61616");
		System.setProperty("registration.processor.queue.typeOfQueue", "ACTIVEMQ");
		System.setProperty("registration.processor.queue.address", "test");

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
		Mockito.doNothing().when(registrationStatusDto).setStatusCode(any());
		Mockito.doNothing().when(registrationStatusDto).setStatusComment(any());
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any());

	}

	/**
	 * Test all.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testAll() throws ApisResourceAccessException, IOException {
		ctx = setContext();
		PrintStageApplication.main(null);
		testDeployVerticle();
		testSendMessage();
		testResendPrintPdf();
		//testRoutes();
	}

	/**
	 * Test deploy verticle.
	 */
	public void testDeployVerticle() {
		stage.deployVerticle();
	}

	/**
	 * Test print stage success.
	 */
	@Test
	public void testPrintStageSuccess() {
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getIsValid());
	}

	/**
	 * Test print stage failure.
	 */
	@Test
	public void testPrintStageFailure() {
		Mockito.when(mosipQueueManager.send(any(), any(), anyString())).thenReturn(false);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertFalse(result.getIsValid());
	}

	/**
	 * Test UIN not found.
	 */
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

	/**
	 * Test queue connection null.
	 */
	@Test
	public void testQueueConnectionNull() {
		Mockito.when(mosipConnectionFactory.createConnection(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(null);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	/**
	 * Test template processing failure.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testTemplateProcessingFailure() throws ApisResourceAccessException, IOException {
		TemplateProcessingFailureException e = new TemplateProcessingFailureException();
		Mockito.doThrow(e).when(templateGenerator).getTemplate(any(), any(), anyString());

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	/**
	 * Test PDF generator exception.
	 */
	@Test
	public void testPDFGeneratorException() {
		PDFGeneratorException e = new PDFGeneratorException(null, null);
		Mockito.doThrow(e).when(uinCardGenerator).generateUinCard(any(), any());

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	/**
	 * Test exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	@Test
	public void testException() throws ApisResourceAccessException {
		LinkedHashMap<String, Object> identityMap = new LinkedHashMap<>();
		Object identity = identityMap;
		response.setIdentity(identity);
		idResponse.setResponse(response);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(idResponse);
		identityMap.put("fullName", "fullName=[{language=eng, value=RaviKant},{language=ara, value=RaviKant}]");

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	/**
	 * Test api resource exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 */
	@Test
	public void testApiResourceException() throws ApisResourceAccessException {
		ApisResourceAccessException e = new ApisResourceAccessException();
		Mockito.doThrow(e).when(restClientService).getApi(any(), any(), any(), any(), any());

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	/**
	 * Test send message.
	 */
	public void testSendMessage() {
		stage.sendMessage(null);
	}

	/**
	 * Test resend print pdf.
	 */
	public void testResendPrintPdf() {
		stage.reSendPrintPdf(ctx);
		assertTrue(responseObject);
	}

	/**
	 * Test routes.
	 *
	 * @throws ClientProtocolException
	 *             the client protocol exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*public void testRoutes() throws ClientProtocolException, IOException {
		HttpGet health = new HttpGet("http://localhost:8099/print-stage/health");
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse getResponse = client.execute(health);
		assertEquals(200, getResponse.getStatusLine().getStatusCode());

		HttpPost resend = getHttpPost("http://localhost:8099/v0.1/registration-processor/print-stage/resend");
		CloseableHttpResponse response = HttpClients.createDefault().execute(resend);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}*/

	/**
	 * Gets the http post.
	 *
	 * @param url
	 *            the url
	 * @return the http post
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	private HttpPost getHttpPost(String url) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);
		String json = "{'regId':'51130282650000320190117144316'}";
		StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);
		httpPost.setHeader("Content-type", "application/json");
		return httpPost;
	}

	/**
	 * Sets the context.
	 *
	 * @return the routing context
	 */
	private RoutingContext setContext() {
		return new RoutingContext() {

			@Override
			public Set<FileUpload> fileUploads() {
				return null;
			}

			@Override
			public Vertx vertx() {
				return null;
			}

			@Override
			public User user() {
				return null;
			}

			@Override
			public int statusCode() {
				return 0;
			}

			@Override
			public void setUser(User user) {
			}

			@Override
			public void setSession(Session session) {
			}

			@Override
			public void setBody(Buffer body) {
			}

			@Override
			public void setAcceptableContentType(String contentType) {
			}

			@Override
			public Session session() {
				return null;
			}

			@Override
			public HttpServerResponse response() {
				return null;
			}

			@Override
			public void reroute(HttpMethod method, String path) {
			}

			@Override
			public HttpServerRequest request() {
				return null;
			}

			@Override
			public boolean removeHeadersEndHandler(int handlerID) {
				return false;
			}

			@Override
			public Cookie removeCookie(String name, boolean invalidate) {
				return null;
			}

			@Override
			public boolean removeBodyEndHandler(int handlerID) {
				return false;
			}

			@Override
			public <T> T remove(String key) {
				return null;
			}

			@Override
			public MultiMap queryParams() {
				return null;
			}

			@Override
			public List<String> queryParam(String query) {
				return null;
			}

			@Override
			public RoutingContext put(String key, Object obj) {
				return null;
			}

			@Override
			public Map<String, String> pathParams() {
				return null;
			}

			@Override
			public String pathParam(String name) {
				return null;
			}

			@Override
			public ParsedHeaderValues parsedHeaders() {
				return null;
			}

			@Override
			public String normalisedPath() {
				return null;
			}

			@Override
			public void next() {
			}

			@Override
			public String mountPoint() {
				return null;
			}

			@Override
			public Cookie getCookie(String name) {
				return null;
			}

			@Override
			public String getBodyAsString(String encoding) {
				return null;
			}

			@Override
			public String getBodyAsString() {
				return null;
			}

			@Override
			public JsonArray getBodyAsJsonArray() {
				return null;
			}

			@Override
			public JsonObject getBodyAsJson() {
				JsonObject obj = new JsonObject();
				obj.put("regId", "51130282650000320190117144316");
				return obj;
			}

			@Override
			public Buffer getBody() {
				return null;
			}

			@Override
			public String getAcceptableContentType() {
				return null;
			}

			@Override
			public <T> T get(String key) {
				return null;
			}

			@Override
			public Throwable failure() {
				return null;
			}

			@Override
			public boolean failed() {
				return false;
			}

			@Override
			public void fail(Throwable throwable) {
			}

			@Override
			public void fail(int statusCode) {
			}

			@Override
			public Map<String, Object> data() {
				return null;
			}

			@Override
			public Route currentRoute() {
				return null;
			}

			@Override
			public Set<Cookie> cookies() {
				return null;
			}

			@Override
			public int cookieCount() {
				return 0;
			}

			@Override
			public void clearUser() {
			}

			@Override
			public int addHeadersEndHandler(Handler<Void> handler) {
				return 0;
			}

			@Override
			public RoutingContext addCookie(Cookie cookie) {
				return null;
			}

			@Override
			public int addBodyEndHandler(Handler<Void> handler) {
				return 0;
			}

			@Override
			public List<Locale> acceptableLocales() {
				return null;
			}
		};

	}

}
