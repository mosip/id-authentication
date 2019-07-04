package io.mosip.kernel.uingenerator.config;

import static io.vertx.ext.healthchecks.impl.StatusHelper.isUp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.support.JdbcUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.uingenerator.constant.HibernatePersistenceConstant;
import io.mosip.kernel.uingenerator.constant.UINHealthConstants;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.netty.handler.codec.http.HttpResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.healthchecks.impl.HealthChecksImpl;
import io.vertx.ext.web.RoutingContext;

/**
 * Health check Handler for uingenerator which check database health,disk space,
 * verticle health
 * 
 * @author Urvil Joshi
 * 
 * @since 1.0.0
 *
 */
public class UinServiceHealthCheckerhandler implements HealthCheckHandler {

	private HealthChecks healthChecks;
	private final AuthProvider authProvider;
	private ObjectMapper objectMapper;
	private String driver;
	private String url;
	private String username;
	private String password;
	private File currentWorkingDirPath;
	private static final int THRESHOLD = 10485760;

	private static final String DEFAULT_QUERY = "SELECT 1";

	private UinServiceHealthCheckerhandler.JSONResultBuilder resultBuilder;
	/**
	 * The field for Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UinServiceHealthCheckerhandler.class);

	public UinServiceHealthCheckerhandler(Vertx vertx, AuthProvider provider, ObjectMapper objectMapper,
			Environment environment) {
		this.healthChecks = new HealthChecksImpl(vertx);
		this.authProvider = provider;
		this.objectMapper = objectMapper;
		this.driver = environment.getProperty(HibernatePersistenceConstant.JAVAX_PERSISTENCE_JDBC_DRIVER);
		this.url = environment.getProperty(HibernatePersistenceConstant.JAVAX_PERSISTENCE_JDBC_URL);
		this.username = environment.getProperty(HibernatePersistenceConstant.JAVAX_PERSISTENCE_JDBC_USER);
		this.password = environment.getProperty(HibernatePersistenceConstant.JAVAX_PERSISTENCE_JDBC_PASS);
		this.currentWorkingDirPath = new File(System.getProperty(UinGeneratorConstant.CURRENT_WORKING_DIRECTORY));
		this.resultBuilder = new UinServiceHealthCheckerhandler.JSONResultBuilder();
	}

	@Override
	public HealthCheckHandler register(String name, Handler<Future<Status>> procedure) {
		healthChecks.register(name, procedure);
		return this;
	}

	@Override
	public HealthCheckHandler register(String name, long timeout, Handler<Future<Status>> procedure) {
		healthChecks.register(name, timeout, procedure);
		return this;
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
			final JsonObject result = resultBuilder.create().add(UINHealthConstants.ERROR, exception.getMessage())
					.build();
			future.complete(Status.KO(result));
		}
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			try (final Statement statement = conn.createStatement()) {

				try (final ResultSet rs = statement.executeQuery(DEFAULT_QUERY)) {

					if (rs.next()) {
						final JsonObject result = resultBuilder.create()
								.add(UINHealthConstants.DATABASE, conn.getMetaData().getDatabaseProductName())
								.add(UINHealthConstants.HELLO, JdbcUtils.getResultSetValue(rs, 1)).build();
						future.complete(Status.OK(result));

					}
				}
			}
		} catch (SQLException exception) {
			final JsonObject result = resultBuilder.create().add(UINHealthConstants.ERROR, exception.getMessage())
					.build();
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
					.add(UINHealthConstants.TOTAL, this.currentWorkingDirPath.getTotalSpace())
					.add(UINHealthConstants.FREE, diskFreeInBytes).add(UINHealthConstants.THRESHOLD, THRESHOLD).build();
			future.complete(Status.OK(result));
		} else {
			final JsonObject result = resultBuilder.create().add(UINHealthConstants.ERROR,
					String.format(UINHealthConstants.THRESHOLD_ERROR, diskFreeInBytes, THRESHOLD)).build();
			future.complete(Status.KO(result));
		}

	}

	/**
	 * Consumer Verticle health check handler
	 * 
	 * @param future {@link Future} instance from handler
	 * @param vertx  {@link Vertx} instance
	 */
	public void verticleHealthHandler(Future<Status> future, Vertx vertx) {

		vertx.eventBus().send(UinGeneratorConstant.UIN_GENERATOR_ADDRESS, UINHealthConstants.PING, response -> {

			if (response.succeeded()) {
				final JsonObject result = resultBuilder.create()
						.add(UINHealthConstants.RESPONSE, response.result().body()).build();
				future.complete(Status.OK(result));
			} else {
				future.complete(Status.KO());
			}
		});
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
					&& rc.request().getHeader(HttpHeaders.CONTENT_TYPE).contains(UinGeneratorConstant.CONTENTTYPE)) {
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

		json.put(UINHealthConstants.DETAILS, new JsonObject());

		JsonArray checks = json.getJsonArray(UINHealthConstants.CHECKS);

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
	 * @param json  Summary json
	 * @param checks Json array of all registered parameters with details
	 */
	private void createResponse(JsonObject json, JsonArray checks) {
		for (int i = 0; i < checks.size(); i++) {
			JsonObject jsonobject = checks.getJsonObject(i);
			String id = jsonobject.getString(UINHealthConstants.ID);
			BaseHealthCheckModel healthCheckModel = new BaseHealthCheckModel();
			healthCheckModel.setStatus(jsonobject.getString(UINHealthConstants.STATUS));
			JsonObject result = null;
			try {
				if (jsonobject.containsKey(UINHealthConstants.DATA)) {
					healthCheckModel.setDetails(jsonobject.getJsonObject(UINHealthConstants.DATA).getMap());
					result = new JsonObject(objectMapper.writeValueAsString(healthCheckModel));

				} else {
					result = new JsonObject(objectMapper.writeValueAsString(healthCheckModel));
					result.remove(UINHealthConstants.DETAILS);
				}
			} catch (JsonProcessingException e) {
				LOGGER.error(e.getMessage());
			}

			json.getJsonObject(UINHealthConstants.DETAILS).put(id, result);

		}
	}

	@Override
	public synchronized HealthCheckHandler unregister(String name) {
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
		JsonObject data = json.getJsonObject(UINHealthConstants.DATA);
		if (data != null && data.getBoolean("procedure-execution-failure", false)) {
			return true;
		}

		JsonArray checks = json.getJsonArray(UINHealthConstants.CHECKS);
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
		final String outcome = json.getString(UINHealthConstants.OUTCOME);
		json.remove(UINHealthConstants.OUTCOME);
		json.put(UINHealthConstants.STATUS, outcome);
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
