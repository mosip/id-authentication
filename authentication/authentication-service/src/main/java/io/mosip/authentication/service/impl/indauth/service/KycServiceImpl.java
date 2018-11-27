package io.mosip.authentication.service.impl.indauth.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;

/**
 * The implementation of Kyc Authentication service.
 * 
 * @author Sanjay Murali
 */

@Service
public class KycServiceImpl implements KycService {

	/** The Constant LABEL. */
	private static final String LABEL = "_label";

	/** The Constant LABEL_SEC. */
	private static final String LABEL_SEC = LABEL + "_sec";

	/** The Constant LABEL_PRI. */
	private static final String LABEL_PRI = LABEL + "_pri";
	
	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(KycServiceImpl.class);
	
	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

    @Autowired
    Environment env;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IdInfoService idInfoService;

    @Autowired
    private IdAuthService idAuthService;

    @Autowired
    private IdTemplateManager idTemplateManager;

    @Autowired
    private DemoHelper demoHelper;

    @Autowired
    private PDFGenerator pdfGenerator;

    /**
	 * This method will return the KYC info of the individual
	 * 
	 * @param eKycType the ekyctype full or limited
	 * @param refId the refId
	 * @param isSecLangInfoRequired the isseclanginforequired used to check secondary language info also needed
	 * @param ePrintReq the ePrintReq used to check is PDF required or not
	 * @return the map 
	 */
    @Override
    public KycInfo retrieveKycInfo(String refId, KycType eKycType, boolean ePrintReq, boolean isSecLangInfoRequired)
	    throws IdAuthenticationBusinessException {
	KycInfo kycInfo = new KycInfo();
	Map<String, List<IdentityInfoDTO>> identityInfo = retrieveIdentityFromIdRepo(refId);
	Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = constructIdentityInfo(eKycType, identityInfo,
		isSecLangInfoRequired);
	kycInfo.setIdentity(filteredIdentityInfo);
	Optional<String> uinOpt = idAuthService.getUIN(refId);
	
	if (uinOpt.isPresent() && ePrintReq) {
		String uin = uinOpt.get();
		Object maskedUin = uin;
	    if (env.getProperty("uin.masking.required", Boolean.class)) {
		maskedUin = MaskUtil.generateMaskValue(uin, env.getProperty("uin.masking.charcount", Integer.class));
	    }
	    Map<String, Object> pdfDetails = generatePDFDetails(filteredIdentityInfo, maskedUin);
	    String ePrintInfo = generatePrintableKyc(eKycType, pdfDetails, isSecLangInfoRequired);
	    kycInfo.setEPrint(ePrintInfo);
	    kycInfo.setIdvId(maskedUin.toString());
	}
	return kycInfo;
    }

    /**
	 * method to retrieve details by passing refid
	 * 
	 * @param refId
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
    private Map<String, List<IdentityInfoDTO>> retrieveIdentityFromIdRepo(String refId)
	    throws IdAuthenticationBusinessException {
	Map<String, List<IdentityInfoDTO>> identity = null;
	try {
	    identity = idInfoService.getIdInfo(refId);
	} catch (IdAuthenticationDaoException e) {
	    mosipLogger.error(DEFAULT_SESSION_ID, null, null, e.getErrorText());
	    throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
	}
	return identity;
    }

    /**
	 * Construct identity info - Method to filter the details to be printed.
	 *
	 * @param eKycType the e kyc type
	 * @param identity the identity
	 * @param isSecLangInfoRequired the is sec lang info required
	 * @return the map
	 */
    private Map<String, List<IdentityInfoDTO>> constructIdentityInfo(KycType eKycType,
	    Map<String, List<IdentityInfoDTO>> identity, boolean isSecLangInfoRequired) {
	Map<String, List<IdentityInfoDTO>> identityInfo;
	String kycTypeKey;

	if (eKycType == KycType.LIMITED) {
	    kycTypeKey = "ekyc.type.limitedkyc";
	} else {
	    kycTypeKey = "ekyc.type.fullkyc";
	}

	List<String> limitedKycDetail = Arrays.asList(env.getProperty(kycTypeKey).split(","));
	identityInfo = identity.entrySet().stream().filter(id -> limitedKycDetail.contains(id.getKey()))
		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	if (!isSecLangInfoRequired) {
	    String primaryLanguage = env.getProperty("mosip.primary.lang-code");
	    identityInfo = identityInfo.entrySet().stream()
		    .collect(Collectors.toMap(Map.Entry::getKey,
			    entry -> entry.getValue().stream()
				    .filter((IdentityInfoDTO info) -> info.getLanguage() == null
					    || info.getLanguage().equalsIgnoreCase("null")
					    || info.getLanguage().equalsIgnoreCase(primaryLanguage))
				    .collect(Collectors.toList())));
	}
	return identityInfo;
    }

