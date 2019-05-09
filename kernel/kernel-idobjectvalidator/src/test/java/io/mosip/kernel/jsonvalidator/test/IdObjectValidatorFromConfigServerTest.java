package io.mosip.kernel.jsonvalidator.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;

import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.HttpRequestException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;
import io.mosip.kernel.core.idobjectvalidator.exception.NullJsonNodeException;
import io.mosip.kernel.core.idobjectvalidator.exception.UnidentifiedJsonException;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectSchemaValidator;

/**
 * 
 * @author Swati Raj
 * @author Manoj SP
 * @since 1.0.0
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonLoader.class })
@ActiveProfiles("test")
public class IdObjectValidatorFromConfigServerTest {

	String propertySourceString = "propertySource";
	String configServerFileStorageURLString = "configServerFileStorageURL";

	@InjectMocks
	IdObjectSchemaValidator idValidator;

	@Before
	public void before() throws IOException {
		MockitoAnnotations.initMocks(this);
		JsonNode schema = JsonLoader.fromResource("/schemaV1.json");
		PowerMockito.mockStatic(JsonLoader.class);
		PowerMockito.when(JsonLoader.fromString(Mockito.any())).thenReturn(new ObjectMapper().createObjectNode());
		PowerMockito.when(JsonLoader.fromURL(Mockito.any())).thenReturn(schema);
		PowerMockito.when(JsonLoader.class.getResource(Mockito.anyString())).thenCallRealMethod();
		PowerMockito.when(JsonLoader.class.getClassLoader()).thenCallRealMethod();
		PowerMockito.when(JsonLoader.fromResource(Mockito.anyString())).thenCallRealMethod();
		ReflectionTestUtils.setField(idValidator, "propertySource", "CONFIG_SERVER");
		ReflectionTestUtils.setField(idValidator, "configServerFileStorageURL", "http://1.1.1.1:51000/");
		ReflectionTestUtils.setField(idValidator, "schemaName", "schemaName");
	}

	@Test
	public void testWhenValidJsonProvided() throws HttpRequestException, IdObjectValidationProcessingException, IOException,
			IdObjectIOException, IdObjectSchemaIOException, FileIOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		JsonLoader.fromString("");
		assertEquals(true, idValidator.validateIdObject(jsonSchemaNode));
	}

	@Test(expected = NullJsonNodeException.class)
	@Ignore
	public void testForEmptyJsonString() throws IdObjectValidationProcessingException, HttpRequestException,
			IdObjectIOException, IdObjectSchemaIOException, FileIOException {
		String jsonString = "";
		idValidator.validateIdObject(new ObjectMapper().createObjectNode());

	}

	@Test(expected = IdObjectIOException.class)
	@Ignore
	public void testForinvalidJsonString() throws HttpRequestException, IdObjectValidationProcessingException,
			IdObjectIOException, IdObjectSchemaIOException, FileIOException {
		ObjectNode objectNode = new ObjectMapper().createObjectNode();
		objectNode.put("", new byte[] { 0 });
		idValidator.validateIdObject(objectNode);
	}

	@Test(expected = UnidentifiedJsonException.class)
	public void testForUnidentifiedJson() throws HttpRequestException, IdObjectValidationProcessingException,
			IdObjectIOException, IOException, IdObjectSchemaIOException, FileIOException {
		JsonLoader.fromString("");
		ObjectNode objectNode = new ObjectMapper().createObjectNode();
		objectNode.put("", new byte[] { 0 });
		idValidator.validateIdObject(objectNode);
	}

	@Test(expected = IdObjectSchemaIOException.class)
	public void testForNullJsonSchemaSyntax() throws HttpRequestException, IdObjectValidationProcessingException,
			IdObjectIOException, IdObjectSchemaIOException, FileIOException, IOException {
		JsonNode jsonSchemaNode = JsonLoader.fromResource("/valid-json.json");
		String jsonString = jsonSchemaNode.toString();
		JsonLoader.fromString("");
		PowerMockito.when(JsonLoader.fromURL(Mockito.any())).thenThrow(new FileNotFoundException(""));
		idValidator.validateIdObject(new ObjectMapper().createObjectNode());
	}

}