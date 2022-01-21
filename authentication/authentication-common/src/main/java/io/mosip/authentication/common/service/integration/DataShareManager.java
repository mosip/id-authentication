package io.mosip.authentication.common.service.integration;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.DATA_SHARE_GET_DECRYPT_REF_ID;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_DATASHARE_THUMBPRINT_VALIDATION_REQUIRED;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.idrepository.core.util.RestUtil;

@Component
public class DataShareManager {
	
	@Autowired
	@Qualifier("withSelfTokenWebclient")
	private RestHelper restHelper;
	
	@Autowired
	private RestRequestFactory restRequestFactory;
	
	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Value("${" + DATA_SHARE_GET_DECRYPT_REF_ID + "}")
	private String dataShareGetDecryptRefId;
	
	@Value("${" + IDA_DATASHARE_THUMBPRINT_VALIDATION_REQUIRED + ":true}")
	private boolean thumbprintValidationRequired;

	@SuppressWarnings("unchecked")
	public <R> R downloadObject(String dataShareUrl, Class<R> clazz, boolean decryptionRequired) throws  RestServiceException, IdAuthenticationBusinessException {
		RestRequestDTO request = restRequestFactory.buildRequest(RestServicesConstants.DATA_SHARE_GET, null, String.class);
		request.setUri(dataShareUrl);
		String responseStr = restHelper.requestSync(request);
		Optional<Entry<String, Object>> errorOpt = RestUtil.getError(responseStr, mapper);
		
		if (errorOpt.isEmpty()) {
			R result;
			if (decryptionRequired) {
				// Decrypt data
				byte[] dataBytes = securityManager.decrypt(responseStr, dataShareGetDecryptRefId, null, null,
						thumbprintValidationRequired);
				if (clazz.equals(String.class)) {
					result = (R) new String(dataBytes);
				} else {
					result = readToObject(clazz, dataBytes);
				}
			} else {
				if (clazz.equals(String.class)) {
					result = (R) responseStr;
				} else {
					byte[] dataBytes = responseStr.getBytes();
					result = readToObject(clazz, dataBytes);
				}
			}
			
			return result;
			
		} else {
			//Unchecked exception is thrown so that retry will not be performed on this.
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage() + " : " + errorOpt.get().toString());
		}
	}

	private <R> R readToObject(Class<R> clazz, byte[] dataBytes) throws IdAuthenticationBusinessException {
		try {
			return mapper.readValue(dataBytes, clazz);
		} catch (IOException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
}
