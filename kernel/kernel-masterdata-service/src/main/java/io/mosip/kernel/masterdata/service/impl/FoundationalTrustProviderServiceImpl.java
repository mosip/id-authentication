/**
 * 
 */
package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderDto;
import io.mosip.kernel.masterdata.dto.getresponse.FoundationalTrustProviderResDto;
import io.mosip.kernel.masterdata.entity.FoundationalTrustProvider;
import io.mosip.kernel.masterdata.entity.FoundationalTrustProviderHistory;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.FoundationalTrustProviderRepository;
import io.mosip.kernel.masterdata.repository.FoundationalTrustProviderRepositoryHistory;
import io.mosip.kernel.masterdata.service.FoundationalTrustProviderService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class FoundationalTrustProviderServiceImpl implements FoundationalTrustProviderService {
	
	@Autowired
	private FoundationalTrustProviderRepository foundationalTrustProviderRepository;
	
	@Autowired
	private FoundationalTrustProviderRepositoryHistory foundationalTrustProviderRepositoryHistory;

	@Override
	public ResponseWrapper<FoundationalTrustProviderResDto> registerFoundationalTrustProvider(
			FoundationalTrustProviderDto foundationalTrustProviderDto) {
		FoundationalTrustProvider foundationalTrustProvider = null;
		FoundationalTrustProviderResDto foundationalTrustProviderResDto = null;
		foundationalTrustProvider = foundationalTrustProviderRepository.findByDetails(foundationalTrustProviderDto.getName(),foundationalTrustProviderDto.getEmail(),foundationalTrustProviderDto.getAddress(),foundationalTrustProviderDto.getCertAlias());
		if(foundationalTrustProvider==null)
		{
			throw new MasterDataServiceException("","");
		}
		String id = UUID.randomUUID().toString();
		foundationalTrustProvider = MetaDataUtils.setCreateMetaData(foundationalTrustProviderDto, FoundationalTrustProvider.class);
		foundationalTrustProvider.setId(id);
		foundationalTrustProvider = foundationalTrustProviderRepository.create(foundationalTrustProvider);
		if(foundationalTrustProvider!=null)
		{
			FoundationalTrustProviderHistory foundationalTrustProviderHistory = MetaDataUtils.setCreateMetaData(foundationalTrustProvider, FoundationalTrustProviderHistory.class);
			foundationalTrustProviderRepositoryHistory.create(foundationalTrustProviderHistory);
		}
		ResponseWrapper<FoundationalTrustProviderResDto> response = new ResponseWrapper<>();
		foundationalTrustProviderResDto =  MetaDataUtils.setCreateMetaData(foundationalTrustProvider, FoundationalTrustProviderResDto.class);
		response.setResponse(foundationalTrustProviderResDto);
		response.setResponsetime(LocalDateTime.now());
		return response;
	}

}
