package io.mosip.kernel.idrepo.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.idrepo.controller.IdRepoController;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
@ConfigurationProperties("mosip.kernel.idrepo")
public class IdRepoFilterTest {

	@Autowired
	MockMvc mockMvc;

	@InjectMocks
	IdRepoFilter filter;
	
	@Autowired
	private ObjectMapper mapper;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	@Mock
	HttpServletResponse response;
	
	@Mock
	HttpServletRequest request;
	
	@Mock
	InputStream inputStream;
	
	private Map<String, String> id;
	
	public Map<String, String> getId() {
		return id;
	}

	public void setId(Map<String, String> id) {
		this.id = id;
	}
	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(filter, "mapper", mapper);
		ReflectionTestUtils.setField(filter, "env", env);
		ReflectionTestUtils.setField(filter, "id", id);
		mockMvc = MockMvcBuilders.standaloneSetup(IdRepoController.class).addFilters(filter).build();
	}

	@Test
	public void testWithInvalidPathVariable() throws Exception {
		mockMvc.perform(get("/v1.0/identity").param("uin", "1234")).andExpect(status().isOk());
	}
	
	@Test
	public void testWithValidPathVariable() throws Exception {
		when(response.getStatus()).thenReturn(200);
		mockMvc.perform(get("/v1.0/identity").param("type", "1234")).andExpect(status().is4xxClientError());
	}
	
	@Test
	public void testWithUrlShouldNotFilter() throws Exception {
		when(response.getStatus()).thenReturn(200);
		mockMvc.perform(get("").param("type", "1234")).andExpect(status().is4xxClientError());
	}
	
	@Test
	public void testCharResponseWrapper() throws IOException {
		CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
		responseWrapper.getOutputStream().write(0);
		responseWrapper.getWriter().write(0);
		responseWrapper.getOutputStream().setWriteListener(null);
		ServletOutputStream outputStream = responseWrapper.getOutputStream();
		outputStream.toString();
		assertTrue(outputStream.isReady());
		outputStream.close();
		assertFalse(outputStream.isReady());
	}
	
	@Test
	public void testResettableStreamHttpServletRequest() throws IOException {
		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(request);
		requestWrapper.getReader();
		requestWrapper.getInputStream().isFinished();
		requestWrapper.getInputStream().isReady();
		requestWrapper.getInputStream().setReadListener(null);
		requestWrapper.replaceData(new byte[] { 0 });
		requestWrapper.getInputStream().close();
		requestWrapper.getInputStream().isReady();
	}
	
	@Test(expected = IdRepoAppUncheckedException.class)
	public void buildErrorResponseError() throws Throwable {
		try {
			ObjectMapper mockMapper = mock(ObjectMapper.class);
			when(mockMapper.writeValueAsString(Mockito.any())).thenThrow(new UnrecognizedPropertyException("", null, getClass(), "", null));
			ReflectionTestUtils.setField(filter, "mapper", mockMapper);
			ReflectionTestUtils.invokeMethod(filter, "buildErrorResponse");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
}

