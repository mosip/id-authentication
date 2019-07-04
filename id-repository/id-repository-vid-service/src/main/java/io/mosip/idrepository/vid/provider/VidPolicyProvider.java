package io.mosip.idrepository.vid.provider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.dto.VidPolicy;

/**
 * The Class VidPolicyProvider - Provider class to load policy from policy json
 * and provide the vid policy details based on vid type.
 *
 * @author Manoj SP
 */
@Component
@RefreshScope
public class VidPolicyProvider {

	/** The Constant READ_LIST_OPTIONS. */
	private static final Configuration READ_LIST_OPTIONS = Configuration.defaultConfiguration()
			.addOptions(Option.SUPPRESS_EXCEPTIONS, Option.ALWAYS_RETURN_LIST);

	/** The env. */
	@Autowired
	private Environment env;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The vid policies. */
	private Map<String, VidPolicy> vidPolicies;

	/**
	 * Loads policy details from policy json and validates against the schema provided.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ProcessingException schema validation processing exception
	 */
	@PostConstruct
	public void loadPolicyDetails() throws IOException, ProcessingException {
		JsonNode policyJson = mapper.readValue(new URL(env.getProperty(IdRepoConstants.VID_POLICY_FILE_URL.getValue())),
				JsonNode.class);
		JsonNode schema = mapper.readValue(new URL(env.getProperty(IdRepoConstants.VID_POLICY_SCHEMA_URL.getValue())),
				JsonNode.class);
		final JsonSchema jsonSchema = JsonSchemaFactory.byDefault().getJsonSchema(schema);
		jsonSchema.validate(policyJson);
		List<String> vidType = JsonPath.compile(IdRepoConstants.VID_TYPE_PATH.getValue()).read(policyJson.toString(),
				READ_LIST_OPTIONS);
		List<Object> vidPolicy = JsonPath.compile(IdRepoConstants.VID_POLICY_PATH.getValue())
				.read(policyJson.toString(), READ_LIST_OPTIONS);
		vidPolicies = IntStream.range(0, vidType.size()).parallel().boxed()
				.collect(Collectors.toMap(vidType::get, i -> mapper.convertValue(vidPolicy.get(i), VidPolicy.class)));
	}

	/**
	 * Returns the policy based on the vid type provided.
	 *
	 * @param vidType
	 *            the vid type
	 * @return the policy
	 */
	public VidPolicy getPolicy(String vidType) {
		return vidPolicies.get(vidType);
	}

	/**
	 * Returns all the vid types available in the vid policy.
	 *
	 * @return the all vid types
	 */
	public Set<String> getAllVidTypes() {
		return vidPolicies.keySet();
	}
}
