package io.mosip.authentication.common.service.transaction.manager;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.retry.WithRetry;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.entity.DataEncryptKeystore;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.repository.DataEncryptKeystoreRepository;
import io.mosip.kernel.signature.constant.SignatureConstant;
import io.mosip.kernel.signature.dto.JWTSignatureRequestDto;
import io.mosip.kernel.signature.dto.JWTSignatureVerifyRequestDto;
import io.mosip.kernel.signature.dto.JWTSignatureVerifyResponseDto;
import io.mosip.kernel.signature.service.SignatureService;
import io.mosip.kernel.zkcryptoservice.constant.ZKCryptoManagerConstants;
import io.mosip.kernel.zkcryptoservice.dto.CryptoDataDto;
import io.mosip.kernel.zkcryptoservice.dto.ReEncryptRandomKeyResponseDto;
import io.mosip.kernel.zkcryptoservice.dto.ZKCryptoRequestDto;
import io.mosip.kernel.zkcryptoservice.dto.ZKCryptoResponseDto;
import io.mosip.kernel.zkcryptoservice.service.spi.ZKCryptoManagerService;

/**
 * The Class IdAuthSecurityManager.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthSecurityManager {

	private static final String SALT_FOR_THE_GIVEN_ID = "Salt for the given ID";

	@Value("${application.id}")
	private String applicationId;

	@Value("${identity-cache.reference.id}")
	private String referenceId;

	/** The Constant ENCRYPT_DECRYPT_DATA. */
	private static final String ENCRYPT_DECRYPT_DATA = "encryptDecryptData";

	/** The Constant ID_AUTH_TRANSACTION_MANAGER. */
	private static final String ID_AUTH_TRANSACTION_MANAGER = "IdAuthSecurityManager";

	/** The mosip logger. */
	private Logger mosipLogger = IdaLogger.getLogger(IdAuthSecurityManager.class);

	/** The mapper. */
	@Autowired
	private Environment env;

	/** The cryptomanager service. */
	@Autowired
	private CryptomanagerService cryptomanagerService;

	/** The key manager. */
	@Autowired
	private SignatureService signatureService;

	/** The sign applicationid. */
	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;

	/** The sign refid. */
	@Value("${mosip.sign.refid:SIGN}")
	private String signRefid;

	/** The uin hash salt repo. */
	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;

	@Autowired
	private DataEncryptKeystoreRepository repo;

	@Autowired
	private ZKCryptoManagerService zkCryptoManagerService;

	@Autowired
	private CryptoCore cryptoCore;

	@Autowired
	private KeyGenerator keyGenerator;

	@Value("${mosip.kernel.tokenid.length}")
	private int tokenIDLength;

	/** KeySplitter. */
	@Value("${" + IdAuthConfigKeyConstants.KEY_SPLITTER + "}")
	private String keySplitter;

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return env.getProperty(IdAuthConfigKeyConstants.MOSIP_IDA_AUTH_CLIENTID);
	}

	/**
	 * Encrypt.
	 *
	 * @param dataToEncrypt the data to encrypt
	 * @param refId         the ref id
	 * @param aad           the aad
	 * @param saltToEncrypt the salt to encrypt
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@WithRetry
	public byte[] encrypt(String dataToEncrypt, String refId, String aad, String saltToEncrypt)
			throws IdAuthenticationBusinessException {
		try {
			CryptomanagerRequestDto request = new CryptomanagerRequestDto();
			request.setApplicationId(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			request.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			request.setData(dataToEncrypt);
			request.setReferenceId(refId);
			request.setAad(aad);
			request.setSalt(saltToEncrypt);
			return CryptoUtil.decodeBase64(cryptomanagerService.encrypt(request).getData());
		} catch (NoUniqueAliasException e) {
			// TODO: check whether PUBLICKEY_EXPIRED to be thrown for NoUniqueAliasException
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, e);
		} catch (Exception e) {
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FAILED_TO_ENCRYPT, e);
		}
	}

	/**
	 * Decrypt.
	 *
	 * @param dataToDecrypt the data to decrypt
	 * @param refId         the ref id
	 * @param aad           the aad
	 * @param saltToDecrypt the salt to decrypt
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@WithRetry
	public byte[] decrypt(String dataToDecrypt, String refId, String aad, String saltToDecrypt,
			Boolean isThumbprintEnabled) throws IdAuthenticationBusinessException {
		try {
			CryptomanagerRequestDto request = new CryptomanagerRequestDto();
			request.setApplicationId(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			request.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			request.setData(dataToDecrypt);
			request.setReferenceId(refId);
			request.setAad(aad);
			request.setSalt(saltToDecrypt);
			request.setPrependThumbprint(isThumbprintEnabled);
			return CryptoUtil.decodeBase64(cryptomanagerService.decrypt(request).getData());
		} catch (NoUniqueAliasException e) {
			// TODO: check whether PUBLICKEY_EXPIRED to be thrown for NoUniqueAliasException
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PUBLICKEY_EXPIRED, e);
		} catch (Exception e) {
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_ENCRYPTION, e);
		}
	}

	@WithRetry
	public String reEncryptRandomKey(String encryptedKey) {
		ReEncryptRandomKeyResponseDto zkReEncryptRandomKeyRespDto = zkCryptoManagerService
				.zkReEncryptRandomKey(encryptedKey);
		return zkReEncryptRandomKeyRespDto.getEncryptedKey();
	}

	public void reEncryptAndStoreRandomKey(String index, String key) {
		Integer indexInt = Integer.valueOf(index);
		if (repo.findKeyById(indexInt) == null) {
			String reEncryptedKey = reEncryptRandomKey(key);
			DataEncryptKeystore randomKeyEntity = new DataEncryptKeystore();
			randomKeyEntity.setId(indexInt);
			randomKeyEntity.setKey(reEncryptedKey);
			randomKeyEntity.setCrBy("IDA");
			randomKeyEntity.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			repo.save(randomKeyEntity);
		}
	}

	@WithRetry
	public Map<String, String> zkDecrypt(String id, Map<String, String> encryptedAttributes)
			throws IdAuthenticationBusinessException {
		ZKCryptoRequestDto cryptoRequestDto = new ZKCryptoRequestDto();
		cryptoRequestDto.setId(id);
		List<CryptoDataDto> zkDataAttributes = encryptedAttributes.entrySet().stream()
				.map(entry -> new CryptoDataDto(entry.getKey(), entry.getValue())).collect(Collectors.toList());
		cryptoRequestDto.setZkDataAttributes(zkDataAttributes);
		ZKCryptoResponseDto zkDecryptResponse = zkCryptoManagerService.zkDecrypt(cryptoRequestDto);
		return zkDecryptResponse.getZkDataAttributes().stream()
				.collect(Collectors.toMap(CryptoDataDto::getIdentifier, CryptoDataDto::getValue));

	}

	public String createRandomToken(byte[] dataToEncrypt) throws IdAuthenticationBusinessException {
		SecretKey key = keyGenerator.getSymmetricKey();
		SecureRandom sRandom = new SecureRandom();
		byte[] nonce = new byte[ZKCryptoManagerConstants.GCM_NONCE_LENGTH];
		byte[] aad = new byte[ZKCryptoManagerConstants.GCM_AAD_LENGTH];

		sRandom.nextBytes(nonce);
		sRandom.nextBytes(aad);
		byte[] encryptedData = cryptoCore.symmetricEncrypt(key, dataToEncrypt, nonce, aad);
		String hash;
		hash = IdAuthSecurityManager.generateHashAndDigestAsPlainText(encryptedData);
		return new BigInteger(hash.getBytes()).toString().substring(0, tokenIDLength);
	}

	/**
	 * Sign.
	 *
	 * @param data the data
	 * @return the string
	 */
	@WithRetry
	public String sign(String data) {
		// TODO: check whether any exception will be thrown
		JWTSignatureRequestDto request = new JWTSignatureRequestDto();
		request.setApplicationId(signApplicationid);
		request.setDataToSign(CryptoUtil.encodeBase64(data.getBytes()));
		request.setIncludeCertHash(true);
		request.setIncludeCertificate(true);
		request.setIncludePayload(false);
		request.setReferenceId(signRefid);
		return signatureService.jwtSign(request).getJwtSignedData();
	}

	public boolean verifySignature(String signature, String domain, String requestData,
			Boolean isTrustValidationRequired) {
		JWTSignatureVerifyRequestDto jwtSignatureVerifyRequestDto = new JWTSignatureVerifyRequestDto();
		jwtSignatureVerifyRequestDto.setApplicationId(signApplicationid);
		jwtSignatureVerifyRequestDto.setReferenceId(signRefid);
		if (Objects.nonNull(requestData)) {
			jwtSignatureVerifyRequestDto.setActualData(CryptoUtil.encodeBase64(requestData.getBytes()));
		}
		jwtSignatureVerifyRequestDto.setJwtSignatureData(signature);
		jwtSignatureVerifyRequestDto.setValidateTrust(isTrustValidationRequired);
		jwtSignatureVerifyRequestDto.setDomain(domain);
		JWTSignatureVerifyResponseDto jwtResponse = signatureService.jwtVerify(jwtSignatureVerifyRequestDto);
		mosipLogger.info(getUser(), ID_AUTH_TRANSACTION_MANAGER, "verifySignature",
				"SIGNATURE VALID : " + jwtResponse.isSignatureValid() + " - TRUST VALID : "
						+ jwtResponse.getTrustValid().contentEquals(SignatureConstant.TRUST_VALID));
		return isTrustValidationRequired
				? jwtResponse.isSignatureValid()
						&& jwtResponse.getTrustValid().contentEquals(SignatureConstant.TRUST_VALID)
				: jwtResponse.isSignatureValid();
	}

	public String hash(String id) throws IdAuthenticationBusinessException {
		int saltModuloConstant = env.getProperty(IdAuthConfigKeyConstants.UIN_SALT_MODULO, Integer.class);
		Long idModulo = (Long.parseLong(id) % saltModuloConstant);
		String hashSaltValue = uinHashSaltRepo.retrieveSaltById(idModulo);
		if (hashSaltValue != null) {
			try {
				return HMACUtils2.digestAsPlainTextWithSalt(id.getBytes(), hashSaltValue.getBytes());
			} catch (NoSuchAlgorithmException e) {
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
			}
		} else {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
							SALT_FOR_THE_GIVEN_ID));
		}
	}

	private X509Certificate getX509Certificate(String partnerCertificate) throws IdAuthenticationBusinessException {
		try {
			String certificate = IdAuthSecurityManager.trimBeginEnd(partnerCertificate);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate x509cert = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(java.util.Base64.getDecoder().decode(certificate)));
			return x509cert;
		} catch (CertificateException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	public String encryptData(byte[] data, String partnerCertificate) throws IdAuthenticationBusinessException {
		X509Certificate x509Certificate = getX509Certificate(partnerCertificate);
		PublicKey publicKey = x509Certificate.getPublicKey();
		byte[] encryptedData = encrypt(publicKey, data);
		return CryptoUtil.encodeBase64(encryptedData);
	}

	public byte[] encrypt(PublicKey publicKey, byte[] dataToEncrypt) {
		SecretKey secretKey = keyGenerator.getSymmetricKey();
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKey, dataToEncrypt, null);
		byte[] encryptedSymmetricKey = cryptoCore.asymmetricEncrypt(publicKey, secretKey.getEncoded());
		return combineDataToEncrypt(encryptedData, encryptedSymmetricKey);
	}

	public byte[] combineDataToEncrypt(byte[] encryptedData, byte[] encryptedSymmetricKey) {
		return CryptoUtil.combineByteArray(encryptedData, encryptedSymmetricKey, keySplitter);
	}

	public static String trimBeginEnd(String pKey) {
		pKey = pKey.replaceAll("-*BEGIN([^-]*)-*(\r?\n)?", "");
		pKey = pKey.replaceAll("-*END([^-]*)-*(\r?\n)?", "");
		pKey = pKey.replaceAll("\\s", "");
		return pKey;
	}

	public static String digestAsPlainText(byte[] data) {
		return DatatypeConverter.printHexBinary(data).toUpperCase();
	}

	public static String generateHashAndDigestAsPlainText(byte[] data) {
		try {
			return HMACUtils2.digestAsPlainText(data);
		} catch (NoSuchAlgorithmException e) {
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
}
