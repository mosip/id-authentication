package io.mosip.kernel.dataaccess.hibernate.config;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;


public class EncryptionInterceptor extends EmptyInterceptor {

	@Value("${mosip.kernel.encrypt.url:http://localhost:8088/v1/keymanager/encrypt}")
	String encryptUrl;

	@Value("${mosip.kerenl.decrypt.url:http://localhost:8088/v1/keymanager/decrypt}")
	String decryptUrl;

	/**
	 * Autowired reference for {@link #restTemplateBuilder}
	 */

	@Autowired
	RestTemplate restTemplate;

	private List<String> reqParams;

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		reqParams = new ArrayList<>();
		return doSaveOrloadAction(entity, state, propertyNames, types, encryptUrl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onLoad(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		reqParams = new ArrayList<>();
		return doSaveOrloadAction(entity, state, propertyNames, types, decryptUrl);
	}

	private boolean doSaveOrloadAction(Object entity, Object[] state, String[] propertyNames, Type[] types,
			String url) {
//		try {
//			reqParams = new ArrayList<>();
//
//			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(url);
//			HttpHeaders headers = new HttpHeaders();
//			Map<String, Object> params = new HashMap<>();
//			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//			HttpEntity<RequestWrapper<String>> en = new HttpEntity<>(headers);
//			String uriBuilder = regbuilder.build().encode().toUriString();
//
//			Field[] fields = entity.getClass().getDeclaredFields();
//			for (Field field : fields) {
//				if (field.isAnnotationPresent(Encrypted.class)) {
//					System.out.println("field name  " + field.getName());
//					reqParams.add(field.getName());
//
//				}
//			}
//			for (int i = 0; i < propertyNames.length; i++) {
//				if (reqParams.contains(propertyNames[i])) {
//					System.out.println("Value " + state[i]);
//					uriBuilder += "/{data}";
//					params.put("data", state[i]);
//					ResponseEntity<ResponseWrapper<String>> responseEntity = restTemplate.exchange(uriBuilder,
//							HttpMethod.GET, en, new ParameterizedTypeReference<ResponseWrapper<String>>() {
//							}, params);
//					if (responseEntity.getBody().getErrors() != null
//							&& !responseEntity.getBody().getErrors().isEmpty()) {
//						// error
//					}
//					state[i] = responseEntity.getBody().getResponse();
//				}
//			}
//
//			return true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return true;
	}
}
