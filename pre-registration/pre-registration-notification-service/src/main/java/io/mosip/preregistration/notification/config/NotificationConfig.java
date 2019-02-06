package io.mosip.preregistration.notification.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configuration class.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 */
@Configuration
@EnableSwagger2
public class NotificationConfig {
	/**
	 * Reference for ${application.env.local:false} from property file.
	 */
	@Value("${application.env.local:false}")
	private Boolean localEnv;

	/**
	 * Reference for ${swagger.base-url:#{null}} from property file.
	 */
	@Value("${swagger.base-url:#{null}}")
	private String swaggerBaseUrl;

	/**
	 * Reference for ${server.port:9099} from property file.
	 */
	@Value("${server.port:9099}")
	private int serverPort;

	/**
	 * To define Protocol
	 */
	String proto = "http";

	/**
	 * To define Host
	 */
	String host = "localhost";

	/**
	 * To define port
	 */
	int port = -1;

	/**
	 * To define host along with the port
	 */
	String hostWithPort = "localhost:9099";

	/**
	 * To configure Host and port along with docket.
	 * 
	 * @return Docket docket
	 */
	@Bean
	public Docket registrationStatusBean() {

		boolean swaggerBaseUrlSet = false;
		if (!localEnv && swaggerBaseUrl != null && !swaggerBaseUrl.isEmpty()) {
			try {
				proto = new URL(swaggerBaseUrl).getProtocol();
				host = new URL(swaggerBaseUrl).getHost();
				port = new URL(swaggerBaseUrl).getPort();
				if (port == -1) {
					hostWithPort = host;
				} else {
					hostWithPort = host + ":" + port;
				}
				swaggerBaseUrlSet = true;
			} catch (MalformedURLException e) {
				System.err.println("SwaggerUrlException: " + e);
			}
		}

		Docket docket = new Docket(DocumentationType.SWAGGER_2).groupName("Pre-Registration").select()
				.apis(RequestHandlerSelectors.basePackage("io.mosip.preregistration.notification.controller"))
				.paths(PathSelectors.ant("/v0.1/pre-registration/*")).build();

		if (swaggerBaseUrlSet) {
			docket.protocols(protocols()).host(hostWithPort);
			System.out.println("\nSwagger Base URL: " + proto + "://" + hostWithPort + "\n");
		}
		return docket;
	}

	/**
	 * @return set or protocols
	 */
	private Set<String> protocols() {
		Set<String> protocols = new HashSet<>();
		protocols.add(proto);
		return protocols;
	}

	@Bean
	public RestTemplate restTemplateConfig()
			throws KeyManagementException, java.security.NoSuchAlgorithmException, KeyStoreException {

		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);

	}

}
