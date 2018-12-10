package io.mosip.registration.util.restclient;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * This class holds the request of http data
 * @author Yaswanth S
 * @since 1.0.0
 */
public class RequestHTTPDTO {
	private HttpMethod httpMethod;
	private HttpEntity<?> httpEntity;
	private Class<?> clazz;
	private URI uri;
	private SimpleClientHttpRequestFactory simpleClientHttpRequestFactory;

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public HttpEntity<?> getHttpEntity() {
		return httpEntity;
	}

	public void setHttpEntity(HttpEntity<?> httpEntity) {
		this.httpEntity = httpEntity;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
		return simpleClientHttpRequestFactory;
	}

	public void setSimpleClientHttpRequestFactory(SimpleClientHttpRequestFactory simpleClientHttpRequestFactory) {
		this.simpleClientHttpRequestFactory = simpleClientHttpRequestFactory;
	}	

}
