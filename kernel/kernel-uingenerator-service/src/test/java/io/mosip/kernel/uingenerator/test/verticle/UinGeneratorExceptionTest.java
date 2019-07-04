package io.mosip.kernel.uingenerator.test.verticle;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.uingenerator.config.HibernateDaoConfig;
import io.mosip.kernel.uingenerator.constant.UinGeneratorErrorCode;
import io.mosip.kernel.uingenerator.verticle.HttpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

@RunWith(VertxUnitRunner.class)
public class UinGeneratorExceptionTest {

	private static Vertx vertx;
	private static int port;
	private static AbstractApplicationContext context;
	private static Logger LOGGER;

	@BeforeClass
	public static void setup(TestContext testContext) throws IOException {
		System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		System.setProperty("mosip.kernel.uin.uins-to-generate", "0");
		LOGGER = LoggerFactory.getLogger(UinGeneratorExceptionTest.class);
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
		context = new AnnotationConfigApplicationContext(HibernateDaoConfig.class);
		vertx = Vertx.vertx();
		// Without UinGeneratorVerticle deployed
		Verticle[] verticles = { new HttpServerVerticle(context) };
		Stream.of(verticles)
				.forEach(verticle -> vertx.deployVerticle(verticle, options, testContext.asyncAssertSuccess()));
		cleanAllUins();
	}

	private static void cleanAllUins() {
		DataSource dataSource = (DataSource) context.getBean("dataSource");
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM kernel.uin");
			while (rs.next()) {
				System.out.println("UIN=" + rs.getString("uin") + ", Status=" + rs.getString("uin_status"));
			}
			System.out.println("Deleting all UINs..");
			stmt.executeUpdate("TRUNCATE TABLE kernel.uin");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@AfterClass
	public static void cleanup(TestContext testContext) {
		if (vertx != null && testContext != null)
			vertx.close(testContext.asyncAssertSuccess());
		if (context != null)
			context.close();
	}

	@Test
	public void getUinExceptionTest(TestContext context) {
		LOGGER.info("getUinExceptionTest execution...");
		Async async = context.async();
		WebClient client = WebClient.create(vertx);
		client.get(port, "localhost", "/v1/uingenerator/uin").send(ar -> {
			if (ar.succeeded()) {
				HttpResponse<Buffer> httpResponse = ar.result();
				System.out.println(httpResponse.bodyAsString());
				context.assertEquals(httpResponse.statusCode(), 200);
				List<ServiceError> validationErrorsList = ExceptionUtils
						.getServiceErrorList(httpResponse.bodyAsString());
				assertTrue(validationErrorsList.size() > 0);
				boolean errorFound = false;
				for (ServiceError sr : validationErrorsList) {
					if (sr.getErrorCode().equals(UinGeneratorErrorCode.UIN_NOT_FOUND.getErrorCode())) {
						errorFound = true;
						break;
					}
				}
				context.assertTrue(errorFound);
				client.close();
				async.complete();
			} else {
				LOGGER.error(ar.cause().getMessage());
			}
		});
	}
}