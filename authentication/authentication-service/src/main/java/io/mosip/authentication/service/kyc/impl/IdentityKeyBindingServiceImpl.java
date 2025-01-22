package io.mosip.authentication.service.kyc.impl;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.entity.IdentityBindingCertificateStore;
import io.mosip.authentication.common.service.repository.IdentityBindingCertificateRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.service.IdentityKeyBindingService;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.kernel.core.keymanager.model.CertificateParameters;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.authentication.common.service.util.EnvUtil;

/**
 * The implementation of Identity Key Binding service which validates and creates 
 * certificate for key.
 *
 * @author Mahammed Taheer
 */

@Service
@Transactional
public class IdentityKeyBindingServiceImpl implements IdentityKeyBindingService {

    /** The logger. */
	private static Logger logger = IdaLogger.getLogger(IdentityKeyBindingServiceImpl.class);

    @Value("${mosip.ida.key.binding.name.default.langCode:eng}")
	private String defaultLangCode;
    
    @Value("${mosip.ida.key.binding.certificate.validity.in.days:90}")
	private int certificateValidityDays;

    @Autowired
	private IdAuthSecurityManager securityManager;

    @Autowired
	private IdentityBindingCertificateRepository bindingCertificateRepo;

    @Autowired
	private IDAMappingConfig idMappingConfig;
    
    @Autowired
	private IdentityCacheRepository identityRepo;
    
    @Override
    public boolean isPublicKeyBinded(String idVid, Map<String, Object> publicKeyJWK, String idvIdType)
            throws IdAuthenticationBusinessException {
        String idVidHash = securityManager.hash(idVid);
        PublicKey publicKey = createPublicKeyObject(publicKeyJWK);
        String publicKeyHash = IdAuthSecurityManager.generateHashAndDigestAsPlainText(publicKey.getEncoded()); 

        List<Object[]> resultList = bindingCertificateRepo.findIdentityBindingDataByPublicKeys(publicKeyHash);
        // Public key is not binded to any UIN/VID
        if (Objects.isNull(resultList) || resultList.isEmpty()) {
            return false;
        }
        String tokenId = getTokenId(idVidHash, idvIdType);

        // Public key is already binded, but checking binded key to same UIN/VID.
        // if key binded to same UIN/VID, let's consider as not binded so that it will bind again.  
        // if key binded to same tokenId of different UIN/VID, let's consider as not binded so that it will bind again.
        return resultList.stream().map(item -> new AbstractMap.SimpleEntry<>(
                                String.valueOf(item[0]), 
                                String.valueOf(item[1])
                            )).filter(e -> e.getKey().equals(tokenId) || e.getValue().equals(idVidHash))
                              .findAny().isEmpty();
    }

