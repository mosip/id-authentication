package io.mosip.authentication.common.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.core.autntxn.dto.AutnTxnDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtxn.service.AuthTxnService;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Service AuthTxnServiceImpl is used to store/retrive Auth transactions for a UIN/VID.
 * 
 */
@Component
public class AuthTxnServiceImpl implements AuthTxnService {

	/** The Constant DEFAULT_PAGE_COUNT. */
	private static final int DEFAULT_PAGE_COUNT = 10;

	/** The Constant DEFAULT_PAGE_START. */
	private static final int DEFAULT_PAGE_START = 1;

	/** The id service. */
	@Autowired
	private IdService<AutnTxn> idService;
	
	/** The authtxn repo. */
	@Autowired
	private AutnTxnRepository authtxnRepo;
	
	/** The Constant AUTH_TXN_DETAILS. */
	private static final String AUTH_TXN_DETAILS = "getAuthTransactionDetails";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(AuthTxnServiceImpl.class);

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.authtxn.service.AuthTxnService#fetchAuthTxnDetails(io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto)
	 */
	@Override
	public List<AutnTxnDto> fetchAuthTxnDetails(AutnTxnRequestDto authtxnrequestdto)
			throws IdAuthenticationBusinessException {
		return doFetchAuthTxnDetails(authtxnrequestdto);
	}

	private List<AutnTxnDto> doFetchAuthTxnDetails(AutnTxnRequestDto authtxnrequestdto) throws IdAuthenticationBusinessException {
		List<AutnTxn> autnTxnList;
		String individualIdType = IdType.getIDTypeStrOrSameStr(authtxnrequestdto.getIndividualIdType());
		
		if(!IdType.UIN.getType().equals(individualIdType) && !IdType.VID.getType().equals(individualIdType)) {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							"individualIdType - " + individualIdType )); 
		}
		
		String individualId = authtxnrequestdto.getIndividualId();
		Map<String, Object> idResDTO = idService.processIdType(individualIdType, individualId, false, false, Collections.emptySet());
		if (idResDTO != null && !idResDTO.isEmpty()) {
			String token = idService.getToken(idResDTO);

			Integer pageStart = authtxnrequestdto.getPageStart();
			Integer pageFetch = authtxnrequestdto.getPageFetch();
			
			boolean fetchAllRecords = false;
			
			if(pageStart == null) {
				if(pageFetch == null) {
					//If both Page start and page fetch values are null return all records
					fetchAllRecords = true;
				} else {
					pageStart = DEFAULT_PAGE_START;
				}
			} else {
				if(pageFetch == null) {
					pageFetch = DEFAULT_PAGE_COUNT;
				}
			}
			
			
			PageRequest pageRequest = getPageRequest(pageStart, pageFetch, fetchAllRecords);
			
			autnTxnList = authtxnRepo.findByToken(token, pageRequest);
			return fetchAuthResponse(autnTxnList);
		}
		
		return Collections.emptyList();
	}

	private PageRequest getPageRequest(Integer pageStart, Integer pageFetch, boolean fetchAllRecords)
			throws IdAuthenticationBusinessException {
		PageRequest pageRequest = null;
		if(!fetchAllRecords) {
			if (pageStart < 1) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(
								IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"pageStart - " + pageStart ));
			}
			
			if (pageFetch < 1) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(
								IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"pageFetch - " + pageFetch));
			}
			
			int pageStartIndex = pageStart - 1;
			pageRequest = PageRequest.of(pageStartIndex, pageFetch);
			
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTH_TXN_DETAILS,
					"pageStart >>" + pageStart + "\t" + "pageFetch >>" + pageFetch);
		}
		return pageRequest;
	}

	/**
	 * Fetch auth response.
	 *
	 * @param autnTxnList the autn txn list
	 * @return the list
	 */
	private List<AutnTxnDto> fetchAuthResponse(List<AutnTxn> autnTxnList) {
		return autnTxnList.stream().map(AuthTxnServiceImpl::fetchAuthResponseDTO).collect(Collectors.toList());
	}

	/**
	 * Fetch auth response DTO.
	 *
	 * @param autnTxn the autn txn
	 * @return the autn txn dto
	 */
	public static AutnTxnDto fetchAuthResponseDTO(AutnTxn autnTxn) {
		AutnTxnDto autnTxnDto = new AutnTxnDto();
		autnTxnDto.setTransactionID(autnTxn.getRequestTrnId());
		autnTxnDto.setRequestdatetime(autnTxn.getRequestDTtimes());
		autnTxnDto.setAuthtypeCode(autnTxn.getAuthTypeCode());
		autnTxnDto.setStatusCode(autnTxn.getStatusCode());
		autnTxnDto.setStatusComment(autnTxn.getStatusComment());
		autnTxnDto.setReferenceIdType(autnTxn.getRefIdType());
		autnTxnDto.setEntityName(autnTxn.getEntityName());
		autnTxnDto.setRequestSignature(autnTxn.getRequestSignature());
		autnTxnDto.setResponseSignature(autnTxn.getResponseSignature());
		autnTxnDto.setTokenId(autnTxn.getToken());
		autnTxnDto.setEntityId(autnTxn.getEntityId());
		autnTxnDto.setIndividualId(autnTxn.getRefId());
		return autnTxnDto;
	}

}
