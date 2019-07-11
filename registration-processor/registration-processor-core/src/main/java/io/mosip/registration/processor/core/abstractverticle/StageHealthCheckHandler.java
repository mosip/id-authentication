package io.mosip.registration.processor.core.abstractverticle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.PrivilegedExceptionAction;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.security.SaslRpcServer.AuthMethod;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.healthchecks.impl.HealthChecksImpl;
import io.vertx.ext.web.RoutingContext;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;
import xyz.capybara.clamav.exceptions.ClamavException;

import static io.vertx.ext.healthchecks.impl.StatusHelper.isUp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.jms.BytesMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.jdbc.support.JdbcUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.mosip.registration.processor.core.constant.HealthConstant;
import io.netty.handler.codec.http.HttpResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.ReadStream;

/**
 * @author Mukul Puspam
 *
 */
public class StageHealthCheckHandler implements HealthCheckHandler {
	private HealthChecks healthChecks;
	private final AuthProvider authProvider;
	private ObjectMapper objectMapper;
	private String driver;
	private String url;
	private String username;
	private String password;
	private String clamavHost;
	private String nameNodeUrl;
	private String hdfsUserName;
	private String kdcDomain;
	private String keytabPath;
	private String queueUsername;
	private String queuePassword;
	private String queueBrokerUrl;
	private Boolean isAuthEnable;
	private int clamavPort;
	private File currentWorkingDirPath;
	private ClamavClient clamavClient;
	private FileSystem configuredFileSystem;
	private Path hadoopLibPath;
	private static final String HADOOP_HOME = "hadoop-lib";
	private static final String WIN_UTIL = "winutils.exe";
	private static final String CLASSPATH_PREFIX = "classpath:";
	private static final int THRESHOLD = 10485760;

	private static final String DEFAULT_QUERY = "SELECT 1";

	private StageHealthCheckHandler.JSONResultBuilder resultBuilder;
	/**
	 * The field for Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StageHealthCheckHandler.class);

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	/**
	 * @param vertx
	 * @param provider
	 * @param objectMapper
	 * @param environment
	 */
	public StageHealthCheckHandler(Vertx vertx, AuthProvider provider, ObjectMapper objectMapper,
			Environment environment) {
		this.healthChecks = new HealthChecksImpl(vertx);
		this.authProvider = provider;
		this.objectMapper = objectMapper;
		this.driver = environment.getProperty(HealthConstant.DRIVER);
		this.url = environment.getProperty(HealthConstant.URL);
		this.username = environment.getProperty(HealthConstant.USER);
		this.password = environment.getProperty(HealthConstant.PASSWORD);
		this.clamavHost = environment.getProperty(HealthConstant.CLAMAV_HOST);
		this.clamavPort = Integer.parseInt(environment.getProperty(HealthConstant.CLAMAV_PORT));
		this.nameNodeUrl = environment.getProperty(HealthConstant.NAMENODEURL);
		this.isAuthEnable = Boolean.parseBoolean(environment.getProperty(HealthConstant.IS_AUTH_ENABLE));
		this.hdfsUserName = environment.getProperty(HealthConstant.HDFSUSERNAME);
		this.kdcDomain = environment.getProperty(HealthConstant.KDC_DOMAIN);
		this.keytabPath = environment.getProperty(HealthConstant.KEY_TAB_PATH);
		this.queueUsername = environment.getProperty(HealthConstant.QUEUE_USERNAME);
		this.queuePassword = environment.getProperty(HealthConstant.QUEUE_PASSWORD);
		this.queueBrokerUrl = environment.getProperty(HealthConstant.QUEUE_BROKER_URL);
		this.currentWorkingDirPath = new File(System.getProperty(HealthConstant.CURRENT_WORKING_DIRECTORY));
		this.resultBuilder = new StageHealthCheckHandler.JSONResultBuilder();
	}

	@Override
	public StageHealthCheckHandler register(String name, Handler<Future<Status>> procedure) {
		healthChecks.register(name, procedure);
		return this;
	}

	@Override
	public StageHealthCheckHandler register(String name, long timeout, Handler<Future<Status>> procedure) {
		healthChecks.register(name, timeout, procedure);
		return this;
	}

