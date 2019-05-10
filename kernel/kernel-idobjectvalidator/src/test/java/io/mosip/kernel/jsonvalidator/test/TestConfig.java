package io.mosip.kernel.jsonvalidator.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * @author Manoj SP
 *
 */
@Configuration
public class TestConfig {
	
	ObjectMapper mapper = new ObjectMapper();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public RestTemplate restTemplate() {
		mapper.registerModule(new Jdk8Module())
		   .registerModule(new JavaTimeModule());
		RestTemplate restTemplate = mock(RestTemplate.class);
		mockLangResponse(restTemplate);
		ResponseWrapper<ObjectNode> errResponse = new ResponseWrapper<>();
		errResponse.setErrors(Collections.singletonList(new ServiceError("", "")));
		when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(),
				Mockito.any(ParameterizedTypeReference.class)))
						.thenReturn(new ResponseEntity(errResponse, HttpStatus.OK));
		when(restTemplate.getForObject("https://0.0.0.0/gendertypes", ResponseWrapper.class)).thenReturn(errResponse);
		when(restTemplate.getForObject(Mockito.matches("^((?!language).)*$"),
				Mockito.any(Class.class))).thenReturn(errResponse);
		return restTemplate;
	}

	@SuppressWarnings("rawtypes")
	private void mockLangResponse(RestTemplate restTemplate) {
		ResponseWrapper<Map> response = new ResponseWrapper<>();
		ObjectNode langResponse1 = mapper.createObjectNode();
		langResponse1.put("isActive", true);
		langResponse1.put("code", "eng");
		ObjectNode langResponse2 = mapper.createObjectNode();
		langResponse2.put("isActive", true);
		langResponse2.put("code", "ara");
		response.setResponse(Collections.singletonMap("languages", Lists.newArrayList(langResponse1, langResponse2)));
		when(restTemplate.getForObject("https://0.0.0.0/language", ObjectNode.class))
				.thenReturn(mapper.convertValue(response, ObjectNode.class));
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return mapper;
	}
}
