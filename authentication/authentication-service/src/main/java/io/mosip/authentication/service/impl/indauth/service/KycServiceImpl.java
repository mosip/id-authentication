package io.mosip.authentication.service.impl.indauth.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoServiceImpl;
import io.mosip.kernel.core.spi.logger.MosipLogger;

/**
 * The implementation of Kyc Authentication service.
 * 
 * @author Sanjay Murali
 */

@Service
public class KycServiceImpl implements KycService{
	
	private static final String LIMITED_KYC = "limited KYC";

	@Autowired
	Environment env;
	
	@Autowired
	private IdInfoServiceImpl idInfoServiceImpl;
	
	/** The mosip logger. */
	private static MosipLogger mosipLogger = IdaLogger.getLogger(KycServiceImpl.class);
	
	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	@Override
	public KycInfo retrieveKycInfo(String uin, String eKycType, boolean ePrintReq, boolean isSecLangInfoRequired) throws IdAuthenticationBusinessException {
		KycInfo kycInfo = new KycInfo();
		Map<String, List<IdentityInfoDTO>> identityInfo = retrieveIdentityFromIdRepo(uin);
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = constructIdentityInfo(eKycType, identityInfo, isSecLangInfoRequired);
		kycInfo.setIdentity(filteredIdentityInfo);
		if(ePrintReq) {
			String ePrintInfo = generatePrintableKyc(eKycType,kycInfo.getIdentity());
			kycInfo.setEPrint(ePrintInfo);			
		}
		return kycInfo;
	}
	
	private Map<String, List<IdentityInfoDTO>> retrieveIdentityFromIdRepo(String uin) throws IdAuthenticationBusinessException{
		Map<String, List<IdentityInfoDTO>> identity = null;
		try {
			identity = idInfoServiceImpl.getIdInfo(uin);
		} catch (IdAuthenticationDaoException e) {
			mosipLogger.error(DEFAULT_SESSION_ID, null, null, e.getErrorText());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
		}
		return identity;
	}
	
	private Map<String, List<IdentityInfoDTO>> constructIdentityInfo(String eKycType, Map<String, List<IdentityInfoDTO>> identity, boolean isSecLangInfoRequired){
		Map<String, List<IdentityInfoDTO>> identityInfo;
		String kycTypeKey;

		if(eKycType.equals(LIMITED_KYC)) {
			 kycTypeKey = "ekyc.type.limitedkyc";
		}else {
			kycTypeKey = "ekyc.type.fullkyc";
		}
		
		List<String> limitedKycDetail = Arrays.asList(env.getProperty(kycTypeKey).split(","));
		identityInfo = identity.entrySet().stream()
				.filter(id -> limitedKycDetail.contains(id.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		if(!isSecLangInfoRequired) {
			String primaryLanguage = env.getProperty("mosip.primary.lang-code");
			identityInfo = identityInfo.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
              entry -> entry.getValue()
                           .stream()
                           .filter(info -> info.getLanguage() != null && info.getLanguage().equalsIgnoreCase(primaryLanguage))
                           .collect(Collectors.toList()))
             );
		}
		return identityInfo;
	}
	
	private String generatePrintableKyc(String eKycType, Map<String, List<IdentityInfoDTO>> identity) {
		String pdfDetails;
		
		if(eKycType.equals(LIMITED_KYC)) {
			//fix me -> notification service to be implemented
			pdfDetails = generatePDF(env.getProperty("ekyc.type.limitedkyc"),identity);
		}else {
			pdfDetails = generatePDF(env.getProperty("ekyc.template.fullkyc"),identity);
		}
		return pdfDetails;
	}

	private String generatePDF(String pdfDetails, Map<String, List<IdentityInfoDTO>> identity) {
		return pdfDetails;
	}

}