	/**
	 * @param future
	 */
	public void queueHealthChecker(Future<Status> future) {
		try {
			ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(queueUsername,
					queuePassword, queueBrokerUrl);
			javax.jms.Connection connection = activeMQConnectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue destination = session.createQueue(HealthConstant.QUEUE_ADDRESS);
			MessageProducer messageProducer = session.createProducer(destination);
			BytesMessage byteMessage = session.createBytesMessage();
			byteMessage.writeObject((HealthConstant.PING).getBytes());
			messageProducer.send(byteMessage);
			MessageConsumer messageConsumer = session.createConsumer(destination);
			String res = new String(((ActiveMQBytesMessage) messageConsumer.receive()).getContent().data);
			final JsonObject result = resultBuilder.create().add(HealthConstant.RESPONSE, res).build();
			future.complete(Status.OK(result));
		} catch (Exception e) {
			final JsonObject result = resultBuilder.create().add(HealthConstant.ERROR, e.getMessage()).build();
			future.complete(Status.KO(result));
		}
	}

	/**
	 * @param future
	 */
	public void hdfsHealthChecker(Future<Status> future) {
		Configuration configuration = null;
		try {
			configuration = new Configuration();
			configuration.set("fs.defaultFS", nameNodeUrl);
			configuration.set("dfs.client.use.datanode.hostname", "true");
			configuration.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
			hadoopLibPath = Files.createTempDirectory(HADOOP_HOME);
			System.setProperty("hadoop.home.dir", hadoopLibPath.toString());
			if (SystemUtils.IS_OS_WINDOWS) {
				File binPath = FileUtils.getFile(hadoopLibPath.toString(), "bin");
				binPath.mkdir();
				Resource resource = resourceLoader.getResource(CLASSPATH_PREFIX + WIN_UTIL);
				if (resource.exists()) {
					java.nio.file.Path c = Paths.get("s", "x");
					File winUtilsPath = FileUtils.getFile(binPath.toString(), resource.getFilename());
					   FileUtils.copyInputStreamToFile(resource.getInputStream(), winUtilsPath);
					   }
			}

			if (isAuthEnable) {
				configuration = initSecurityConfiguration(configuration);
				configuredFileSystem = FileSystem.get(configuration);
			} else {
				configuredFileSystem = getDefaultConfiguredFileSystem(configuration);
			}

			org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(
					FilenameUtils.concat("1000".toUpperCase(), "1000".toUpperCase()));

			final JsonObject result = resultBuilder.create()
					.add(HealthConstant.RESPONSE, configuredFileSystem.exists(path)).build();
			future.complete(Status.OK(result));

		} catch (Exception e) {
			final JsonObject result = resultBuilder.create().add(HealthConstant.ERROR, e.getMessage()).build();
			future.complete(Status.KO(result));
		}
	}

	private FileSystem getDefaultConfiguredFileSystem(Configuration configuration) throws Exception {
		try {
			configuredFileSystem = UserGroupInformation.createRemoteUser(hdfsUserName, AuthMethod.TOKEN)
					.doAs(new PrivilegedExceptionAction<FileSystem>() {
						public FileSystem run() throws IOException {
							return FileSystem.get(configuration);
						}
					});
		} catch (Exception e) {
			throw new Exception("HDFS_ADAPTER_EXCEPTION", e);
		}
		return configuredFileSystem;
	}

	/**
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	private Configuration initSecurityConfiguration(Configuration configuration) throws Exception {
		configuration.set("dfs.data.transfer.protection", "authentication");
		configuration.set("hadoop.security.authentication", "kerberos");
		InputStream krbStream = getClass().getClassLoader().getResourceAsStream("krb5.conf");
		File krbPath = FileUtils.getFile(hadoopLibPath.toString(), "krb5.conf");
		 FileUtils.copyInputStreamToFile(krbStream, krbPath);
		System.setProperty("java.security.krb5.conf", krbPath.toString());
		UserGroupInformation.setConfiguration(configuration);
		String user = hdfsUserName + "@" + kdcDomain;
		loginWithKeyTab(user, keytabPath);
		return configuration;
	}

	/**
	 * @param user
	 * @param keytabPath
	 * @throws Exception
	 */
	private void loginWithKeyTab(String user, String keytabPath) throws Exception {
		File keyPath = null;
		Resource resource = resourceLoader.getResource(keytabPath);
		File dataPath = FileUtils.getFile(hadoopLibPath.toString(), "data");
		dataPath.mkdir();
		if (resource.exists()) {
			keyPath = FileUtils.getFile(dataPath.toString(), resource.getFilename());
			FileUtils.copyInputStreamToFile(resource.getInputStream(), keyPath);
		} else {
			throw new Exception("KEYTAB_FILE_NOT_FOUND_EXCEPTION: " + keytabPath);
		}
		try {
			UserGroupInformation.loginUserFromKeytab(user, keyPath.toString());
		} catch (IOException e) {
			throw new Exception("LOGIN_EXCEPTION", e);
		}
	}

