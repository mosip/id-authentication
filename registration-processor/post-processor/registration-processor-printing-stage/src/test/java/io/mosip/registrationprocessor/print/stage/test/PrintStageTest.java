package io.mosip.registrationprocessor.print.stage.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.EventId;
import io.mosip.registration.processor.core.constant.EventName;
import io.mosip.registration.processor.core.constant.EventType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.TemplateProcessingFailureException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.queue.impl.exception.ConnectionUnavailableException;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.print.service.PrintService;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.PrintStageApplication;
import io.mosip.registration.processor.print.exception.QueueConnectionNotFound;
import io.mosip.registration.processor.print.service.impl.PrintPostServiceImpl;
import io.mosip.registration.processor.print.stage.PrintStage;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
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

@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class})
@PowerMockIgnore({ "javax.management.*", "javax.net.*" })
@PropertySource("classpath:bootstrap.properties")
public class PrintStageTest {

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Mock
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;

	@Mock
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Mock
	private MosipQueue queue;

	@Mock
	private InternalRegistrationStatusDto registrationStatusDto;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	private RoutingContext ctx;

	private Boolean responseObject;

	@Mock
	private PrintPostServiceImpl printPostService;
	
	@Mock
	private PrintService<Map<String, byte[]>> printService;

	@Mock
	public FileSystemAdapter filesystemAdapter;
	
	@Mock
	private UinValidator<String> uinValidatorImpl;

	@InjectMocks
	private PrintStage stage = new PrintStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
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

	@Before
	public void setup() throws Exception {
		System.setProperty("server.port", "8099");
		System.setProperty("registration.processor.queue.username", "admin");
		System.setProperty("registration.processor.queue.password", "admin");
		System.setProperty("registration.processor.queue.url", "tcp://104.211.200.46:61616");
		System.setProperty("registration.processor.queue.typeOfQueue", "ACTIVEMQ");
		System.setProperty("registration.processor.queue.address", "test");
		System.setProperty("mosip.kernel.xsdstorage-uri", "http://104.211.212.28:51000");
		System.setProperty("mosip.kernel.xsdfile", "mosip-cbeff.xsd");

		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);

		byte[] pdfbytes = "UIN Card Template pdf".getBytes();
		byte[] textBytes = "Text File ".getBytes();
		Map<String, byte[]> byteMap = new HashMap<>();
		byteMap.put("uinPdf", pdfbytes);
		byteMap.put("textFile", textBytes);
		Mockito.when(printService.getDocuments(any(), anyString())).thenReturn(byteMap);

