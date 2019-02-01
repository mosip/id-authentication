package io.mosip.registration.processor.packet.receiver.stage;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;

@RunWith(SpringRunner.class)
public class PacketReceiverStageTest {

	private Vertx vertx;
	private String id = "2018782130000113112018183001.zip";
	private String newId = "2018782130000113112018183000.zip";
	private File file;
	private String registrationStatusCode;

	@Mock
	public PacketReceiverService<File, MessageDTO> packetReceiverService;

	public RoutingContext ctx;

	public FileUpload fileUpload;

	@InjectMocks
	PacketReceiverStage packetReceiverStage = new PacketReceiverStage() {
	
		@Override
		public void setResponse(RoutingContext ctx, Object object) {
			registrationStatusCode = object.toString();
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
	public void setup() throws IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("0000.zip").getFile());
		FileUtils.copyFile(file, new File(file.getParentFile().getPath() + "/" + id));
		file = new File(classLoader.getResource(id).getFile());

		fileUpload = setFileUpload();
		ctx = setContext();

		AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext();
		configApplicationContext.scan("io.mosip.registration.processor.packet.receiver.config",
				"io.mosip.registration.processor.packet.manager.config",
				"io.mosip.registration.processor.status.config", "io.mosip.registration.processor.core.kernel.beans");
		configApplicationContext.refresh();
		PacketReceiverStage packetReceiverStage = configApplicationContext.getBean(PacketReceiverStage.class);
		vertx = Vertx.vertx();
		vertx.deployVerticle(packetReceiverStage);
	}

	@Test
	public void testProcessURLSuccess() {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setIsValid(Boolean.TRUE);
		when(packetReceiverService.storePacket(any(File.class))).thenReturn(messageDTO);
		
		packetReceiverStage.processURL(ctx);
		
		assertEquals(RegistrationStatusCode.PACKET_UPLOADED_TO_VIRUS_SCAN.toString(), registrationStatusCode);
	}
	
	@Test
	public void testProcessURLFail() {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setIsValid(Boolean.FALSE);
		when(packetReceiverService.storePacket(any(File.class))).thenReturn(messageDTO);
		
		packetReceiverStage.processURL(ctx);
		
		assertEquals(RegistrationStatusCode.DUPLICATE_PACKET_RECIEVED.name(), registrationStatusCode);
	}

	@Test
	public void healthCheckTest() throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet("http://localhost:8081/health");
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = client.execute(httpGet);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	@Test
	public void packetUploaderTest() throws ClientProtocolException, IOException {
		FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("file", fileBody);
		HttpEntity entity = builder.build();

		HttpPost request = new HttpPost("http://localhost:8081/v0.1/registration-processor/packet-receiver/registrationpackets");
		request.setEntity(entity);

		HttpClient client = HttpClientBuilder.create().build();

		HttpResponse response = client.execute(request);

	}


	@Test
	public void testDeployVerticle() {
		packetReceiverStage.deployVerticle();
	}
	
	@Test
	public void testSendMessage() {
		packetReceiverStage.sendMessage(null);
	}
	
	@After
	public void destroy() throws IOException {
		if(file.exists())
			FileUtils.forceDelete(file);
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
				return null;
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