    /**
	 * Method to give details in primary or secondary language
	 * 
	 * @param filteredIdentityInfo
	 * @param maskedUin
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
    private Map<String, Object> generatePDFDetails(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo,
	    Object maskedUin) throws IdAuthenticationBusinessException {
	String primaryLanguage = env.getProperty("mosip.primary.lang-code");
	String secondaryLanguage = env.getProperty("mosip.secondary.lang-code");
	Map<String, Object> pdfDetails = new HashMap<>();
	filteredIdentityInfo.entrySet().stream().forEach(e -> e.getValue().stream().forEach(v -> {
	    if (v.getLanguage().equalsIgnoreCase(primaryLanguage)) {
		pdfDetails.put(e.getKey().concat("_pri"), v.getValue());
		pdfDetails.put(e.getKey().concat(LABEL_PRI),
			messageSource.getMessage(e.getKey().concat(LABEL), null, LocaleContextHolder.getLocale()));
	    } else if (v.getLanguage().equalsIgnoreCase(secondaryLanguage)) {
		pdfDetails.put(e.getKey().concat("_sec"), v.getValue());
		pdfDetails.put(e.getKey().concat(LABEL_SEC),
			messageSource.getMessage(e.getKey().concat(LABEL), null, new Locale(secondaryLanguage)));
	    }
	}));
	pdfDetails.put("uin_pri", maskedUin);
	pdfDetails.put("uin_label_pri", messageSource.getMessage("uin_label", null, LocaleContextHolder.getLocale()));
	pdfDetails.put("uin_sec", maskedUin);
	pdfDetails.put("uin_label_sec", messageSource.getMessage("uin_label", null, new Locale(secondaryLanguage)));
	pdfDetails.put("name_label_pri", messageSource.getMessage("name_label", null, LocaleContextHolder.getLocale()));
	pdfDetails.put("name_label_sec", messageSource.getMessage("name_label", null, new Locale(secondaryLanguage)));
	pdfDetails.put("name_pri", demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, filteredIdentityInfo).getValue());
	pdfDetails.put("name_sec", demoHelper.getEntityInfo(DemoMatchType.NAME_SEC, filteredIdentityInfo).getValue());
	faceDetails(filteredIdentityInfo, maskedUin, pdfDetails);
	return pdfDetails;
    }

    /**
	 * Methods to retrieve image of the requested refid
	 * 
	 * @param filteredIdentityInfo
	 * @param maskedUin
	 * @param pdfDetails
	 * @throws IdAuthenticationBusinessException
	 */
    private void faceDetails(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo, Object maskedUin,
	    Map<String, Object> pdfDetails) throws IdAuthenticationBusinessException {
	Optional<String> faceValue = getFaceDetails(filteredIdentityInfo);
	if (faceValue.isPresent()) {
	    byte[] bytearray = Base64.getDecoder().decode(faceValue.get());
	    Path path = null;
	    BufferedImage imag;
	    try {
		imag = ImageIO.read(new ByteArrayInputStream(bytearray));
		File facePath = File.createTempFile(String.valueOf(maskedUin), ".jpg");
		ImageIO.write(imag, "jpg", facePath);
		path = facePath.toPath();
	    } catch (IOException e) {
		mosipLogger.error(DEFAULT_SESSION_ID, null, null, e.getMessage());
		throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
	    }
	    pdfDetails.put("photoUrl", path);
	}
    }

    /**
	 * @param filteredIdentityInfo
	 * @return
	 */
    private Optional<String> getFaceDetails(Map<String, List<IdentityInfoDTO>> filteredIdentityInfo) {
	return filteredIdentityInfo.entrySet().stream().filter(e -> e.getKey().equals("face"))
		.flatMap(val -> val.getValue().stream()).findAny().map(IdentityInfoDTO::getValue);
    }

    /**
	 * Method to find the template to provided the kyc info
	 * 
	 * @param eKycType
	 * @param identity
	 * @param isSecLangInfoRequired
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
    private String generatePrintableKyc(KycType eKycType, Map<String, Object> identity, boolean isSecLangInfoRequired)
	    throws IdAuthenticationBusinessException {
	String pdfDetails = null;
	try {
	    String template = null;
	    if (eKycType == KycType.LIMITED && isSecLangInfoRequired) {
		template = idTemplateManager.applyTemplate(env.getProperty("ekyc.template.limitedkyc.full"), identity);
	    } else if (eKycType == KycType.LIMITED && !isSecLangInfoRequired) {
		template = idTemplateManager.applyTemplate(env.getProperty("ekyc.template.limitedkyc.pri"), identity);
	    } else if (eKycType == KycType.FULL && isSecLangInfoRequired) {
		template = idTemplateManager.applyTemplate(env.getProperty("ekyc.template.fullkyc.full"), identity);
	    } else {
		template = idTemplateManager.applyTemplate(env.getProperty("ekyc.template.fullkyc.pri"), identity);
	    }

	    ByteArrayOutputStream bos = (ByteArrayOutputStream) pdfGenerator.generate(template);
	    deleteFileOnExit(identity);
	    pdfDetails = Base64.getEncoder().encodeToString(bos.toByteArray());
	} catch (IOException e) {
	    mosipLogger.error(DEFAULT_SESSION_ID, null, null, e.getMessage());
	    throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
	}
	return pdfDetails;
    }

    /**
	 * Method to delete the temp file created for image
	 * 
	 * @param identity
	 */
    private void deleteFileOnExit(Map<String, Object> identity) {
	Path path = (Path) identity.get("photoUrl");
	if (path != null) {
	    File file = path.toFile();
	    if (file.exists()) {
		file.deleteOnExit();
	    }
	}
    }

}
