package io.mosip.authentication.service.kyc.util;

import java.io.IOException;
import java.io.StringReader;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.JsonDocument;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class fetches the Verifiable Credentials schema & @Context data.
 * 
 * @author Mahammed Taheer
 *
 */
@Component
public class VCSchemaProviderUtil {
    
    private static Logger logger = IdaLogger.getLogger(VCSchemaProviderUtil.class);

    @Autowired
    RestTemplate restTemplate;

    public JsonDocument getVCContextSchema(String configServerFileStorageUrl, String uri) {
		try {
			String vcContextJson = restTemplate.getForObject(configServerFileStorageUrl + uri, String.class);
			JsonDocument jsonDocument = JsonDocument.of(new StringReader(vcContextJson));
			return jsonDocument;
		} catch (JsonLdError e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getVCContextSchema",
					"Error while getting VC Context Schema Json Document.",  e );
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorCode(),
					IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorMessage());
		}
	}

	public JSONObject getVCContextData(String configServerFileStorageURL, String uri, ObjectMapper objectMapper) 
				throws IdAuthenticationBusinessException {
		try {
			String vcContextData = restTemplate.getForObject(configServerFileStorageURL + uri, String.class);
			JSONObject jsonObject = objectMapper.readValue(vcContextData, JSONObject.class);
			return jsonObject;
		} catch (IOException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getVCContextData",
					"error while getting VC Context Json.", e);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorCode(),
					IdAuthenticationErrorConstants.DOWNLOAD_ERROR.getErrorMessage());
		}
	}
}
