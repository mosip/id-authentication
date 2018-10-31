package io.mosip.registration.util.restclient;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

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

}
