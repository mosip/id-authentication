package io.mosip.authentication.service.kyc.util;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class VCSchemaProviderUtilTest {

    @InjectMocks
    private VCSchemaProviderUtil vcSchemaProviderUtil;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        Assert.assertEquals(expectedJsonDocument.getJsonContent(), result.getJsonContent());
        Mockito.verify(restTemplate).getForObject(configServerFileStorageUrl + uri, String.class);
    }

    @Test
    public void testGetVCContextSchema_throwsException()  {
        String configServerFileStorageUrl = "http://example.com";
        String uri = "vc-context-schema.json";
        String vcContextJson = "";
        Mockito.when(restTemplate.getForObject(configServerFileStorageUrl + uri, String.class))
                .thenReturn(vcContextJson);
        Assert.assertThrows(IdAuthUncheckedException.class,()->vcSchemaProviderUtil.getVCContextSchema(configServerFileStorageUrl, uri));
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
            Assert.fail("Error parsing JSON: " + e.getMessage());
            return;
        }

        JSONObject expectedJsonObject = new JSONObject(expectedMap);
        Mockito.when(restTemplate.getForObject(configServerFileStorageUrl + uri, String.class))
                .thenReturn(vcContextData);
        JSONObject result = vcSchemaProviderUtil.getVCContextData(configServerFileStorageUrl, uri, objectMapper);
        Assert.assertEquals(expectedJsonObject, result);
        Mockito.verify(restTemplate).getForObject(configServerFileStorageUrl + uri, String.class);
    }

    @Test
    public void testGetVCContextData_throwsException() throws IdAuthenticationBusinessException{
        String configServerFileStorageUrl = "http://example.com";
        String uri = "/vc-context-data.json";
        String vcContextData = "";
        Mockito.when(restTemplate.getForObject(configServerFileStorageUrl + uri, String.class))
                .thenReturn(vcContextData);
        Assert.assertThrows(IdAuthenticationBusinessException.class,()->vcSchemaProviderUtil.getVCContextData(configServerFileStorageUrl,uri, new ObjectMapper()));
    }

}