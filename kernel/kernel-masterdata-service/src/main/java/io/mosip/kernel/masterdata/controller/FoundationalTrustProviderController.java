/**
 * 
 */
package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderDto;
import io.mosip.kernel.masterdata.dto.getresponse.FoundationalTrustProviderResDto;
import io.mosip.kernel.masterdata.service.FoundationalTrustProviderService;
import io.swagger.annotations.Api;


/**
 * 
 * Class handles REST calls with appropriate URLs.Service class
 * {@link FoundationalTrustProviderService} is called wherein the business logics are handled.
 * @author Ramadurai Pandian
 *
 */
@RestController
@Api(tags = { "FoundationalTrustProvider" })
@RequestMapping(value = "/foundationaltrustprovider")
public class FoundationalTrustProviderController {
	
	@Autowired
	private FoundationalTrustProviderService foundationalTrustProviderService;
	
	@PostMapping
	public ResponseWrapper<FoundationalTrustProviderResDto> registerFoundationalTrustProvider(RequestWrapper<FoundationalTrustProviderDto> foundationalTrustProviderDto)
	{
		return foundationalTrustProviderService.registerFoundationalTrustProvider(foundationalTrustProviderDto.getRequest());
	}
	

}
