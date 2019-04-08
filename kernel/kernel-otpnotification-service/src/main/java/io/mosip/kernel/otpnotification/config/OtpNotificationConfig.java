package io.mosip.kernel.otpnotification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.core.templatemanager.spi.TemplateManagerBuilder;

/**
 * Configuration class for Otp notification.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Configuration
public class OtpNotificationConfig {
//	@Bean
//	public RestTemplate restTemplateConfig()
//			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
//		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
//		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
//				.build();
//		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
//		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//		requestFactory.setHttpClient(httpClient);
//		return new RestTemplate(requestFactory);
//	}

	@Bean
	public TemplateManager templateManager(TemplateManagerBuilder templateManagerBuilder) {
		return templateManagerBuilder.build();
	}
}
