package io.mosip.kernel.idrepo.config;

import static org.junit.Assert.assertTrue;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@Import(value = {IdRepoConfig.class, SwaggerConfig.class})
@WebMvcTest
@ActiveProfiles("test")
@Ignore
public class IdRepoConfigTest {
	
	@Autowired
	Environment env;

	@Autowired
	private RestTemplate restTemplate;
	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(restTemplate, "interceptor", interceptor());
	}
	
	public Interceptor interceptor() {
		return new EmptyInterceptor() {

			private static final long serialVersionUID = 1L;
			
		};
	}
	
	@Test
	public void testRestTemplateSSL() {
		ObjectNode uinObject = restTemplate.getForObject(env.getProperty("mosip.kernel.uingen.url"), ObjectNode.class);
		assertTrue(uinObject.has("uin"));
	}
}
