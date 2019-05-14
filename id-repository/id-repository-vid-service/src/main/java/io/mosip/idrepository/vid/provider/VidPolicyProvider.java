package io.mosip.idrepository.vid.provider;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.mosip.idrepository.vid.dto.VidPolicy;

/**
 * @author Manoj SP
 *
 */
@Component
public class VidPolicyProvider {
	
	private static final Configuration READ_LIST_OPTIONS = Configuration.defaultConfiguration()
			.addOptions(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private ObjectMapper mapper;
	
	private Map<String, VidPolicy> vidPolicies;
	
	@PostConstruct
	public void policyDetails() throws IOException, URISyntaxException {
//		FileReader jsonFile = new FileReader(
//				new File(new URL(env.getProperty("mosip.idrepo.vid.policy-file-location")).toURI()));
		JsonNode policyJson = mapper.readValue(
				this.getClass().getClassLoader().getResource("vid_policy.json"), JsonNode.class);
		List<String> vidType = JsonPath.compile("vidPolicies.*.vidType").read(policyJson.toString(), READ_LIST_OPTIONS);
		List<Object> vidPolicy = JsonPath.compile("vidPolicies.*.vidPolicy").read(policyJson.toString(),
				READ_LIST_OPTIONS);
		vidPolicies = IntStream.range(0, vidType.size()).parallel()
			.boxed()
			.collect(Collectors.toMap(i -> vidType.get(i),
					i -> mapper.convertValue(vidPolicy.get(i), VidPolicy.class)));
	}
	
	public VidPolicy getPolicy(String vidType) {
		return vidPolicies.get(vidType);
	}
}
