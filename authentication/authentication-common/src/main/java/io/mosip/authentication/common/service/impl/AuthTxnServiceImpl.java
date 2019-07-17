package io.mosip.authentication.common.service.impl;

import java.util.ArrayList;
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
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.authtxn.service.AuthTxnService;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;

@Component
public class AuthTxnServiceImpl implements AuthTxnService {

	private static final int DEFAULT_COUNT = 10;

	private static final int DEFAULT_PAGE_SIZE = 1;

	private static final String UIN_KEY = "uin";

	@Autowired
	private IdService<AutnTxn> idService;

	@Autowired
	private AutnTxnRepository authtxnRepo;

	private static final String AUTH_TXN_DETAILS = "getAuthTransactionDetails";

	private static Logger logger = IdaLogger.getLogger(AuthTxnServiceImpl.class);

	@Override
	public List<AutnTxnDto> fetchAuthTxnDetails(AutnTxnRequestDto authtxnrequestdto)
			throws IdAuthenticationBusinessException {
		List<AutnTxn> autnTxnList = new ArrayList<>();
		String individualIdType = authtxnrequestdto.getIndividualIdType();
		String individualId = authtxnrequestdto.getIndividualId();
		Map<String, Object> idResDTO = idService.processIdType(individualIdType, individualId, false);
		if (idResDTO != null && !idResDTO.isEmpty() && idResDTO.containsKey(UIN_KEY)) {
			Integer pageStart = authtxnrequestdto.getPageStart();
			Integer pageFetch = authtxnrequestdto.getPageFetch();
			String uin = String.valueOf(idResDTO.get(UIN_KEY));
			String hashedUin = HMACUtils.digestAsPlainText(HMACUtils.generateHash(uin.getBytes()));
			autnTxnList = authtxnRepo.findByUin(hashedUin,
					PageRequest.of(pageStart == null || pageStart == 0 ? DEFAULT_PAGE_SIZE : pageStart,
							pageFetch == null || pageFetch == 0 ? DEFAULT_COUNT : pageFetch));
			logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), AUTH_TXN_DETAILS,
					"pageStart >>" + pageStart + "\t" + "pageFetch >>" + pageFetch);
		}
		return fetchAuthResponse(autnTxnList);
	}

	private List<AutnTxnDto> fetchAuthResponse(List<AutnTxn> autnTxnList) {
		return autnTxnList.stream().map(this::fetchAuthResponseDTO).collect(Collectors.toList());
	}

	private AutnTxnDto fetchAuthResponseDTO(AutnTxn autnTxn) {
		AutnTxnDto autnTxnDto = new AutnTxnDto();
		autnTxnDto.setTransactionID(autnTxn.getRequestTrnId());
		autnTxnDto.setRequestdatetime(autnTxn.getRequestDTtimes());
		autnTxnDto.setAuthtypeCode(autnTxn.getAuthTypeCode());
		autnTxnDto.setStatusCode(autnTxn.getStatusCode());
		autnTxnDto.setStatusComment(autnTxn.getStatusComment());
		autnTxnDto.setReferenceIdType(autnTxn.getRefIdType());
		return autnTxnDto;
	}

}
