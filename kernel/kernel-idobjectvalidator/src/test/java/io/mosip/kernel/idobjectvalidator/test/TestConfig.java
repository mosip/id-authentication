package io.mosip.kernel.idobjectvalidator.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * @author Manoj SP
 *
 */
@Configuration
public class TestConfig {

	ObjectMapper mapper = new ObjectMapper();

	@Bean
	public RestTemplate restTemplate()
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		mapper.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
		RestTemplate restTemplate = mock(RestTemplate.class);
		mockLangResponse(restTemplate);
		mockGenderResponse(restTemplate);
		mockLocationResponse(restTemplate);
		mockLocationHierarchyLAAResponse(restTemplate);
		mockLocationHierarchyProvinceResponse(restTemplate);
		mockLocationHierarchyRegionResponse(restTemplate);
		mockLocationHierarchyPostalCodeResponse(restTemplate);
		mockLocationHierarchyCityResponse(restTemplate);
		mockDocumentCategoriesResponse(restTemplate);
		mockDocumentTypesResponse(restTemplate);
		return restTemplate;
	}

	private void mockLangResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-21T05:37:06.663Z\",\"metadata\":null,\"response\":{\"languages\":[{\"code\":\"eng\",\"name\":\"English\",\"family\":\"Indo-European\",\"nativeName\":\"English\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/language", ObjectNode.class))
				.thenReturn(mapper.readValue(response.getBytes(), ObjectNode.class));
	}

	private void mockGenderResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-21T05:37:06.813Z\",\"metadata\":null,\"response\":{\"genderType\":[{\"code\":\"MLE\",\"genderName\":\"Male\",\"langCode\":\"eng\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/gendertypes", ResponseWrapper.class))
				.thenReturn(mapper.readValue(response.getBytes(), ResponseWrapper.class));
	}

	private void mockLocationResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-21T05:37:07.582Z\",\"metadata\":null,\"response\":{\"locations\":[{\"locationHierarchylevel\":3,\"locationHierarchyName\":\"City\",\"isActive\":true},{\"locationHierarchylevel\":1,\"locationHierarchyName\":\"Region\",\"isActive\":true},{\"locationHierarchylevel\":2,\"locationHierarchyName\":\"Province\",\"isActive\":true},{\"locationHierarchylevel\":4,\"locationHierarchyName\":\"Local Administrative Authority\",\"isActive\":true},{\"locationHierarchylevel\":5,\"locationHierarchyName\":\"Postal Code\",\"isActive\":true}]},\"errors\":null}";
		ResponseWrapper<ObjectNode> responseWrapper = mapper.readValue(response.getBytes(),
				new TypeReference<ResponseWrapper<ObjectNode>>() {
				});
		when(restTemplate.exchange("https://0.0.0.0/locations/eng", HttpMethod.GET, null,
				new ParameterizedTypeReference<ResponseWrapper<ObjectNode>>() {
				})).thenReturn(new ResponseEntity<ResponseWrapper<ObjectNode>>(responseWrapper, HttpStatus.OK));
	}

	private void mockLocationHierarchyLAAResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-21T05:37:08.486Z\",\"metadata\":null,\"response\":{\"locations\":[{\"code\":\"MOGR\",\"name\":\"Mograne\",\"hierarchyLevel\":0,\"hierarchyName\":\"Local Administrative Authority\",\"parentLocCode\":\"KNT\",\"langCode\":\"eng\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/locationhierarchy/Local Administrative Authority",
				ResponseWrapper.class)).thenReturn(mapper.readValue(response.getBytes(), ResponseWrapper.class));
	}

	private void mockLocationHierarchyProvinceResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-21T05:37:09.945Z\",\"metadata\":null,\"response\":{\"locations\":[{\"code\":\"KTA\",\"name\":\"Kenitra\",\"hierarchyLevel\":0,\"hierarchyName\":\"Province\",\"parentLocCode\":\"RSK\",\"langCode\":\"eng\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/locationhierarchy/Province", ResponseWrapper.class))
				.thenReturn(mapper.readValue(response.getBytes(), ResponseWrapper.class));
	}

	private void mockLocationHierarchyRegionResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-22T06:49:50.975Z\",\"metadata\":null,\"response\":{\"locations\":[{\"code\":\"RSK\",\"name\":\"Rabat Sale Kenitra\",\"hierarchyLevel\":0,\"hierarchyName\":\"Region\",\"parentLocCode\":\"MOR\",\"langCode\":\"eng\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/locationhierarchy/Region", ResponseWrapper.class))
				.thenReturn(mapper.readValue(response.getBytes(), ResponseWrapper.class));
	}

	private void mockLocationHierarchyPostalCodeResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-22T06:49:50.818Z\",\"metadata\":null,\"response\":{\"locations\":[{\"code\":\"10112\",\"name\":\"10112\",\"hierarchyLevel\":0,\"hierarchyName\":\"Postal Code\",\"parentLocCode\":\"BNMR\",\"langCode\":\"eng\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/locationhierarchy/Postal Code", ResponseWrapper.class))
				.thenReturn(mapper.readValue(response.getBytes(), ResponseWrapper.class));
	}

	private void mockLocationHierarchyCityResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-22T06:49:51.552Z\",\"metadata\":null,\"response\":{\"locations\":[{\"code\":\"KNT\",\"name\":\"Kenitra\",\"hierarchyLevel\":0,\"hierarchyName\":\"City\",\"parentLocCode\":\"KTA\",\"langCode\":\"eng\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/locationhierarchy/City", ResponseWrapper.class))
				.thenReturn(mapper.readValue(response.getBytes(), ResponseWrapper.class));
	}

	private void mockDocumentCategoriesResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-21T05:37:10.030Z\",\"metadata\":null,\"response\":{\"documentcategories\":[{\"code\":\"POI\",\"name\":\"Proof of Identity\",\"description\":\"Identity Proof\",\"langCode\":\"eng\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/documentcategories", ResponseWrapper.class))
				.thenReturn(mapper.readValue(response.getBytes(), ResponseWrapper.class));
	}

	private void mockDocumentTypesResponse(RestTemplate restTemplate)
			throws RestClientException, JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":null,\"version\":null,\"responsetime\":\"2019-05-21T05:37:16.097Z\",\"metadata\":null,\"response\":{\"documents\":[{\"code\":\"DOC001\",\"name\":\"Passport\",\"description\":\"Proof of Idendity\",\"langCode\":\"eng\",\"isActive\":true}]},\"errors\":null}";
		when(restTemplate.getForObject("https://0.0.0.0/documenttypes", ResponseWrapper.class))
				.thenReturn(mapper.readValue(response.getBytes(), ResponseWrapper.class));
	}

	@Bean
	public ObjectMapper objectMapper() {
		return mapper;
	}
}
