package com.test.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.demo.authentication.service.impl.indauth.controller.FingerPrint;
import io.mosip.demo.authentication.service.impl.indauth.controller.IdRepo;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.model.ValidationReport;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;

/**
 * @author Arun Bose S
 * The Class IdRepoTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdRepoTest {

	
	/** The id repo mock. */
	@InjectMocks
	private IdRepo idRepoMock;
	
	/** The json validator. */
	@Mock
	private JsonValidator jsonValidator;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(idRepoMock, "jsonValidator", jsonValidator);
	}
	
	/**
	 * Encode file test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void encodeFileTest() throws IOException {
		idRepoMock.encodeFile(new MultipartFile() {
			
			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public long getSize() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getOriginalFilename() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public InputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public byte[] getBytes() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
	}
	
	/**
	 * Decode file test.
	 */
	@Test
	public void decodeFileTest() {
		idRepoMock.decodeToFile("Sample", "sample");
	}
	
	/**
	 * Json validate success.
	 *
	 * @throws JsonValidationProcessingException the json validation processing exception
	 * @throws JsonIOException the json IO exception
	 * @throws JsonSchemaIOException the json schema IO exception
	 * @throws FileIOException the file IO exception
	 */
	@Test
	public void jsonValidateSuccess() throws JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ValidationReport report = Mockito.mock(ValidationReport.class);
		Mockito.when(report.isValid()).thenReturn(true);
		Mockito.when(jsonValidator.validateJson(Mockito.anyString(),Mockito.anyString())).thenReturn(report);
		ObjectNode objectNode=new ObjectNode(null);
		idRepoMock.jsonSchemaValidator(objectNode);
	}
	
	/**
	 * Json validate failure.
	 *
	 * @throws JsonValidationProcessingException the json validation processing exception
	 * @throws JsonIOException the json IO exception
	 * @throws JsonSchemaIOException the json schema IO exception
	 * @throws FileIOException the file IO exception
	 */
	@Test
	public void jsonValidateFailure() throws JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ValidationReport report = Mockito.mock(ValidationReport.class);
		Mockito.when(report.isValid()).thenReturn(false);
		Mockito.when(jsonValidator.validateJson(Mockito.anyString(),Mockito.anyString())).thenReturn(report);
		ObjectNode objectNode=new ObjectNode(null);
		idRepoMock.jsonSchemaValidator(objectNode);
	}
	
	/**
	 * Json validate exception.
	 *
	 * @throws JsonValidationProcessingException the json validation processing exception
	 * @throws JsonIOException the json IO exception
	 * @throws JsonSchemaIOException the json schema IO exception
	 * @throws FileIOException the file IO exception
	 */
	@Test
	public void jsonValidateException() throws JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		ValidationReport report = Mockito.mock(ValidationReport.class);
		Mockito.when(report.isValid()).thenReturn(false);
		Mockito.when(jsonValidator.validateJson(Mockito.anyString(),Mockito.anyString())).thenThrow(JsonSchemaIOException.class);
		ObjectNode objectNode=new ObjectNode(null);
		assertNull(idRepoMock.jsonSchemaValidator(objectNode));
	}
	
	/**
	 * Multi part resolver test.
	 *
	 * @throws JsonValidationProcessingException the json validation processing exception
	 * @throws JsonIOException the json IO exception
	 * @throws JsonSchemaIOException the json schema IO exception
	 * @throws FileIOException the file IO exception
	 */
	@Test
	public void multiPartResolverTest() throws JsonValidationProcessingException, JsonIOException, JsonSchemaIOException, FileIOException {
		idRepoMock.multipartResolver();
	}
}
