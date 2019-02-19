package io.mosip.kernel.idrepo.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.idrepo.controller.IdRepoController;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
//@Import(IdRepoFilter.class)
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
	public void testWithPathVariable() throws Exception {
		mockMvc.perform(get("/v1.0/identity").param("uin", "1234")).andExpect(status().isOk());
	}
	
	@Test
	public void testCharResponseWrapper() throws IOException {
		CharResponseWrapper responseWrapper = new CharResponseWrapper(response);
		responseWrapper.getWriter();
		ServletOutputStream outputStream = responseWrapper.getOutputStream();
		assertTrue(outputStream.isReady());
		outputStream.close();
		assertFalse(outputStream.isReady());
	}
	
	@Test
	public void testResettableStreamHttpServletRequest() throws IOException {
		ResettableStreamHttpServletRequest requestWrapper = new ResettableStreamHttpServletRequest(request);
		requestWrapper.getReader();
	}
	
}