		Mockito.when(mosipConnectionFactory.createConnection(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(queue);
		Mockito.when(mosipQueueManager.send(any(), any(), anyString())).thenReturn(true);

		Mockito.doNothing().when(registrationStatusDto).setStatusCode(any());
		Mockito.doNothing().when(registrationStatusDto).setStatusComment(any());
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any());
		
		QueueListener listener = new QueueListener() {
			@Override
			public void setListener(Message message) {
				stage.cosnumerListener(message);
			}
		};
		
		PowerMockito.whenNew(QueueListener.class).withNoArguments().thenReturn(listener);

		Field auditLog = AuditLogRequestBuilder.class.getDeclaredField("registrationProcessorRestService");
		auditLog.setAccessible(true);
		@SuppressWarnings("unchecked")
		RegistrationProcessorRestClientService<Object> mockObj = Mockito
				.mock(RegistrationProcessorRestClientService.class);
		auditLog.set(auditLogRequestBuilder, mockObj);
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(auditResponseDto);
		Mockito.doReturn(responseWrapper).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_401.toString(), EventName.ADD.toString(),
				EventType.BUSINESS.toString(), "1234testcase", ApiName.AUDIT);

	}

	@Test
	public void testAll() throws Exception {
		ctx = setContext();
		//PrintStageApplication.main(null);
		testDeployVerticle();
		testDeployVerticleForResend();
		testResendPrintPdfSuccess();
		//testRoutes();
		testResendPrintPdfFailure();
		//testResendPrintPdfException();
	}

	public void testDeployVerticle() throws Exception {

		String response = "{\"Status\":\"Success\",\"UIN\":\"6718394257\"}";
        ActiveMQBytesMessage amq= new ActiveMQBytesMessage();
        ByteSequence byteSeq = new ByteSequence();
        byteSeq.setData(response.getBytes());
        amq.setContent(byteSeq);
		stage.cosnumerListener(amq);
		stage.deployVerticle();
	}
	
	public void testDeployVerticleForResend() throws Exception {
		String response = "{\"Status\":\"Resend\",\"UIN\":\"6718394257\"}";
        ActiveMQBytesMessage amq= new ActiveMQBytesMessage();
        ByteSequence byteSeq = new ByteSequence();
        byteSeq.setData(response.getBytes());
        amq.setContent(byteSeq);
		stage.cosnumerListener(amq);
		stage.deployVerticle();
	}
	
	@Test
	public void testConsumerListenerException() throws Exception {
		IOException exp = new IOException();
        ActiveMQBytesMessage amq= new ActiveMQBytesMessage();
        ByteSequence byteSeq = new ByteSequence();
        byteSeq.setData("registration processor".getBytes());
        amq.setContent(byteSeq);
		PowerMockito.whenNew(String.class).withArguments(((ActiveMQBytesMessage) amq).getContent().data).thenThrow(exp);
		stage.cosnumerListener(amq);
	}
	
	@Test(expected=QueueConnectionNotFound.class)
	public void testDeployVerticleForException(){
		Mockito.when(mosipConnectionFactory.createConnection(anyString(), anyString(), anyString(), anyString()))
		.thenReturn(null);
		stage.deployVerticle();
	}
	
	@Test
	public void testPrintStageSuccess() {
		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");
		List<String> uinList = new ArrayList<>();
		uinList.add("3051738163");
		doNothing().when(printPostService).generatePrintandPostal(any(),any(),any());
		MessageDTO result = stage.process(dto);
		assertTrue(result.getIsValid());
	}

	@Test
	public void testPrintStageFailure() {
		Mockito.when(mosipQueueManager.send(any(), any(), anyString())).thenReturn(false);

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");
		
		List<String> uinList = new ArrayList<>();
		uinList.add("3051738163");
		doNothing().when(printPostService).generatePrintandPostal(any(),any(),any());

		MessageDTO result = stage.process(dto);
		assertFalse(result.getIsValid());
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
	public void testPdfGenerationException() {
		
		PDFGeneratorException e = new PDFGeneratorException(null, null);
		Mockito.doThrow(e).when(printService).getDocuments(any(), anyString());

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");
		List<String> uinList = new ArrayList<>();
		uinList.add("3051738163");
		doNothing().when(printPostService).generatePrintandPostal(any(),any(),any());
		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	@Test
	public void testTemplateProcessingFailureException() {
		TemplateProcessingFailureException e = new TemplateProcessingFailureException();
		Mockito.doThrow(e).when(printService).getDocuments(any(), anyString());

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");
		List<String> uinList = new ArrayList<>();
		uinList.add("3051738163");
		doNothing().when(printPostService).generatePrintandPostal(any(),any(),any());
		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	@Test
	public void testConnectionUnavailableException() {
		ConnectionUnavailableException e = new ConnectionUnavailableException();
		Mockito.doThrow(e).when(mosipQueueManager).send(any(), any(), anyString());

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");
		List<String> uinList = new ArrayList<>();
		uinList.add("3051738163");
		doNothing().when(printPostService).generatePrintandPostal(any(),any(),any());
		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	@Test
	public void testRetrySend() {
		QueueConnectionNotFound e = new QueueConnectionNotFound();
		Mockito.doThrow(e).when(mosipQueueManager).send(any(), any(), anyString());

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");
		List<String> uinList = new ArrayList<>();
		uinList.add("3051738163");

		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	@Test
	public void testException() throws ApisResourceAccessException {
		NullPointerException e = new NullPointerException();
		Mockito.doThrow(e).when(registrationStatusService).getRegistrationStatus(anyString());

		MessageDTO dto = new MessageDTO();
		dto.setRid("1234567890987654321");
		List<String> uinList = new ArrayList<>();
		uinList.add("3051738163");
		doNothing().when(printPostService).generatePrintandPostal(any(),any(),any());
		MessageDTO result = stage.process(dto);
		assertTrue(result.getInternalError());
	}

	public void testResendPrintPdfSuccess() {
		Mockito.when(uinValidatorImpl.validateId(any())).thenReturn(true);
		stage.reSendPrintPdf(ctx);
		assertTrue(responseObject);
	}
	
	public void testResendPrintPdfFailure() {
		Mockito.when(uinValidatorImpl.validateId(any())).thenReturn(false);
		stage.reSendPrintPdf(ctx);
		assertTrue(responseObject);
	}
	
	public void testRoutes() throws ClientProtocolException, IOException {
		HttpGet health = new HttpGet("http://localhost:8099/print-stage/health");
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse getResponse = client.execute(health);
		assertEquals(200, getResponse.getStatusLine().getStatusCode());

		HttpPost resend = getHttpPost("http://localhost:8099/v0.1/registration-processor/print-stage/resend");
		CloseableHttpResponse response = HttpClients.createDefault().execute(resend);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	private HttpPost getHttpPost(String url) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);
		String json = "{\"regId\":\"10011100110008720190329060420\",\"uin\":\"9754156940\",\"status\":\"Resend\"}";
		StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);
		httpPost.setHeader("Content-type", "application/json");
		return httpPost;
	}

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
				obj.put("uin" , "9754156940");
				obj.put("status","Resend");
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