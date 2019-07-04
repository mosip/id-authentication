package io.mosip.idrepository.vid.test.provider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.dto.VidPolicy;
import io.mosip.idrepository.vid.provider.VidPolicyProvider;

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
public class VidPolicyProviderTest {

	@Autowired
	private Environment env;

	@Mock
	private ObjectMapper mapper;
	
	@InjectMocks
	private VidPolicyProvider policyProvider;

	@Before
	public void setup() {
		ReflectionTestUtils.setField(policyProvider, "env", env);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPolicyDetails() throws IOException, ProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode policy = objectMapper
				.readValue(this.getClass().getClassLoader().getResource("vid_policy.json"), ObjectNode.class);
		List<Object> vidPolicy = JsonPath.compile(IdRepoConstants.VID_POLICY_PATH.getValue())
				.read(policy.toString(), Configuration.defaultConfiguration()
						.addOptions(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST));
		when(mapper.readValue(Mockito.any(URL.class), Mockito.any(Class.class))).thenReturn(policy);
		when(mapper.convertValue(vidPolicy.get(0), VidPolicy.class))
				.thenReturn(objectMapper.convertValue(vidPolicy.get(0), VidPolicy.class));
		when(mapper.convertValue(vidPolicy.get(1), VidPolicy.class))
		.thenReturn(objectMapper.convertValue(vidPolicy.get(1), VidPolicy.class));
		policyProvider.loadPolicyDetails();
		assertTrue(policyProvider.getPolicy("Perpetual").getAutoRestoreAllowed());
		assertFalse(policyProvider.getPolicy("Temporary").getAutoRestoreAllowed());
		assertTrue(policyProvider.getAllVidTypes().containsAll(Lists.newArrayList("Perpetual", "Temporary")));
	}
}
