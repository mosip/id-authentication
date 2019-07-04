package io.mosip.registration.processor.packet.receiver.stage;

import static org.mockito.Matchers.any;

import java.io.File;
import java.io.IOException;
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

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.packet.receiver.dto.PacketReceiverResponseDTO;
import io.mosip.registration.processor.packet.receiver.exception.handler.PacketReceiverExceptionHandler;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
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
public class PacketReceiverStageTest {
	@Mock
	Environment env;
	public RoutingContext ctx;
	private File file;
	private String id = "2018782130000113112018183001.zip";
	private String newId = "2018782130000113112018183000.zip";
	public FileUpload fileUpload;
	@Mock
	public PacketReceiverService<File, MessageDTO> packetReceiverService;
	@Mock
	SignatureUtil signatureUtil;
	@Mock
	SignatureResponse signatureResponse;
	@Mock
	private MosipRouter router;
	@Mock
	PacketReceiverExceptionHandler exceptionhandler;
	@InjectMocks
	PacketReceiverStage packetReceiverStage = new PacketReceiverStage() {

		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}

		@Override
		public MosipEventBus getEventBus(Object verticleName, String clusterManagerUrl, int instance) {
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
	public void setup() throws IOException, io.mosip.kernel.core.exception.IOException {
		ReflectionTestUtils.setField(packetReceiverStage, "port", "8080");
		ReflectionTestUtils.setField(packetReceiverStage, "contextPath", "/registrationprocessor/v1/packetreceiver");
		Mockito.when(env.getProperty("mosip.registration.processor.datetime.pattern"))
		.thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Mockito.doNothing().when(router).setRoute(any());
		Mockito.when(router.post(any())).thenReturn(null);
		Mockito.when(router.get(any())).thenReturn(null);
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("0000.zip").getFile());
		FileUtils.copyFile(file, new File(file.getParentFile().getPath() + "/" + id));
		file = new File(classLoader.getResource(id).getFile());
		fileUpload = setFileUpload();
		ctx = setContext();
	}

	@Test
	public void testStart() {
		packetReceiverStage.start();
	}

	@Test
	public void testProcessURLSuccess() throws Exception {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setIsValid(Boolean.TRUE);
		Mockito.when(packetReceiverService.validatePacket(any(File.class), any(String.class))).thenReturn(messageDTO);
		packetReceiverStage.processURL(ctx);
	}

	@Test
	public void testProcessURLFailure() throws Exception {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setIsValid(Boolean.FALSE);
		Mockito.when(packetReceiverService.validatePacket(any(File.class), any(String.class))).thenReturn(messageDTO);
		packetReceiverStage.processURL(ctx);
	}

	@Test
	public void testDeployVerticle() throws Exception {
		packetReceiverStage.deployVerticle();
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setIsValid(Boolean.FALSE);
		packetReceiverStage.process(messageDTO);
	}

	@Test
	public void testProcessPacket() {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setIsValid(Boolean.TRUE);
		Mockito.when(packetReceiverService.processPacket(any(File.class))).thenReturn(messageDTO);
		packetReceiverStage.processPacket(ctx);
	}

	@Test
	public void testFailure() {
		PacketReceiverResponseDTO packetReceiverResponseDTO = new PacketReceiverResponseDTO();
		Mockito.when(exceptionhandler.handler(ctx.failure()))
		.thenReturn(packetReceiverResponseDTO);
		Mockito.when(signatureResponse.getData()).thenReturn("gdshgsahjhghgsad");
		packetReceiverStage.failure(ctx);
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
