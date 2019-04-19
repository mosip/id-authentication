package io.mosip.kernel.uingenerator.healthcheck;

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

import io.mosip.kernel.uingenerator.constant.UINHealthConstants;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.healthcheck.model.BaseHealthCheckModel;
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
import io.vertx.ext.healthchecks.impl.HealthCheckHandlerImpl;
import io.vertx.ext.healthchecks.impl.HealthChecksImpl;
import io.vertx.ext.web.RoutingContext;

/**
 * Health check Handler for uingenerator which check database health,disk space, verticle health
 * 
 * @author Urvil Joshi
 * 
 * @since 1.0.0
 *
 */
public class UINHealthCheckerhandler implements HealthCheckHandler {

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
	

	/**
	 * The field for Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UINHealthCheckerhandler.class);

	public UINHealthCheckerhandler(Vertx vertx, AuthProvider provider, ObjectMapper objectMapper,
			Environment environment) {
		this.healthChecks = new HealthChecksImpl(vertx);
		this.authProvider = provider;
		this.objectMapper = objectMapper;
		this.driver = environment.getProperty(UinGeneratorConstant.JAVAX_PERSISTENCE_JDBC_DRIVER);
		this.url = environment.getProperty(UinGeneratorConstant.JAVAX_PERSISTENCE_JDBC_URL);
		this.username = environment.getProperty(UinGeneratorConstant.JAVAX_PERSISTENCE_JDBC_USER);
		this.password = environment.getProperty(UinGeneratorConstant.JAVAX_PERSISTENCE_JDBC_PASS);
		this.currentWorkingDirPath = new File(System.getProperty(UinGeneratorConstant.CURRENT_WORKING_DIRECTORY));
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

	public void databaseHealthChecker(Future<Status> future) {

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException exception) {
			UINHealthCheckerhandler.JSONResultBuilder resultBuilder = new UINHealthCheckerhandler.JSONResultBuilder();
			final JsonObject result = resultBuilder.add(UINHealthConstants.ERROR, exception.getMessage()).build();
			future.complete(Status.KO(result));
		}
		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			try(Statement statement = conn.createStatement()){
			

			try(ResultSet rs = statement.executeQuery(DEFAULT_QUERY)){

			if (rs.next()) {
				UINHealthCheckerhandler.JSONResultBuilder resultBuilder = new UINHealthCheckerhandler.JSONResultBuilder();
				final JsonObject result = resultBuilder.add(UINHealthConstants.DATABASE, conn.getMetaData().getDatabaseProductName())
						.add(UINHealthConstants.HELLO, JdbcUtils.getResultSetValue(rs, 1)).build();
				future.complete(Status.OK(result));

			}
			}
			}
		} catch (SQLException exception) {
			UINHealthCheckerhandler.JSONResultBuilder resultBuilder = new UINHealthCheckerhandler.JSONResultBuilder();
			final JsonObject result = resultBuilder.add(UINHealthConstants.ERROR, exception.getMessage()).build();
			future.complete(Status.KO(result));
		}
	}

	public void dispSpaceHealthChecker(Future<Status> future) {

		long diskFreeInBytes = this.currentWorkingDirPath.getUsableSpace();
		UINHealthCheckerhandler.JSONResultBuilder resultBuilder = new UINHealthCheckerhandler.JSONResultBuilder();
		if (diskFreeInBytes >= THRESHOLD) {
			final JsonObject result = resultBuilder.add(UINHealthConstants.TOTAL, this.currentWorkingDirPath.getTotalSpace())
					.add(UINHealthConstants.FREE, diskFreeInBytes).add(UINHealthConstants.THRESHOLD, THRESHOLD).build();
			future.complete(Status.OK(result));
		} else {
			final JsonObject result = resultBuilder.add(UINHealthConstants.ERROR,
					String.format(UINHealthConstants.THRESHOLD_ERROR,
							diskFreeInBytes, THRESHOLD))
					.build();
			future.complete(Status.KO(result));
		}

	}

	public void verticleHealthHandler(Future<Status> future, Vertx vertx) {

		vertx.eventBus().send(UinGeneratorConstant.UIN_GENERATOR_ADDRESS, UINHealthConstants.PING, response -> {
			
			
			UINHealthCheckerhandler.JSONResultBuilder resultBuilder = new UINHealthCheckerhandler.JSONResultBuilder();
			if (response.succeeded()) {
				final JsonObject result = resultBuilder.add(UINHealthConstants.RESPONSE, response.result().body()).build();
				future.complete(Status.OK(result));
			} else {
				final JsonObject result = resultBuilder.add(UINHealthConstants.ERROR, UINHealthConstants.NOT_ACTIVE).build();
				future.complete(Status.KO(result));
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
		if(checks!=null && !checks.isEmpty()) {
		createResponse(json, checks);
		}
		response.setStatusCode(status).end(encode(json));
	}

	private void createResponse(JsonObject json, JsonArray checks) {
		for (int i = 0; i < checks.size(); i++) {
			JsonObject jsonobject = checks.getJsonObject(i);
			String id = jsonobject.getString(UINHealthConstants.ID);
			BaseHealthCheckModel healthCheckModel = new BaseHealthCheckModel();
			healthCheckModel.setStatus(jsonobject.getString(UINHealthConstants.STATUS));
			if (jsonobject.containsKey(UINHealthConstants.DATA)) {
				healthCheckModel.setDetails(jsonobject.getJsonObject(UINHealthConstants.DATA).getMap());
			}
				try {
					json.getJsonObject(UINHealthConstants.DETAILS).put(id,
							new JsonObject(objectMapper.writeValueAsString(healthCheckModel)));
				} catch (JsonProcessingException e) {
					LOGGER.error(e.getMessage());
				}
			
		}
	}

	@Override
	public synchronized HealthCheckHandler unregister(String name) {
		healthChecks.unregister(name);
		return this;
	}

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

	private String encode(JsonObject json) {
		String outcome = json.getString(UINHealthConstants.OUTCOME);
		json.remove(UINHealthConstants.OUTCOME);
		json.put(UINHealthConstants.STATUS, outcome);
		return json.encode();
	}

	class JSONResultBuilder {

		private JsonObject jsonObject;

		public JSONResultBuilder() {
			jsonObject = new JsonObject();
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
