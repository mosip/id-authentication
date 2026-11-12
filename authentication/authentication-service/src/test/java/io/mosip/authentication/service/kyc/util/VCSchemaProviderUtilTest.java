package io.mosip.authentication.service.kyc.util;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class VCSchemaProviderUtilTest {

    @InjectMocks
    private VCSchemaProviderUtil vcSchemaProviderUtil;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetVCContextSchema()  {
        String configServerFileStorageUrl = "http://example.com";
        String uri = "vc-context-schema.json";
        String vcContextJson = "{\"vc\": \"context\"}";
        JsonDocument expectedJsonDocument = null;
        try {
            expectedJsonDocument = JsonDocument.of(new StringReader(vcContextJson));
        } catch (JsonLdError e) {
            throw new RuntimeException(e);
        }

        Mockito.when(restTemplate.getForObject(configServerFileStorageUrl + uri, String.class))
                .thenReturn(vcContextJson);
        JsonDocument result = vcSchemaProviderUtil.getVCContextSchema(configServerFileStorageUrl, uri);
        Assertions.assertEquals(expectedJsonDocument.getJsonContent(), result.getJsonContent());
        Mockito.verify(restTemplate).getForObject(configServerFileStorageUrl + uri, String.class);
    }

    @Test
    public void testGetVCContextSchema_throwsException()  {
        String configServerFileStorageUrl = "http://example.com";
        String uri = "vc-context-schema.json";
        String vcContextJson = "";
        Mockito.when(restTemplate.getForObject(configServerFileStorageUrl + uri, String.class))
                .thenReturn(vcContextJson);
        Assertions.assertThrows(IdAuthUncheckedException.class,()->vcSchemaProviderUtil.getVCContextSchema(configServerFileStorageUrl, uri));
    }

    @Test
    public void testGetVCContextData() throws IdAuthenticationBusinessException {
        String configServerFileStorageUrl = "http://example.com";
        String uri = "/vc-context-data.json";
        String vcContextData = "{\"vc\": \"data\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedMap;

        try {
            expectedMap = objectMapper.readValue(vcContextData, new TypeReference<Map<String,Object>>(){});
        } catch (IOException e) {
            Assertions.fail("Error parsing JSON: " + e.getMessage());
            return;
        }

        JSONObject expectedJsonObject = new JSONObject(expectedMap);
        Mockito.when(restTemplate.getForObject(configServerFileStorageUrl + uri, String.class))
                .thenReturn(vcContextData);
        JSONObject result = vcSchemaProviderUtil.getVCContextData(configServerFileStorageUrl, uri, objectMapper);
        Assertions.assertEquals(expectedJsonObject, result);
        Mockito.verify(restTemplate).getForObject(configServerFileStorageUrl + uri, String.class);
    }

    @Test
    public void testGetVCContextData_throwsException() throws IdAuthenticationBusinessException{
        String configServerFileStorageUrl = "http://example.com";
        String uri = "/vc-context-data.json";
        String vcContextData = "";
        Mockito.when(restTemplate.getForObject(configServerFileStorageUrl + uri, String.class))
                .thenReturn(vcContextData);
        Assertions.assertThrows(IdAuthenticationBusinessException.class,()->vcSchemaProviderUtil.getVCContextData(configServerFileStorageUrl,uri, new ObjectMapper()));
    }

}