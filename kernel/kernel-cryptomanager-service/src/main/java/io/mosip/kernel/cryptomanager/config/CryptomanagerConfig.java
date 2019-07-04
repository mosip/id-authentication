package io.mosip.kernel.cryptomanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyRequestDto;
import io.mosip.kernel.datamapper.orika.builder.DataMapperBuilderImpl;

/**
 * Configuration class for Crypto-Manager-Service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Configuration
public class CryptomanagerConfig {

	/*
	 * @Bean public RestTemplate restTemplateConfig() throws KeyManagementException,
	 * NoSuchAlgorithmException, KeyStoreException {
	 * 
	 * TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String
	 * authType) -> true;
	 * 
	 * SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
	 * .loadTrustMaterial(null, acceptingTrustStrategy).build();
	 * 
	 * SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
	 * 
	 * CloseableHttpClient httpClient =
	 * HttpClients.custom().setSSLSocketFactory(csf).build();
	 * HttpComponentsClientHttpRequestFactory requestFactory = new
	 * HttpComponentsClientHttpRequestFactory();
	 * 
	 * requestFactory.setHttpClient(httpClient); return new
	 * RestTemplate(requestFactory);
	 * 
	 * }
	 */

	@Bean
	public DataMapper<CryptomanagerRequestDto, KeymanagerSymmetricKeyRequestDto> datamapper() {
		return new DataMapperBuilderImpl<>(CryptomanagerRequestDto.class, KeymanagerSymmetricKeyRequestDto.class)
				.build();
	}
}
