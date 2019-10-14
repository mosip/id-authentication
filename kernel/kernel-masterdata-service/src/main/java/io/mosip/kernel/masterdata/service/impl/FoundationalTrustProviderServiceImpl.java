/**
 * 
 */
package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.FoundationalTrustProviderErrorCode;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderDto;
import io.mosip.kernel.masterdata.dto.FoundationalTrustProviderPutDto;
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
		if(foundationalTrustProvider!=null)
		{
			throw new MasterDataServiceException(FoundationalTrustProviderErrorCode.FTP_ALREADY_PRESENT.getErrorCode(),FoundationalTrustProviderErrorCode.FTP_ALREADY_PRESENT.getErrorMessage());
		}
		foundationalTrustProvider = MetaDataUtils.setCreateMetaData(foundationalTrustProviderDto, FoundationalTrustProvider.class);
		foundationalTrustProvider = foundationalTrustProviderRepository.create(foundationalTrustProvider);
		if(foundationalTrustProvider!=null)
		{
			FoundationalTrustProviderHistory foundationalTrustProviderHistory = MetaDataUtils.setCreateMetaData(foundationalTrustProvider, FoundationalTrustProviderHistory.class);
			foundationalTrustProviderHistory.setEffectivetimes(foundationalTrustProvider.getCreatedDateTime());
			foundationalTrustProviderHistory.setCreatedDateTime(foundationalTrustProvider.getCreatedDateTime());
			foundationalTrustProviderRepositoryHistory.create(foundationalTrustProviderHistory);
		}
		ResponseWrapper<FoundationalTrustProviderResDto> response = new ResponseWrapper<>();
		foundationalTrustProviderResDto =  MetaDataUtils.setCreateMetaData(foundationalTrustProvider, FoundationalTrustProviderResDto.class);
		response.setResponse(foundationalTrustProviderResDto);
		response.setResponsetime(LocalDateTime.now());
		return response;
	}

	@Override
	public ResponseWrapper<FoundationalTrustProviderResDto> updateFoundationalTrustProvider(
			FoundationalTrustProviderPutDto foundationalTrustProviderPutDto) {
		FoundationalTrustProvider updateFoundationalTrustProvider = null;
		FoundationalTrustProviderResDto foundationalTrustProviderResDto = null;
		updateFoundationalTrustProvider = MetaDataUtils.setUpdateMetaData(foundationalTrustProviderPutDto, updateFoundationalTrustProvider,  null);
		updateFoundationalTrustProvider = foundationalTrustProviderRepository.update(updateFoundationalTrustProvider);
		if(updateFoundationalTrustProvider!=null)
		{
			FoundationalTrustProviderHistory foundationalTrustProviderHistory = MetaDataUtils.setCreateMetaData(updateFoundationalTrustProvider, FoundationalTrustProviderHistory.class);
			foundationalTrustProviderHistory.setEffectivetimes(updateFoundationalTrustProvider.getCreatedDateTime());
			foundationalTrustProviderHistory.setCreatedDateTime(updateFoundationalTrustProvider.getCreatedDateTime());
			foundationalTrustProviderRepositoryHistory.create(foundationalTrustProviderHistory);
		}
		ResponseWrapper<FoundationalTrustProviderResDto> response = new ResponseWrapper<>();
		foundationalTrustProviderResDto =  MetaDataUtils.setCreateMetaData(updateFoundationalTrustProvider, FoundationalTrustProviderResDto.class);
		response.setResponse(foundationalTrustProviderResDto);
		response.setResponsetime(LocalDateTime.now());
		return response;
	}

}