    @Override
    public String createAndSaveKeyBindingCertificate(IdentityKeyBindingRequestDTO identityKeyBindingRequestDTO,
            Map<String, List<IdentityInfoDTO>> identityInfo, String token, String partnerId) throws IdAuthenticationBusinessException {

        Map<String, Object> publicKeyJWK = identityKeyBindingRequestDTO.getIdentityKeyBinding().getPublicKeyJWK();
        PublicKey publicKey = createPublicKeyObject(publicKeyJWK);
        String identityName = getIdentityNameData(identityInfo);
        if (identityName.trim().length() == 0) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "createKeyBindingCertificate",
                                    "Identity Name is not available for the default language code.");
            throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.IDENTITY_NAME_NOT_FOUND.getErrorCode(),
                    IdAuthenticationErrorConstants.IDENTITY_NAME_NOT_FOUND.getErrorMessage());
        }
        LocalDateTime notBeforeDate = DateUtils.getUTCCurrentDateTime(); 
        LocalDateTime notAfterDate = notBeforeDate.plus(certificateValidityDays, ChronoUnit.DAYS);
        CertificateParameters certParams = getCertificateParameters(identityName, notBeforeDate, notAfterDate);

        Entry<String, String> certificateEntry;
        String certThumbprint;
        String certificateData;
        try {
            certificateEntry = securityManager.generateKeyBindingCertificate(publicKey, certParams);
            certThumbprint = certificateEntry.getKey();
            certificateData = certificateEntry.getValue();
        } catch (CertificateEncodingException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "createAndSaveKeyBindingCertificate",
                                    "Error creating Certificate details.", e);
            throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.CREATE_CERTIFICATE_OBJECT_ERROR.getErrorCode(),
                    IdAuthenticationErrorConstants.CREATE_CERTIFICATE_OBJECT_ERROR.getErrorMessage());
        }
        
        String idvid = identityKeyBindingRequestDTO.getIndividualId();
        String idVidHash = securityManager.hash(idvid);

        String uuid = UUID.randomUUID().toString();
        IdentityBindingCertificateStore bindingCertStore = new IdentityBindingCertificateStore();
        bindingCertStore.setCertId(uuid);
        bindingCertStore.setIdVidHash(idVidHash);
        bindingCertStore.setToken(token);
        bindingCertStore.setCertificateData(certificateData);
        bindingCertStore.setCertThumbprint(certThumbprint);
        bindingCertStore.setPublicKeyHash(IdAuthSecurityManager.generateHashAndDigestAsPlainText(publicKey.getEncoded()));
        bindingCertStore.setPartnerName(partnerId);
        bindingCertStore.setCertExpireDateTime(notAfterDate);
        bindingCertStore.setAuthFactor(identityKeyBindingRequestDTO.getIdentityKeyBinding().getAuthFactorType());
        bindingCertStore.setCreatedBy(EnvUtil.getAppId());
		bindingCertStore.setCrDTimes(DateUtils.getUTCCurrentDateTime());
        updateCertDataForSameTokenId(token, partnerId, certificateData, certThumbprint, notAfterDate);
		bindingCertificateRepo.saveAndFlush(bindingCertStore);
        return certificateData;
    }

    private PublicKey createPublicKeyObject(Map<String, Object> publicKeyJWK) 
        throws IdAuthenticationBusinessException{
        
        try {
            String publicKeyModulus = (String) publicKeyJWK.get(IdAuthCommonConstants.PUBLIC_KEY_MODULUS_KEY);
            String publicKeyExponent = (String) publicKeyJWK.get(IdAuthCommonConstants.PUBLIC_KEY_EXPONENT_KEY);
            KeyFactory keyfactory = KeyFactory.getInstance(IdAuthCommonConstants.ALGORITHM_RSA);
            BigInteger modulus = new BigInteger(1, CryptoUtil.decodeBase64Url(publicKeyModulus));
            BigInteger exponent = new BigInteger(1, CryptoUtil.decodeBase64Url(publicKeyExponent));
            return keyfactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "createPublicKeyObject",
                                    "Error Building Public Key Object.", e);
            throw new IdAuthenticationBusinessException(
                    IdAuthenticationErrorConstants.CREATE_PUBLIC_KEY_OBJECT_ERROR.getErrorCode(),
                    IdAuthenticationErrorConstants.CREATE_PUBLIC_KEY_OBJECT_ERROR.getErrorMessage());
        } 
    }

    private String getIdentityNameData(Map<String, List<IdentityInfoDTO>> identityInfo) {
        // reading the name value for the certificate CN value. 
        // Need to re-check this again
        List<String> idNames = idMappingConfig.getName();
        StringBuilder strBuilder = new StringBuilder();
        for (String idName: idNames) {
            List<IdentityInfoDTO> idInfoList = identityInfo.get(idName);
            if (Objects.isNull(idInfoList) || idInfoList.isEmpty())
                continue;
            for (IdentityInfoDTO identityInfoData : idInfoList) {
                if (identityInfoData.getLanguage().equalsIgnoreCase(defaultLangCode)) {
                    if (strBuilder.length() > 0) 
                        strBuilder.append(" ");
                    strBuilder.append(identityInfoData.getValue());
                }
            }
        }
        return strBuilder.toString();
    }

    private CertificateParameters getCertificateParameters(String cn, LocalDateTime notBefore, 
                                        LocalDateTime notAfter) {

		CertificateParameters certParams = new CertificateParameters();
        certParams.setCommonName(cn);
		certParams.setNotBefore(notBefore);
		certParams.setNotAfter(notAfter);
        return certParams;
	}

    private void updateCertDataForSameTokenId(String tokenId, String partnerName, String certificateData, 
            String certThumbprint, LocalDateTime notAfterDate) {
        int updateCount = bindingCertificateRepo.updateBindingCertificateForSameToken(tokenId,
                         partnerName, certificateData, certThumbprint, notAfterDate);
        logger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "updateCertDataForSameTokenId",
                         String.format("Total Updated Count for Token Id: %s, count: %s.", tokenId, updateCount));
    }

    private String getTokenId(String idVidHash, String idvIdType) throws IdAuthenticationBusinessException{
        // Duplicate data fetch, to check id exists
        // check already available in IdServiceImpl
        List<Object[]> identityData = identityRepo.findDemoDataById(idVidHash);
        
        if (Objects.isNull(identityData) || identityData.isEmpty()) {
            logger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "getIdentity",
						"Id not found in DB");
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
                    idvIdType));
        }
        return String.valueOf(identityData.get(0)[4]);
    }
}
