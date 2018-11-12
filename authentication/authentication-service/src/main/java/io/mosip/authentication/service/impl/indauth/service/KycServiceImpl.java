package io.mosip.authentication.service.impl.indauth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.kernel.core.spi.logger.MosipLogger;

/**
 * The implementation of Kyc Authentication service.
 * 
 * @author Sanjay Murali
 */

@Service
public class KycServiceImpl implements KycService{
	
	private static final String LABEL_SEC = "_label_sec";

	private static final String LABEL_PRI = "_label_pri";

	private static final String LIMITED_KYC = "limited KYC";

	@Autowired
	Environment env;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private IdInfoServiceImpl idInfoServiceImpl;
	
	@Autowired
	private IdTemplateManager idTemplateManager;
	
	/** The mosip logger. */
	private static MosipLogger mosipLogger = IdaLogger.getLogger(KycServiceImpl.class);
	
	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	@Override
	public KycInfo retrieveKycInfo(String uin, KycType eKycType, boolean ePrintReq, boolean isSecLangInfoRequired) throws IdAuthenticationBusinessException {
		KycInfo kycInfo = new KycInfo();
		Map<String, List<IdentityInfoDTO>> identityInfo = retrieveIdentityFromIdRepo(uin);
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = constructIdentityInfo(eKycType, identityInfo, isSecLangInfoRequired);
		kycInfo.setIdentity(filteredIdentityInfo);
		if(ePrintReq) {
			Object maskedUin = uin;			
			if(env.getProperty("uin.masking.required", Boolean.class)) {
				maskedUin = generateMaskedUIN(uin, env.getProperty("uin.masking.charcount", Integer.class));
			}
			Map<String, Object> pdfDetails = generatePDFDetails(filteredIdentityInfo, maskedUin);
			String ePrintInfo = generatePrintableKyc(eKycType,pdfDetails);
			kycInfo.setEPrint(ePrintInfo);			
		}
		return kycInfo;
	}

	private String generateMaskedUIN(String uin, int maskNo) {
		char[] uinChar = uin.toCharArray();
		for(int i=0; i<maskNo; i++) {
			uinChar[i] = 'X';
		}
		return String.valueOf(uinChar);
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
	
	private Map<String, List<IdentityInfoDTO>> constructIdentityInfo(KycType eKycType, Map<String, List<IdentityInfoDTO>> identity, boolean isSecLangInfoRequired){
		Map<String, List<IdentityInfoDTO>> identityInfo;
		String kycTypeKey;

		if(eKycType == KycType.LIMITED) {
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
	

	private Map<String, Object> generatePDFDetails(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, Object maskedUin) {
		String primaryLanguage = env.getProperty("mosip.primary.lang-code");
		String secondaryLanguage = env.getProperty("mosip.secondary.lang-code");
		Map<String, Object> pdfDetails = new HashMap<>();
		filteredIdentityInfo
			    .entrySet()
			    .stream()
			    .forEach(e -> e.getValue().stream().forEach(v -> {
			    	if(v.getLanguage().equalsIgnoreCase(primaryLanguage)) {
			    		pdfDetails.put(e.getKey().concat("_pri"), v.getValue());
			    		pdfDetails.put(e.getKey().concat(LABEL_PRI), 
			    				messageSource.getMessage(e.getKey().concat(LABEL_PRI), null, LocaleContextHolder.getLocale()));
			    	}else if(v.getLanguage().equalsIgnoreCase(secondaryLanguage)) {
			    		pdfDetails.put(e.getKey().concat("_sec"), v.getValue());
			    		pdfDetails.put(e.getKey().concat(LABEL_SEC), 
			    				messageSource.getMessage(e.getKey().concat(LABEL_SEC), null, new Locale(secondaryLanguage)));
			    	}
			    }));
		pdfDetails.put("uin_pri", maskedUin);
		pdfDetails.put("uin_label_pri", messageSource.getMessage("uin_label_pri", null, LocaleContextHolder.getLocale()));
		pdfDetails.put("uin_sec", maskedUin);
		pdfDetails.put("uin_label_sec", messageSource.getMessage("uin_label_sec", null, new Locale(secondaryLanguage)));
		pdfDetails.put("name_label_pri", messageSource.getMessage("name_label_pri", null, LocaleContextHolder.getLocale()));
		pdfDetails.put("name_label_sec", messageSource.getMessage("name_label_sec", null, new Locale(secondaryLanguage)));
		return pdfDetails;
	}

	private String generatePrintableKyc(KycType eKycType, Map<String, Object> identity) throws IdAuthenticationBusinessException {
		String pdfDetails = null;
		try {
			if(eKycType == KycType.LIMITED) {
				pdfDetails =  idTemplateManager.applyTemplate(env.getProperty("ekyc.template.limitedkyc"), identity);

			}else {
				pdfDetails = idTemplateManager.applyTemplate(env.getProperty("ekyc.template.fullkyc"), identity);
			}
		} catch (IOException e) {
			mosipLogger.error(DEFAULT_SESSION_ID, null, null, e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
		}
		return pdfDetails;
	}


}
