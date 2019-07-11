package io.mosip.kernel.auth.adapter.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import io.mosip.kernel.auth.adapter.constant.AuthAdapterConstant;
import io.mosip.kernel.auth.adapter.model.AuthUserDetails;

@Configuration
public class BeanConfig {

	@Bean
	public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
//		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
//		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
//				.build();
//		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
//		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//		requestFactory.setHttpClient(httpClient);		
//		RestTemplate restTemplate = new RestTemplate(requestFactory);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(Collections.singletonList(new RestTemplateInterceptor()));
		return restTemplate;
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder().filter((req, next) -> {
			ClientRequest filtered = null;
			if (SecurityContextHolder.getContext() != null
					&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
					&& SecurityContextHolder.getContext().getAuthentication()
							.getPrincipal() instanceof AuthUserDetails) {
				AuthUserDetails userDetail = (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal();
				filtered = ClientRequest.from(req).header(AuthAdapterConstant.AUTH_HEADER_COOKIE,
						AuthAdapterConstant.AUTH_COOOKIE_HEADER + userDetail.getToken()).build();
			}
			return next.exchange(filtered);
		}).build();
	}
}
