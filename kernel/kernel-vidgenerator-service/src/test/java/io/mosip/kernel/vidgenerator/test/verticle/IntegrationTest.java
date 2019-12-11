/*
 * package io.mosip.kernel.vidgenerator.test.verticle;
 * 
 * import java.io.IOException; import java.net.ServerSocket; import
 * java.util.stream.Stream;
 * 
 * import org.junit.AfterClass; import org.junit.BeforeClass; import
 * org.junit.Test; import org.junit.runner.RunWith; import
 * org.springframework.context.annotation.AnnotationConfigApplicationContext;
 * import org.springframework.context.support.AbstractApplicationContext;
 * 
 * import com.fasterxml.jackson.databind.ObjectMapper; import
 * com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; import
 * com.redhat.ceylon.compiler.java.metadata.Ignore;
 * 
 * import io.mosip.kernel.core.exception.ServiceError; import
 * io.mosip.kernel.core.http.ResponseWrapper; import
 * io.mosip.kernel.core.util.DateUtils; import
 * io.mosip.kernel.vidgenerator.config.HibernateDaoConfig; import
 * io.mosip.kernel.vidgenerator.constant.EventType; import
 * io.mosip.kernel.vidgenerator.dto.VidFetchResponseDto; import
 * io.mosip.kernel.vidgenerator.verticle.VidExpiryVerticle; import
 * io.mosip.kernel.vidgenerator.verticle.VidPoolCheckerVerticle; import
 * io.mosip.kernel.vidgenerator.verticle.VidPopulatorVerticle; import
 * io.vertx.core.DeploymentOptions; import io.vertx.core.Verticle; import
 * io.vertx.core.Vertx; import io.vertx.core.VertxOptions; import
 * io.vertx.core.buffer.Buffer; import io.vertx.core.eventbus.EventBus; import
 * io.vertx.core.logging.Logger; import io.vertx.core.logging.LoggerFactory;
 * import io.vertx.core.logging.SLF4JLogDelegateFactory; import
 * io.vertx.ext.unit.Async; import io.vertx.ext.unit.TestContext; import
 * io.vertx.ext.unit.junit.VertxUnitRunner; import
 * io.vertx.ext.web.client.HttpResponse; import
 * io.vertx.ext.web.client.WebClient;
 * 
 * @RunWith(VertxUnitRunner.class) public class IntegrationTest {
 * 
 * private static Vertx vertx; private static int port; private static
 * AbstractApplicationContext context; private static ObjectMapper objectMapper
 * = new ObjectMapper(); private static Logger LOGGER;
 * 
 * @BeforeClass public static void setup(TestContext testContext) throws
 * IOException { System.setProperty("vertx.logger-delegate-factory-class-name",
 * SLF4JLogDelegateFactory.class.getName()); objectMapper.registerModule(new
 * JavaTimeModule()); LOGGER = LoggerFactory.getLogger(IntegrationTest.class);
 * ServerSocket socket = new ServerSocket(0); port = socket.getLocalPort();
 * System.setProperty("server.port", String.valueOf(port)); socket.close();
 * VertxOptions options = new VertxOptions(); context = new
 * AnnotationConfigApplicationContext(HibernateDaoConfig.class);
 * DeploymentOptions workerOptions = new DeploymentOptions().setWorker(true);
 * vertx = Vertx.vertx(options); Verticle[] workerVerticles = {new
 * VidPoolCheckerVerticle(context),new VidPopulatorVerticle(context),new
 * VidExpiryVerticle(context)}; Stream.of(workerVerticles).forEach(verticle ->
 * deploy(verticle, workerOptions, vertx)); vertx.setTimer(1000, handler ->
 * initPool()); LOGGER.info("down"); }
 * 
 * private static void deploy(Verticle verticle, DeploymentOptions opts, Vertx
 * vertx) { vertx.deployVerticle(verticle, opts, res -> { if (res.failed()) {
 * LOGGER.info("Failed to deploy verticle " +
 * verticle.getClass().getSimpleName()+" "+res.cause()); } else
 * if(res.succeeded()) { LOGGER.info("Deployed verticle " +
 * verticle.getClass().getSimpleName());
 * 
 * } }); }
 * 
 * private static void initPool() {
 * LOGGER.info("Service will be started after pooling vids.."); EventBus
 * eventBus=vertx.eventBus(); LOGGER.info("eventBus deployer {}",eventBus);
 * eventBus.publish(EventType.INITPOOL, EventType.INITPOOL); }
 * 
 * @AfterClass public static void cleanup(TestContext testContext) { if (vertx
 * != null && testContext != null)
 * vertx.close(testContext.asyncAssertSuccess()); if (context != null)
 * context.close(); }
 * 
 * @Test public void getTest(TestContext context) {
 * LOGGER.info("getVidSuccessTest execution..."); Async async = context.async();
 * WebClient client = WebClient.create(vertx); client.get(port, "localhost",
 * "/v1/vidgenerator/vid").send(ar -> { if (ar.succeeded()) {
 * HttpResponse<Buffer> httpResponse = ar.result();
 * LOGGER.info(httpResponse.bodyAsString()); context.assertEquals(200,
 * httpResponse.statusCode()); try { ResponseWrapper<?> uinResp =
 * objectMapper.readValue(httpResponse.bodyAsString(), ResponseWrapper.class);
 * VidFetchResponseDto dto = objectMapper.convertValue(uinResp.getResponse(),
 * VidFetchResponseDto.class); context.assertNotNull(dto.getVid()); } catch
 * (IOException e) { e.printStackTrace(); } client.close(); async.complete(); }
 * else { LOGGER.error(ar.cause().getMessage()); } }); }
 * 
 * @Test public void getViDExpiryEmptyTest(TestContext context) {
 * LOGGER.info("getVidSuccessTest execution..."); Async async = context.async();
 * WebClient client = WebClient.create(vertx); client.get(port, "localhost",
 * "/v1/vidgenerator/vid?videxpiry=").send(ar -> { if (ar.succeeded()) {
 * HttpResponse<Buffer> httpResponse = ar.result();
 * LOGGER.info(httpResponse.bodyAsString()); context.assertEquals(200,
 * httpResponse.statusCode()); try { ResponseWrapper<?> uinResp =
 * objectMapper.readValue(httpResponse.bodyAsString(), ResponseWrapper.class);
 * ServiceError dto = objectMapper.convertValue(uinResp.getErrors().get(0),
 * ServiceError.class); context.assertEquals(dto.getErrorCode(), "KER-VID-002");
 * } catch (IOException e) { e.printStackTrace(); } client.close();
 * async.complete(); } else { LOGGER.error(ar.cause().getMessage()); } }); }
 * 
 * @Test public void getViDExpiryInvalidPatternTest(TestContext context) {
 * LOGGER.info("getVidSuccessTest execution..."); Async async = context.async();
 * WebClient client = WebClient.create(vertx); client.get(port, "localhost",
 * "/v1/vidgenerator/vid?videxpiry="+(DateUtils.getCurrentDateTimeString().
 * replace("Z", ""))).send(ar -> {
 * 
 * if (ar.succeeded()) { HttpResponse<Buffer> httpResponse = ar.result();
 * LOGGER.info(httpResponse.bodyAsString()); context.assertEquals(200,
 * httpResponse.statusCode()); try { ResponseWrapper<?> uinResp =
 * objectMapper.readValue(httpResponse.bodyAsString(), ResponseWrapper.class);
 * ServiceError dto = objectMapper.convertValue(uinResp.getErrors().get(0),
 * ServiceError.class); context.assertEquals(dto.getErrorCode(), "KER-VID-004");
 * } catch (IOException e) { e.printStackTrace(); } client.close();
 * async.complete(); } else { LOGGER.error(ar.cause().getMessage()); } }); }
 * 
 * @Ignore
 * 
 * @Test public void getViDExpiryInvalidExpiryDateTest(TestContext context) {
 * LOGGER.info("getVidSuccessTest execution..."); Async async = context.async();
 * WebClient client = WebClient.create(vertx); client.get(port, "localhost",
 * "/v1/vidgenerator/vid?videxpiry=2018-12-10T06:12:52.994Z").send(ar -> {
 * 
 * if (ar.succeeded()) { HttpResponse<Buffer> httpResponse = ar.result();
 * LOGGER.info(httpResponse.bodyAsString()); context.assertEquals(200,
 * httpResponse.statusCode()); try { ResponseWrapper<?> uinResp =
 * objectMapper.readValue(httpResponse.bodyAsString(), ResponseWrapper.class);
 * ServiceError dto = objectMapper.convertValue(uinResp.getErrors().get(0),
 * ServiceError.class); context.assertEquals(dto.getErrorCode(), "KER-VID-003");
 * } catch (IOException e) { e.printStackTrace(); } client.close();
 * async.complete(); } else { LOGGER.error(ar.cause().getMessage()); } }); }
 * 
 * }
 */