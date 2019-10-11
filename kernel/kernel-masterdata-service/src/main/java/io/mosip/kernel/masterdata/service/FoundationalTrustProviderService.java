/**
 * 
 */
package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderDto;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderPutDto;
import io.mosip.kernel.masterdata.dto.getresponse.FoundationalTrustProviderResDto;

/**
 * @author Ramadurai Pandian
 *
 */
public interface FoundationalTrustProviderService {

	ResponseWrapper<FoundationalTrustProviderResDto> registerFoundationalTrustProvider(
			FoundationalTrustProviderDto request);

	ResponseWrapper<FoundationalTrustProviderResDto> updateFoundationalTrustProvider(
			FoundationalTrustProviderPutDto request);

}
