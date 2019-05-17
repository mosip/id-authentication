package io.mosip.idrepository.vid.provider;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.dto.VidPolicy;
import io.mosip.kernel.core.idobjectvalidator.exception.FileIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectSchemaIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationProcessingException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;

/**
 * The Class VidPolicyProvider.
 *
 * @author Manoj SP
 */
@Component
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
	
	@Autowired
	@Qualifier("schema")
	private IdObjectValidator schemaValidator;

	/** The vid policies. */
	private Map<String, VidPolicy> vidPolicies;

	/**
	 * Policy details.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FileIOException 
	 * @throws IdObjectSchemaIOException 
	 * @throws IdObjectIOException 
	 * @throws IdObjectValidationProcessingException 
	 */
	@PostConstruct
	public void loadPolicyDetails() throws IOException, IdObjectValidationProcessingException, IdObjectIOException, IdObjectSchemaIOException, FileIOException {
		JsonNode policyJson = mapper.readValue(
				new URL(env.getProperty(IdRepoConstants.VID_POLICY_FILE_URL.getValue())), JsonNode.class);
		schemaValidator.validateIdObject(policyJson);
		List<String> vidType = JsonPath.compile(IdRepoConstants.VID_TYPE_PATH.getValue()).read(policyJson.toString(),
				READ_LIST_OPTIONS);
		List<Object> vidPolicy = JsonPath.compile(IdRepoConstants.VID_POLICY_PATH.getValue())
				.read(policyJson.toString(), READ_LIST_OPTIONS);
		vidPolicies = IntStream.range(0, vidType.size()).parallel().boxed()
				.collect(Collectors.toMap(vidType::get, i -> mapper.convertValue(vidPolicy.get(i), VidPolicy.class)));
	}

	/**
	 * Gets the policy.
	 *
	 * @param vidType the vid type
	 * @return the policy
	 */
	public VidPolicy getPolicy(String vidType) {
		return vidPolicies.get(vidType);
	}

	/**
	 * Gets the all vid types.
	 *
	 * @return the all vid types
	 */
	public Set<String> getAllVidTypes() {
		return vidPolicies.keySet();
	}
}
