/**
 * 
 */
package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderDto;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderPutDto;
import io.mosip.kernel.masterdata.dto.getresponse.FoundationalTrustProviderResDto;
import io.mosip.kernel.masterdata.service.FoundationalTrustProviderService;
import io.mosip.kernel.masterdata.utils.AuditUtil;
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
	AuditUtil auditUtil;
	
	@Autowired
	private FoundationalTrustProviderService foundationalTrustProviderService;
	
	@ResponseFilter
	@PostMapping
	public ResponseWrapper<FoundationalTrustProviderResDto> registerFoundationalTrustProvider(@RequestBody @Valid RequestWrapper<FoundationalTrustProviderDto> foundationalTrustProviderDto)
	{
		auditUtil.auditRequest(MasterDataConstant.CREATE_API_IS_CALLED + FoundationalTrustProviderDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.CREATE_API_IS_CALLED + FoundationalTrustProviderDto.class.getCanonicalName());
		ResponseWrapper<FoundationalTrustProviderResDto> response=foundationalTrustProviderService.registerFoundationalTrustProvider(foundationalTrustProviderDto.getRequest());
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_CREATE, FoundationalTrustProviderDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_CREATE_DESC, FoundationalTrustProviderDto.class.getCanonicalName()));
		return response;
	}
	
	@ResponseFilter
	@PutMapping
	public ResponseWrapper<FoundationalTrustProviderResDto> updateFoundationalTrustProvider(@RequestBody @Valid RequestWrapper<FoundationalTrustProviderPutDto> foundationalTrustProviderDto)
	{
		auditUtil.auditRequest(MasterDataConstant.UPDATE_API_IS_CALLED + FoundationalTrustProviderDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.UPDATE_API_IS_CALLED + FoundationalTrustProviderDto.class.getCanonicalName());
		ResponseWrapper<FoundationalTrustProviderResDto> response= foundationalTrustProviderService.updateFoundationalTrustProvider(foundationalTrustProviderDto.getRequest());
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_UPDATE, FoundationalTrustProviderDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_UPDATE_DESC, FoundationalTrustProviderDto.class.getCanonicalName()));
		return response;
	}
	

}