	/**
	 * @param future
	 */
	public void virusScanHealthChecker(Future<Status> future) {
		File file = new File(getClass().getClassLoader().getResource("cbef.xml").getFile());
		try {
			this.clamavClient = new ClamavClient(clamavHost, clamavPort);
			ScanResult scanResult;
			scanResult = this.clamavClient.scan(new FileInputStream(file));

			final JsonObject result = resultBuilder.create().add(HealthConstant.RESPONSE, scanResult.getStatus().name())
					.build();
			future.complete(Status.OK(result));

		} catch (FileNotFoundException | ClamavException e) {
			final JsonObject result = resultBuilder.create().add(HealthConstant.ERROR, e.getMessage()).build();
			future.complete(Status.KO(result));
		}
	}

	/**
	 * Database health check handler
	 * 
	 * @param future {@link Future} instance from handler
	 */
	public void databaseHealthChecker(Future<Status> future) {

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException exception) {
			final JsonObject result = resultBuilder.create().add(HealthConstant.ERROR, exception.getMessage()).build();
			future.complete(Status.KO(result));
		}
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			try (final Statement statement = conn.createStatement()) {

				try (final ResultSet rs = statement.executeQuery(DEFAULT_QUERY)) {

					if (rs.next()) {
						final JsonObject result = resultBuilder.create()
								.add(HealthConstant.DATABASE, conn.getMetaData().getDatabaseProductName())
								.add(HealthConstant.HELLO, JdbcUtils.getResultSetValue(rs, 1)).build();
						future.complete(Status.OK(result));

					}
				}
			}
		} catch (SQLException exception) {
			final JsonObject result = resultBuilder.create().add(HealthConstant.ERROR, exception.getMessage()).build();
			future.complete(Status.KO(result));
		}
	}

	/**
	 * Disk-Space health check Handler
	 * 
	 * @param future {@link Future} instance from handler
	 */
	public void dispSpaceHealthChecker(Future<Status> future) {

		final long diskFreeInBytes = this.currentWorkingDirPath.getUsableSpace();
		if (diskFreeInBytes >= THRESHOLD) {
			final JsonObject result = resultBuilder.create()
					.add(HealthConstant.TOTAL, this.currentWorkingDirPath.getTotalSpace())
					.add(HealthConstant.FREE, diskFreeInBytes).add(HealthConstant.THRESHOLD, THRESHOLD).build();
			future.complete(Status.OK(result));
		} else {
			final JsonObject result = resultBuilder.create().add(HealthConstant.ERROR,
					String.format(HealthConstant.THRESHOLD_ERROR, diskFreeInBytes, THRESHOLD)).build();
			future.complete(Status.KO(result));
		}

	}

	/**
	 * Send Verticle health check handler
	 * 
	 * @param future {@link Future} instance from handler
	 * @param vertx  {@link Vertx} instance
	 */
	public void senderHealthHandler(Future<Status> future, Vertx vertx, String address) {
		try {
			vertx.eventBus().send(address, HealthConstant.PING);
			final JsonObject result = resultBuilder.create().add(HealthConstant.RESPONSE, HealthConstant.PING).build();
			future.complete(Status.OK(result));
		} catch (Exception e) {
			final JsonObject result = resultBuilder.create().add(HealthConstant.ERROR, e.getMessage()).build();
			future.complete(Status.KO(result));
		}
	}

	/**
	 * @param future
	 * @param vertx
	 * @param address
	 */
	public void consumerHealthHandler(Future<Status> future, Vertx vertx, String address) {
		try {
			Boolean isRegistered = vertx.eventBus().consumer(address).isRegistered();
			final JsonObject result = resultBuilder.create().add(HealthConstant.RESPONSE, isRegistered).build();
			future.complete(Status.OK(result));
		} catch (Exception e) {
			final JsonObject result = resultBuilder.create().add(HealthConstant.ERROR, e.getMessage()).build();
			future.complete(Status.KO(result));
		}
	}

	@Override
	public void handle(RoutingContext rc) {
		String path = rc.request().path();
		String mount = rc.mountPoint();
		String route = rc.currentRoute().getPath();

		String id;

		// We are under a sub-router.
		// Remove the mount prefix from the path
		if (mount != null && path.startsWith(mount)) {
			path = path.substring(mount.length());
		}

		// The route has a path, remove this path from the path
		if (route != null && path.startsWith(route)) {
			id = path.substring(route.length());
		} else {
			id = path;
		}

		if (authProvider != null) {
			// Copy all HTTP header in a json array and params
			JsonObject authData = new JsonObject();
			rc.request().headers().forEach(entry -> authData.put(entry.getKey(), entry.getValue()));
			rc.request().params().forEach(entry -> authData.put(entry.getKey(), entry.getValue()));
			if (rc.request().method() == HttpMethod.POST && rc.request().getHeader(HttpHeaders.CONTENT_TYPE) != null
					&& rc.request().getHeader(HttpHeaders.CONTENT_TYPE).contains(HealthConstant.CONTENT_TYPE)) {
				authData.mergeIn(rc.getBodyAsJson());
			}
			authProvider.authenticate(authData, ar -> {
				if (ar.failed()) {
					rc.response().setStatusCode(403).end();
				} else {
					healthChecks.invoke(id, this.healthSummaryHandler(rc));
				}
			});
		} else {
			healthChecks.invoke(id, this.healthSummaryHandler(rc));
		}
	}

	/**
	 * Create health check summary
	 * 
	 * @param rc {@link RoutingContext} instance
	 * @return {@link Handler}
	 */
	private Handler<AsyncResult<JsonObject>> healthSummaryHandler(RoutingContext rc) {
		return json -> {
			HttpServerResponse response = rc.response().putHeader(HttpHeaders.CONTENT_TYPE,
					"application/json;charset=UTF-8");
			if (json.failed()) {
				if (json.cause().getMessage().toLowerCase().contains("not found")) {
					response.setStatusCode(404);
				} else {
					response.setStatusCode(400);
				}
				response.end("{\"message\": \"" + json.cause().getMessage() + "\"}");
			} else {
				createResponse(json.result(), response);
			}
		};
	}

	/**
	 * Create a json response
	 * 
	 * @param json     summary json
	 * @param response {@link HttpResponse}
	 */
	private void createResponse(JsonObject json, HttpServerResponse response) {
		int status = isUp(json) ? 200 : 503;

		if (status == 503 && hasErrors(json)) {
			status = 500;
		}

		json.put(HealthConstant.DETAILS, new JsonObject());

		JsonArray checks = json.getJsonArray(HealthConstant.CHECKS);

		if (status == 200 && checks.isEmpty()) {
			// Special case, no procedure installed.
			response.setStatusCode(204).end();
			return;
		}
		if (checks != null && !checks.isEmpty()) {
			createResponse(json, checks);
		}
		response.setStatusCode(status).end(encode(json));
	}

	/**
	 * Copy actual response to Spring actuator like response
	 * 
	 * @param json   Summary json
	 * @param checks Json array of all registered parameters with details
	 */
	private void createResponse(JsonObject json, JsonArray checks) {
		for (int i = 0; i < checks.size(); i++) {
			JsonObject jsonobject = checks.getJsonObject(i);
			String id = jsonobject.getString(HealthConstant.ID);
			BaseHealthCheckModel healthCheckModel = new BaseHealthCheckModel();
			healthCheckModel.setStatus(jsonobject.getString(HealthConstant.STATUS));
			JsonObject result = null;
			try {
				if (jsonobject.containsKey(HealthConstant.DATA)) {
					healthCheckModel.setDetails(jsonobject.getJsonObject(HealthConstant.DATA).getMap());
					result = new JsonObject(objectMapper.writeValueAsString(healthCheckModel));

				} else {
					result = new JsonObject(objectMapper.writeValueAsString(healthCheckModel));
					result.remove("details");
				}
			} catch (JsonProcessingException e) {
				LOGGER.error(e.getMessage());
			}

			json.getJsonObject(HealthConstant.DETAILS).put(id, result);

		}
	}

	@Override
	public synchronized StageHealthCheckHandler unregister(String name) {
		healthChecks.unregister(name);
		return this;
	}

	/**
	 * Check if error has occurred or not
	 * 
	 * @param json Summary json
	 * @return True if has Error;else False
	 */
	private boolean hasErrors(JsonObject json) {
		JsonObject data = json.getJsonObject(HealthConstant.DATA);
		if (data != null && data.getBoolean("procedure-execution-failure", false)) {
			return true;
		}

		JsonArray checks = json.getJsonArray(HealthConstant.CHECKS);
		if (checks != null) {
			for (int i = 0; i < checks.size(); i++) {
				JsonObject check = checks.getJsonObject(i);
				if (hasErrors(check)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Encode the json object
	 * 
	 * @param json Result json
	 * @return Encoded Json String
	 */
	private String encode(JsonObject json) {
		final String outcome = json.getString(HealthConstant.OUTCOME);
		json.remove(HealthConstant.OUTCOME);
		json.put(HealthConstant.STATUS, outcome);
		return json.encode();
	}

	static class JSONResultBuilder {

		private JsonObject jsonObject;

		public JSONResultBuilder create() {
			jsonObject = new JsonObject();
			return this;
		}

		public JSONResultBuilder add(String key, Object object) {
			jsonObject.put(key, object);
			return this;
		}

		public JsonObject build() {
			return jsonObject;
		}

	}

}
