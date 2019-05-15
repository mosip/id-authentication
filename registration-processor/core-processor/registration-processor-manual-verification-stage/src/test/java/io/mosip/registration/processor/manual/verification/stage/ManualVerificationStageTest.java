/*
package io.mosip.registration.processor.manual.verification.stage;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.manual.verification.ManualVerificationApplication;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.dto.UserDto;
import io.mosip.registration.processor.manual.verification.exception.ManualVerificationAppException;
import io.mosip.registration.processor.manual.verification.response.builder.ManualVerificationResponseBuilder;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationAssignResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationBioDemoResponseDTO;
import io.mosip.registration.processor.manual.verification.response.dto.ManualVerificationPacketResponseDTO;
import io.mosip.registration.processor.manual.verification.service.ManualVerificationService;
import io.mosip.registration.processor.manual.verification.util.ManualVerificationRequestValidator;
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

@RunWith(SpringRunner.class)
public class ManualVerificationStageTest{

	private RoutingContext ctx;

	@Mock
	private ManualVerificationService manualAdjudicationService;
	@Mock
	ManualVerificationResponseBuilder manualVerificationResponseBuilder;

	private String jsonData;
	
	@Mock
	SignatureUtil signatureUtil;

	@Mock
	private Environment env;
	@Mock
	ManualVerificationRequestValidator manualVerificationRequestValidator;
	Gson gson = new GsonBuilder().create();
	String serviceID="";
	private List<ErrorDTO> errors=new ArrayList<>();
	ErrorDTO errorCode=new ErrorDTO("","");

	@Mock
	io.mosip.kernel.core.signatureutil.model.SignatureResponse signatureResponse;
	@InjectMocks
	ManualVerificationStage manualVerificationStage = new ManualVerificationStage() {

		@Override
		public void setResponse(RoutingContext ctx, Object object,String jsonType,String digitalSignature) {
			jsonData = object.toString();


			if(serviceID=="bio") {
				ManualVerificationBioDemoResponseDTO manualVerificationBioDemoResponseDTO =gson.fromJson(jsonData, ManualVerificationBioDemoResponseDTO.class);
				errors=manualVerificationBioDemoResponseDTO.getErrors();
			}else if(serviceID=="demo") {
				ManualVerificationBioDemoResponseDTO manualVerificationBioDemoResponseDTO =gson.fromJson(jsonData, ManualVerificationBioDemoResponseDTO.class);
				errors=manualVerificationBioDemoResponseDTO.getErrors();
			}else if(serviceID=="assign") {
				ManualVerificationAssignResponseDTO manualVerificationAssignResponseDTO =gson.fromJson(jsonData, ManualVerificationAssignResponseDTO.class);
				errors=manualVerificationAssignResponseDTO.getErrors();

			}else if(serviceID=="decision") {
				ManualVerificationAssignResponseDTO manualVerificationAssignResponseDTO =gson.fromJson(jsonData, ManualVerificationAssignResponseDTO.class);
				errors=manualVerificationAssignResponseDTO.getErrors();

			}else if(serviceID=="packetinfo") {
				ManualVerificationPacketResponseDTO manualVerificationPacketResponseDTO =gson.fromJson(jsonData, 		ManualVerificationPacketResponseDTO.class);
				errors=manualVerificationPacketResponseDTO.getErrors();

			}

		}

		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}

		@Override
		public MosipEventBus getEventBus(Object verticleName, String clusterManagerUrl) {
			return null;
		}
	};
	@Before
	public void setup() throws Exception {
		
		ctx = setContext();
		ManualVerificationApplication.main(null);
		when(env.getProperty(anyString())).thenReturn("mosip.manual.verification.biometric");
		when(env.getProperty("mosip.registration.processor.datetime.pattern")).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		
		signatureResponse=Mockito.mock(SignatureResponse.class);//new SignatureResponse();
		when(signatureUtil.signResponse(anyString())).thenReturn(signatureResponse);
		when(signatureResponse.getData()).thenReturn("gdshgsahjhghgsad");
		
	
	}



	public String getDataAsJson(String dataNumber) {
		JsonObject obj= new JsonObject();
		obj.put("id", "mosip.manual.verification.biometric");
		obj.put("version", "1.0");
		obj.put("responsetime", "2019-02-04T13:46:39.919+0000");
		JsonObject obj1= new JsonObject();
		obj1.put("regId", "27847657360002520181208123456");
		  
		if(dataNumber=="1") {
			obj.put("file", "MOSIPE@34whfh34");
		}else {
			obj.put("response",obj1);
		}
		errorCode=null;
		obj.put("errors",errorCode);
		


		return obj.toString();
	}





	@Test
	public void testAllProcesses() throws ClientProtocolException, IOException, ManualVerificationAppException {
		doNothing().when(manualVerificationRequestValidator).validate(any(JsonObject.class),any(String.class));

		processBiometricTest();
		processDemographicTest();
		processAssignmentTest();
		processDecisionTest();
		processPacketInfoTest();
		testSendMessage();
		testProcess();
		packetUploaderTest();
	}


	public void processBiometricTest(){
		serviceID="bio";
		byte[] packetInfo = "packetInfo".getBytes();
		when(manualAdjudicationService.getApplicantFile(any(String.class),any(String.class))).thenReturn(packetInfo);
		manualVerificationStage.processBiometric(ctx);
		assertEquals(errors, null);


	}


	public void processDemographicTest() throws ManualVerificationAppException{
		serviceID="demo";
		byte[] packetInfo = "packetInfo".getBytes();
		when(manualAdjudicationService.getApplicantFile(any(String.class),any(String.class))).thenReturn(packetInfo);
		manualVerificationStage.processDemographic(ctx);
		assertEquals(errors, null);

	}


	public void processAssignmentTest(){
		serviceID="assign";

		ManualVerificationDTO manualVerificationDTO= new ManualVerificationDTO();
		when(manualAdjudicationService.assignApplicant(any(UserDto.class),any(String.class))).thenReturn(manualVerificationDTO);
		manualVerificationStage.processAssignment(ctx);
		assertEquals(errors, null);

	}


	public void processDecisionTest(){
		serviceID="decision";
		manualVerificationStage.processDecision(ctx);
		assertEquals(errors, null);

	}


	public void processPacketInfoTest(){
		serviceID="packetinfo";
		PacketMetaInfo packetInfo = new PacketMetaInfo();
		when(manualAdjudicationService.getApplicantPacketInfo(any(String.class))).thenReturn(packetInfo);
		manualVerificationStage.processPacketInfo(ctx);
		assertEquals(errors, null);

	}


	public void testSendMessage() {
		manualVerificationStage.sendMessage(null);
	}

	public void testProcess() {
		manualVerificationStage.process(null);
	}

	public void packetUploaderTest() throws ClientProtocolException, IOException {

		HttpGet httpGet = new HttpGet("http://localhost:8084/manual-verification/health");
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse getResponse = client.execute(httpGet);
		assertEquals(200, getResponse.getStatusLine().getStatusCode());

		HttpPost applicantBiometric = getHttpPost("http://localhost:8084/manual-verification/applicantBiometric/v1.0");
		CloseableHttpResponse response = HttpClients.createDefault().execute(applicantBiometric);
		assertEquals(response.getStatusLine().getStatusCode(), 200);

		HttpPost applicantDemographic = getHttpPost("http://localhost:8084/manual-verification/applicantDemographic/v1.0");
		response = HttpClients.createDefault().execute(applicantDemographic);
		assertEquals(response.getStatusLine().getStatusCode(), 200);

		HttpPost assignment = getHttpPost("http://localhost:8084/manual-verification/assignment/v1.0");
		response = HttpClients.createDefault().execute(assignment);
		assertEquals(response.getStatusLine().getStatusCode(), 200);

		HttpPost decision = getHttpPost("http://localhost:8084/manual-verification/decision/v1.0");
		response = HttpClients.createDefault().execute(decision);
		assertEquals(response.getStatusLine().getStatusCode(), 200);

		HttpPost packetInfo = getHttpPost("http://localhost:8084/manual-verification/packetInfo/v1.0");
		response = HttpClients.createDefault().execute(packetInfo);
		assertEquals(response.getStatusLine().getStatusCode(), 200);

	}

	private HttpPost getHttpPost(String url) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);

		String json = "{'regId':'27847657360002520181208183004','fileName':'APPLICANTPHOTO'}";
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
				JsonObject obj= new JsonObject();
				obj.put("id", "51130282650000320190117144316");
				obj.put("version", "1.0");
				obj.put("requesttime", "51130282650000320190117");
				JsonObject obj1= new JsonObject();

				if(serviceID=="bio") {
					obj1.put("regId", "51130282650000320190117144316");
					obj1.put("fileName", "APPLICANTPHOTO");
				}else if(serviceID=="demo") {
					obj1.put("regId", "51130282650000320190117144316");

				}else if(serviceID=="assign") {
					obj1.put("userId", "51130282650000320190117");

				}else if(serviceID=="decision") {
					obj1.put("matchedRefId", "27847657360002520181208123987");
					obj1.put("matchedRefType", "RID");
					obj1.put("mvUsrId", "mono");
					obj1.put("reasonCode", "Problem with biometrics");
					obj1.put("regId", "27847657360002520181208123456");
					obj1.put("statusCode", "APPROVED");

				}else if(serviceID=="packetinfo") {
					obj1.put("regId", "51130282650000320190117144316");

				}

				obj.put("request", obj1);



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

*/