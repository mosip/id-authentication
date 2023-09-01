package io.mosip.authentication.common.service.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ACR_AMR;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.AMR;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDP_AMR_ACR_IDA_MAPPING_SOURCE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtype.acramr.AuthMethodsRefValues;
import io.mosip.authentication.core.spi.authtype.acramr.AuthenticationFactor;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class instantiates the acr_amr configuration.
 * @author Mahammed Taheer
 *
 */
@Component
public class AuthContextClazzRefProvider {
    
    private static Logger logger = IdaLogger.getLogger(AuthContextClazzRefProvider.class);

    @Value("${"+ IDP_AMR_ACR_IDA_MAPPING_SOURCE  +"}")
	private String amracrMappingUri;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    private AuthMethodsRefValues authMethodsRefValues;

    @PostConstruct
	public void init() throws IdAuthenticationBusinessException {
		authMethodsRefValues = createAuthMethodsRefValuesObject();
	}
 
	private AuthMethodsRefValues createAuthMethodsRefValuesObject() throws IdAuthenticationBusinessException {
        
        try {
            String mappingJson = restTemplate.getForObject(amracrMappingUri, String.class);
            ObjectNode responseBody = objectMapper.readValue(mappingJson, new TypeReference<ObjectNode>(){});
            Map<String, List<String>> acramrMap = objectMapper.convertValue(responseBody.get(ACR_AMR), 
                        new TypeReference<Map<String, List<String>>>(){});

            Map<String, List<AuthenticationFactor>> amrMap = objectMapper.convertValue(responseBody.get(AMR), 
                        new TypeReference<Map<String, List<AuthenticationFactor>>>(){});
            Map<String, List<AuthenticationFactor>> mappedAcrsAmrs = new HashMap<>();
            for (String key : acramrMap.keySet()) {
                List<String> amrs = acramrMap.get(key);
                List<AuthenticationFactor> authFactors = new ArrayList<>();
                for (String amr : amrs) {
                    authFactors.addAll(amrMap.get(amr));
                }
                mappedAcrsAmrs.put(key, authFactors);

            }
            return new AuthMethodsRefValues(mappedAcrsAmrs);
        } catch (JsonProcessingException e) {
            logger.error(IdAuthCommonConstants.IDA, this.getClass().getSimpleName(), "createAuthMethodsRefValuesObject", 
                        "Not able to download the AMR-ACR Json config file. URI: " + amracrMappingUri, e);
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorCode(),
                        IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorMessage());
        }

        /* ClientResponse clientResponse = webClient.get().uri(amracrMappingUri).accept(MediaType.APPLICATION_JSON).exchange().block();
        if (Objects.isNull(clientResponse) || clientResponse.statusCode() != HttpStatus.OK) {
            logger.error(IdAuthCommonConstants.IDA, this.getClass().getSimpleName(), "createAuthMethodsRefValuesObject", 
						"Not able to download the AMR-ACR Json config file. URI: " + amracrMappingUri);
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorCode(),
						IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorMessage());
        }

        ObjectNode responseBody = clientResponse.bodyToMono(ObjectNode.class).block(); */
        
    }

    public AuthMethodsRefValues getAuthMethodsRefValues() { 
        return authMethodsRefValues;
    }

}
