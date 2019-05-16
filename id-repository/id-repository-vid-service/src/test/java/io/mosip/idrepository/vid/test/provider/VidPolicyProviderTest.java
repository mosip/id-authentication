package io.mosip.idrepository.vid.test.provider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import io.mosip.idrepository.vid.provider.VidPolicyProvider;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
public class VidPolicyProviderTest {

	@Autowired
	private Environment env;

	@Autowired
	private ObjectMapper mapper;

	@InjectMocks
	private VidPolicyProvider policyProvider;

	@Before
	public void setup() {
		ReflectionTestUtils.setField(policyProvider, "env", env);
		ReflectionTestUtils.setField(policyProvider, "mapper", mapper);
	}

	@Test
	public void testPolicyDetails() throws IOException, URISyntaxException {
		policyProvider.policyDetails();
		assertTrue(policyProvider.getPolicy("Perpetual").getAutoRestoreAllowed());
		assertFalse(policyProvider.getPolicy("Temporary").getAutoRestoreAllowed());
		assertTrue(policyProvider.getAllVidTypes().containsAll(Lists.newArrayList("Perpetual", "Temporary")));
	}
}
