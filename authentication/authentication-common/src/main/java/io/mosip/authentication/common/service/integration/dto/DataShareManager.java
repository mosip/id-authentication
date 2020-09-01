package io.mosip.authentication.common.service.integration.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.RestServiceException;

@Component
public class DataShareManager {
	
	@Autowired
	private RestHelper restHelper;
	
	@Autowired
	private RestRequestFactory restRequestFactory;
	
	public <R> R downloadObject(String dataShareUrl, Class<R> clazz) throws  RestServiceException, IDDataValidationException {
		RestRequestDTO request = restRequestFactory.buildRequest(RestServicesConstants.DATA_SHARE_GET, null, clazz);
		return restHelper.requestSync(request);
	}

}
