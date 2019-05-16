package io.mosip.registration.util.restclient;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
	private boolean isPregRegSync;
	private boolean isAuthRequired;
	private String authZHeader;
	private String triggerPoint;
	private HttpHeaders httpHeaders;
	private Object requestBody;
	private Boolean isSignRequired;
	private boolean isRequestSignRequired; 
	
	/**
	 * @return the isSignRequired
	 */
	public Boolean getIsSignRequired() {
		return isSignRequired;
	}

	/**
	 * @param isSignRequired the isSignRequired to set
	 */
	public void setIsSignRequired(Boolean isSignRequired) {
		this.isSignRequired = isSignRequired;
	}

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

	public boolean isPregRegSync() {
		return isPregRegSync;
	}

	public void setPregRegSync(boolean isPregRegSync) {
		this.isPregRegSync = isPregRegSync;
	}

	public boolean isAuthRequired() {
		return isAuthRequired;
	}

	public void setAuthRequired(boolean isAuthRequired) {
		this.isAuthRequired = isAuthRequired;
	}

	public String getAuthZHeader() {
		return authZHeader;
	}

	public void setAuthZHeader(String authZHeader) {
		this.authZHeader = authZHeader;
	}

	public String getTriggerPoint() {
		return triggerPoint;
	}

	public void setTriggerPoint(String triggerPoint) {
		this.triggerPoint = triggerPoint;
	}

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(HttpHeaders httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public Object getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}

	public boolean isRequestSignRequired() {
		return isRequestSignRequired;
	}

	public void setRequestSignRequired(boolean isRequestSignRequired) {
		this.isRequestSignRequired = isRequestSignRequired;
	}

}
