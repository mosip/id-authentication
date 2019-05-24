package io.mosip.registration.processor.manual.verification.stage;


import static org.mockito.Matchers.any;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.manual.verification.dto.ManualVerificationDTO;
import io.mosip.registration.processor.manual.verification.dto.UserDto;
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
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

@RunWith(SpringRunner.class)

public class ManualVerificationStageTest{

	@Mock
	private MosipRouter router;
	public RoutingContext ctx;
	@Mock
	SignatureUtil signatureUtil;
	@Mock
	SignatureResponse signatureResponse;
	@Mock
	private ManualVerificationRequestValidator manualVerificationRequestValidator;
	@Mock
	private ManualVerificationService manualAdjudicationService;
	@Mock
	private Environment env;
	private File file;
	private String id = "2018782130000113112018183001.zip";
	private String newId = "2018782130000113112018183000.zip";
	public FileUpload fileUpload;
	private String serviceID="";
	private byte[] packetInfo;

	@InjectMocks
	private ManualVerificationStage manualverificationstage=new ManualVerificationStage()
	{
		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}

		@Override
		public MosipEventBus getEventBus(Object verticleName, String clusterManagerUrl) {
			return null;
		}
		@Override
		public void createServer(Router router, int port) {

		}
		@Override
		public Router postUrl(Vertx vertx, MessageBusAddress consumeAddress, MessageBusAddress sendAddress) {
			return null;
		}

		@Override
		public void setResponseWithDigitalSignature(RoutingContext ctx, Object object, String contentType) {

		}
	};
	@Before
	public void setUp() throws IOException
	{
		ReflectionTestUtils.setField(manualverificationstage, "port", "8080");
		ReflectionTestUtils.setField(manualverificationstage, "contextPath", "/registrationprocessor/v1/manualverification");
		//Mockito.when(env.getProperty(SwaggerConstant.SERVER_SERVLET_PATH)).thenReturn("/registrationprocessor/v1/manualverification");
		Mockito.doNothing().when(router).setRoute(any());
		Mockito.when(router.post(any())).thenReturn(null);
		Mockito.when(router.get(any())).thenReturn(null);
		Mockito.doNothing().when(manualVerificationRequestValidator).validate(any(),any());
		Mockito.when(signatureResponse.getData()).thenReturn("gdshgsahjhghgsad");
		packetInfo="packetInfo".getBytes();
		Mockito.when(manualAdjudicationService.getApplicantFile(any(),any())).thenReturn(packetInfo);
		//ClassLoader classLoader = getClass().getClassLoader();
		file = new File("/src/test/resources/0000.zip");
		//FileUtils.copyFile(file, new File(file.getParentFile().getPath() + "/" + id));
		//file = new File(classLoader.getResource(id).getFile());
		fileUpload = setFileUpload();
		ctx=setContext();
	}
	@Test
	public void testDeployeVerticle()
	{
		manualverificationstage.deployStage();
	}
	@Test
	public void testStart()
	{
		MessageDTO dto=new MessageDTO();
		manualverificationstage.process(dto);
		manualverificationstage.sendMessage(dto);
		manualverificationstage.start();
	}
	@Test
	public void testAllProcess()
	{
		testBiometric();
		testDemographic();
		testProcessAssignment();
		testProcessDecision();
		testProcessPacketInfo();
	}
	private void testBiometric()
	{
		serviceID="bio";
		Mockito.when(env.getProperty(any())).thenReturn("mosip.manual.verification.biometric");
		Mockito.when(env.getProperty("mosip.registration.processor.datetime.pattern")).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		manualverificationstage.processBiometric(ctx);
	}
	private void testDemographic()
	{
		serviceID="demo";
		Mockito.when(env.getProperty(any())).thenReturn("mosip.manual.verification.demographic");
		Mockito.when(env.getProperty("mosip.registration.processor.datetime.pattern")).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		manualverificationstage.processDemographic(ctx);
	}
	private void testProcessAssignment()
	{
		serviceID="assign";
		Mockito.when(env.getProperty(any())).thenReturn("mosip.manual.verification.assignment");
		Mockito.when(env.getProperty("mosip.registration.processor.datetime.pattern")).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		ManualVerificationDTO manualVerificationDTO= new ManualVerificationDTO();
		Mockito.when(manualAdjudicationService.assignApplicant(any(UserDto.class))).thenReturn(manualVerificationDTO);
		manualverificationstage.processAssignment(ctx);
	}
	private void testProcessDecision()
	{
		serviceID="decision";
		Mockito.when(env.getProperty(any())).thenReturn("mosip.manual.verification.decision");
		Mockito.when(env.getProperty("mosip.registration.processor.datetime.pattern")).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		ManualVerificationDTO updatedManualVerificationDTO=new ManualVerificationDTO();
		Mockito.when(manualAdjudicationService.updatePacketStatus(any(),any())).thenReturn(updatedManualVerificationDTO);
		manualverificationstage.processDecision(ctx);
	}
	private void testProcessPacketInfo()
	{
		serviceID="packetinfo";
		Mockito.when(env.getProperty(any())).thenReturn("mosip.manual.verification.packetinfo");
		Mockito.when(env.getProperty("mosip.registration.processor.datetime.pattern")).thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		PacketMetaInfo info=new PacketMetaInfo();
		Mockito.when(manualAdjudicationService.getApplicantPacketInfo(any())).thenReturn(info);
		manualverificationstage.processPacketInfo(ctx);
	}
	private FileUpload setFileUpload() {
		return new FileUpload() {

			@Override
			public String uploadedFileName() {
				return file.getPath();
			}

			@Override
			public long size() {
				return file.length();
			}

			@Override
			public String name() {
				return file.getName();
			}

			@Override
			public String fileName() {
				return newId;
			}

			@Override
			public String contentType() {
				return null;
			}

			@Override
			public String contentTransferEncoding() {
				return null;
			}

			@Override
			public String charSet() {
				return null;
			}
		};
	}
	private RoutingContext setContext() {
		return new RoutingContext() {

			@Override
			public Set<FileUpload> fileUploads() {
				Set<FileUpload> fileUploads = new HashSet<FileUpload>();
				fileUploads.add(fileUpload);
				return fileUploads;
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

