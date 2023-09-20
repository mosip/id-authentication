package io.mosip.authentication.common.service.transaction.manager;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.cryptomanager.dto.JWTEncryptRequestDto;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TokenEncoderUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.idrepository.core.util.SaltUtil;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.keymanager.model.CertificateParameters;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.retry.WithRetry;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.JWTCipherResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.cryptomanager.util.CryptomanagerUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanager.hsm.util.CertificateUtility;
import io.mosip.kernel.keymanagerservice.dto.SignatureCertificate;
import io.mosip.kernel.keymanagerservice.entity.DataEncryptKeystore;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.repository.DataEncryptKeystoreRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import io.mosip.kernel.signature.constant.SignatureConstant;
import io.mosip.kernel.signature.dto.JWSSignatureRequestDto;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

/**
 * The Class IdAuthSecurityManager.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthSecurityManager {

	private static final String HASH_ALGORITHM_NAME = "SHA-256";
	
	/** The Constant SALT_FOR_THE_GIVEN_ID. */
	private static final String SALT_FOR_THE_GIVEN_ID = "Salt for the given ID";

	/** The application id. */
	@Value("${application.id}")
	private String applicationId;

	/** The reference id. */
	@Value("${identity-cache.reference.id}")
	private String referenceId;

	/** The Constant ENCRYPT_DECRYPT_DATA. */
	private static final String ENCRYPT_DECRYPT_DATA = "encryptDecryptData";

	/** The Constant ID_AUTH_TRANSACTION_MANAGER. */
	private static final String ID_AUTH_TRANSACTION_MANAGER = "IdAuthSecurityManager";

	/** The mosip logger. */
	private Logger mosipLogger = IdaLogger.getLogger(IdAuthSecurityManager.class);

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

	/** The token ID length. */
	@Value("${mosip.kernel.tokenid.length}")
	private int tokenIDLength;

	/** KeySplitter. */
	@Value("${" + IdAuthConfigKeyConstants.KEY_SPLITTER + "}")
	private String keySplitter;

	/** The token ID length. */
	@Value("${mosip.ida.kyc.token.secret}")
	private String kycTokenSecret;

	@Value("${mosip.ida.kyc.exchange.sign.include.certificate:false}")
	private boolean includeCertificate;

	/** The sign applicationid. */
	@Value("${mosip.ida.kyc.exchange.sign.applicationid:IDA_KYC_EXCHANGE}")
	private String kycExchSignApplicationId;

	@Value("${mosip.ida.kyc.exchange.sign.applicationid:IDA_KEY_BINDING}")
	private String idKeyBindSignKeyAppId;

	@Value("${mosip.kernel.certificate.sign.algorithm:SHA256withRSA}")
    private String signAlgorithm;

	/** The sign applicationid. */
	@Value("${mosip.ida.vci.exchange.sign.applicationid:IDA_VCI_EXCHANGE}")
	private String vciExchSignApplicationId;

	/** The uin hash salt repo. */
	@Autowired
	private IdaUinHashSaltRepo uinHashSaltRepo;

	/** The repo. */
	@Autowired
	private DataEncryptKeystoreRepository repo;

	/** The zk crypto manager service. */
	@Autowired
	private ZKCryptoManagerService zkCryptoManagerService;

	/** The crypto core. */
	@Autowired
	private CryptoCore cryptoCore;

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/** The cryptomanager utils. */
	@Autowired
	private CryptomanagerUtils cryptomanagerUtils;

	@Autowired
    private KeymanagerService keymanagerService;

	@Autowired
    private KeyStore keyStore;

	@Autowired
    private KeymanagerUtil keymanagerUtil;
	
	@Value("${mosip.ida.idhash.legacy-salt-selection-enabled:false}")
	private boolean legacySaltSelectionEnabled;
	
	@Autowired
	private IdentityCacheRepository identityRepo;
	
	@Autowired
	private IdTypeUtil idTypeUtil;
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return EnvUtil.getIdaAuthClientId();
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
			request.setApplicationId(EnvUtil.getAppId());
			request.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			request.setData(dataToEncrypt);
			request.setReferenceId(refId);
			request.setAad(aad);
			request.setSalt(saltToEncrypt);
			return CryptoUtil.decodeBase64Url(cryptomanagerService.encrypt(request).getData());
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
	 * @param isThumbprintEnabled the is thumbprint enabled
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@WithRetry
	public byte[] decrypt(String dataToDecrypt, String refId, String aad, String saltToDecrypt,
			Boolean isThumbprintEnabled) throws IdAuthenticationBusinessException {
		try {
			CryptomanagerRequestDto request = new CryptomanagerRequestDto();
			request.setApplicationId(EnvUtil.getAppId());
			request.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			request.setData(dataToDecrypt);
			request.setReferenceId(refId);
			request.setAad(aad);
			request.setSalt(saltToDecrypt);
			//request.setPrependThumbprint(isThumbprintEnabled);
			return CryptoUtil.decodeBase64Url(cryptomanagerService.decrypt(request).getData());
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

	/**
	 * Re encrypt random key.
	 *
	 * @param encryptedKey the encrypted key
	 * @return the string
	 */
	@WithRetry
	public String reEncryptRandomKey(String encryptedKey) {
		ReEncryptRandomKeyResponseDto zkReEncryptRandomKeyRespDto = zkCryptoManagerService
				.zkReEncryptRandomKey(encryptedKey);
		return zkReEncryptRandomKeyRespDto.getEncryptedKey();
	}

	/**
	 * Re encrypt and store random key.
	 *
	 * @param index the index
	 * @param key the key
	 */
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

	/**
	 * Zk decrypt.
	 *
	 * @param id the id
	 * @param encryptedAttributes the encrypted attributes
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
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

	/**
	 * Creates the random token.
	 *
	 * @param dataToEncrypt the data to encrypt
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
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
		request.setDataToSign(CryptoUtil.encodeBase64Url(data.getBytes()));
		request.setIncludeCertHash(true);
		request.setIncludeCertificate(true);
		request.setIncludePayload(false);
		request.setReferenceId(signRefid);
		return signatureService.jwtSign(request).getJwtSignedData();
	}

	/**
	 * Verify signature.
	 *
	 * @param signature the signature
	 * @param domain the domain
	 * @param requestData the request data
	 * @param isTrustValidationRequired the is trust validation required
	 * @return true, if successful
	 */
	public boolean verifySignature(String signature, String domain, String requestData,
			Boolean isTrustValidationRequired) {
		JWTSignatureVerifyRequestDto jwtSignatureVerifyRequestDto = new JWTSignatureVerifyRequestDto();
		jwtSignatureVerifyRequestDto.setApplicationId(signApplicationid);
		jwtSignatureVerifyRequestDto.setReferenceId(signRefid);
		if (Objects.nonNull(requestData)) {
			jwtSignatureVerifyRequestDto.setActualData(CryptoUtil.encodeBase64Url(requestData.getBytes()));
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

	private String newHash(String id) throws IdAuthenticationBusinessException {
		Integer idModulo = getSaltKeyForHashOfId(id);
		return doGetHashForIdAndSaltKey(id, idModulo);
	}
	
	private String legacyHash(String id) throws IdAuthenticationBusinessException {
		Integer idModulo = getSaltKeyForId(id);
		return doGetHashForIdAndSaltKey(id, idModulo);
	}
	
	public String hash(String id) throws IdAuthenticationBusinessException {
		String hashWithNewMethod = null;
		try {
			hashWithNewMethod = newHash(id);
		} catch (IdAuthenticationBusinessException e) {
			//If salt key is not present in the DB, this error will occur.
			if (e.getErrorCode().equals(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode())) {
				//If legacy hash is not selected throw back the error.
				if(!legacySaltSelectionEnabled) {
					mosipLogger.error("Salt key is missing in the table");
					throw e;
				}
				// Ignoring this error.
				mosipLogger
						.debug("Ignoring missing salt key in the table as legacy salt selection will be used further.");
			} else {
				throw e;
			}
		}
		// If either salt key is not present in uin_hash_salt table
		// or the new hash is not present in the identity_cache table
		if(hashWithNewMethod == null || !identityRepo.existsById(hashWithNewMethod)) {
			if(!legacySaltSelectionEnabled) {
				//Throw error
				throwIdNotAvailabeError(id);
			}
			
			String hashWithLegacyMethod = legacyHash(id);
			if(!identityRepo.existsById(hashWithLegacyMethod)) {
				//Throw error
				throwIdNotAvailabeError(id);
			}
			
			return hashWithLegacyMethod;
		}
		return hashWithNewMethod;
	}

	private void throwIdNotAvailabeError(String id) throws IdAuthenticationBusinessException {
		throw new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
				String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
						idTypeUtil.getIdType(id)));
	}

	private String doGetHashForIdAndSaltKey(String id, Integer idModulo) throws IdAuthenticationBusinessException {
		String hashSaltValue = uinHashSaltRepo.retrieveSaltById(idModulo);
		if (hashSaltValue != null) {
			try {
				return HMACUtils2.digestAsPlainTextWithSalt(id.getBytes(), hashSaltValue.getBytes());
			} catch (NoSuchAlgorithmException e) {
				mosipLogger.error(String.format("No such algorithm exception: %s", e.getMessage()));
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
			}
		} else {
			mosipLogger.error(String.format("salt hash value does not exist for modulo: %s", idModulo));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(),
							SALT_FOR_THE_GIVEN_ID));
		}
	}

	public int getSaltKeyForId(String id) {
		Integer saltKeyLength = EnvUtil.getSaltKeyLength();
		return SaltUtil.getIdvidModulo(id, saltKeyLength);
	}

	/**
	 * Gets the x 509 certificate.
	 *
	 * @param partnerCertificate the partner certificate
	 * @return the x 509 certificate
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
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

	/**
	 * Encrypt data.
	 *
	 * @param data the data
	 * @param partnerCertificate the partner certificate
	 * @return the tuple 2
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Tuple3<String, String, String> encryptData(byte[] data, String partnerCertificate) throws IdAuthenticationBusinessException {
		X509Certificate x509Certificate = getX509Certificate(partnerCertificate);
		PublicKey publicKey = x509Certificate.getPublicKey();
		Tuple2<byte[], byte[]> encryptedData = encrypt(publicKey, data);
		byte[] certificateThumbprint = cryptomanagerUtils.getCertificateThumbprint(x509Certificate);
		return Tuples.of(CryptoUtil.encodeBase64Url(encryptedData.getT1()), CryptoUtil.encodeBase64Url(encryptedData.getT2()), digestAsPlainText(certificateThumbprint));
	}

	/**
	 * Encrypt.
	 *
	 * @param publicKey the public key
	 * @param dataToEncrypt the data to encrypt
	 * @return the byte[]
	 */
	public Tuple2<byte[], byte[]> encrypt(PublicKey publicKey, byte[] dataToEncrypt) {
		SecretKey secretKey = keyGenerator.getSymmetricKey();
		byte[] encryptedData = cryptoCore.symmetricEncrypt(secretKey, dataToEncrypt, null);
		byte[] encryptedSymmetricKey = cryptoCore.asymmetricEncrypt(publicKey, secretKey.getEncoded());
		return Tuples.of(encryptedSymmetricKey,encryptedData);
	}

	/**
	 * Combine data to encrypt.
	 *
	 * @param encryptedData the encrypted data
	 * @param encryptedSymmetricKey the encrypted symmetric key
	 * @return the byte[]
	 */
	public byte[] combineDataToEncrypt(byte[] encryptedData, byte[] encryptedSymmetricKey) {
		return CryptoUtil.combineByteArray(encryptedData, encryptedSymmetricKey, keySplitter);
	}

	/**
	 * Trim begin end.
	 *
	 * @param pKey the key
	 * @return the string
	 */
	public static String trimBeginEnd(String pKey) {
		pKey = pKey.replaceAll("-{0,30}BEGIN([^-]{0,30})-{0,30}(\r?\n)?", "");
		pKey = pKey.replaceAll("-{0,30}END([^-]{0,30})-{0,30}(\r?\n)?", "");
		pKey = pKey.replaceAll("\\s", "");
		return pKey;
	}

	/**
	 * Digest as plain text.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String digestAsPlainText(byte[] data) {
		return toHex(data);
	}

	/**
	 * Generate hash and digest as plain text.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String generateHashAndDigestAsPlainText(byte[] data) {
		try {
			return digestAsPlainText(generateHash(data));
		} catch (NoSuchAlgorithmException e) {
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	/**
	 * Generate hash.
	 *
	 * @param data the data
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public static byte[] generateHash(final byte[] data) throws NoSuchAlgorithmException {
		try {
			return HMACUtils2.generateHash(data);
		} catch (NoSuchAlgorithmException e) {
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}
	
	/**
	 * Decode hex.
	 *
	 * @param hexData the hex data
	 * @return the byte[]
	 * @throws DecoderException the decoder exception
	 */
	public static byte[] decodeHex(String hexData) throws DecoderException{
        return Hex.decodeHex(hexData);
    }
	
	/**
	 * To hex.
	 *
	 * @param bytes the bytes
	 * @return the string
	 */
	public static String toHex(byte[] bytes) {
        return Hex.encodeHexString(bytes).toUpperCase();
    }
	
	/**
	 * Gets the bytes from thumbprint.
	 *
	 * @param thumbprint the thumbprint
	 * @return the bytes from thumbprint
	 */
	public static byte[] getBytesFromThumbprint(String thumbprint) {
		try {
			//First try decoding with hex
			return decodeHex(thumbprint);
		} catch (DecoderException e) {
			try {
				//Then try decoding with base64
				return CryptoUtil.decodeBase64Url(thumbprint);
			} catch (Exception ex) {
				throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, ex);
			}
		}
	}
	
	private int getSaltKeyForHashOfId(String id) {
		Integer saltKeyLength = EnvUtil.getSaltKeyLength();
		return SaltUtil.getIdvidHashModulo(id, saltKeyLength);
	}
	
	public String generateKeyedHash(byte[] bytesToHash) {
		try {
			// Need to get secret from HSM  
			byte[] tokenSecret = CryptoUtil.decodeBase64Url(kycTokenSecret);
			MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);
			messageDigest.update(bytesToHash);
			messageDigest.update(tokenSecret);
			byte[] tokenHash = messageDigest.digest();

			return TokenEncoderUtil.encodeBase58(tokenHash);
		} catch (NoSuchAlgorithmException e) {
			mosipLogger.error("Error generating Keyed Hash", e);
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	@WithRetry
	public String signWithPayload(String data) {
		JWTSignatureRequestDto request = new JWTSignatureRequestDto();
		request.setApplicationId(kycExchSignApplicationId);
		request.setDataToSign(CryptoUtil.encodeBase64Url(data.getBytes()));
		request.setIncludeCertHash(false);
		request.setIncludeCertificate(includeCertificate);
		request.setIncludePayload(true);
		request.setReferenceId(IdAuthCommonConstants.EMPTY);
		return signatureService.jwtSign(request).getJwtSignedData();
	}

	@WithRetry
	public String jwsSignWithPayload(String data) {
		JWSSignatureRequestDto request = new JWSSignatureRequestDto();
		request.setApplicationId(vciExchSignApplicationId);
		request.setDataToSign(CryptoUtil.encodeBase64Url(data.getBytes()));
		request.setIncludeCertHash(false);
		request.setIncludeCertificate(includeCertificate);
		request.setIncludePayload(false);
		request.setReferenceId(IdAuthCommonConstants.EMPTY);
		request.setB64JWSHeaderParam(false);
		request.setValidateJson(false);
		return signatureService.jwsSign(request).getJwtSignedData();
	}

	@WithRetry
	public Entry<String, String> generateKeyBindingCertificate(PublicKey publicKey, CertificateParameters certParams) 
				throws CertificateEncodingException {
		String timestamp = DateUtils.getUTCCurrentDateTimeString();
		SignatureCertificate certificateResponse = keymanagerService.getSignatureCertificate(idKeyBindSignKeyAppId,
                                                        Optional.of(IdAuthCommonConstants.EMPTY), timestamp);
		PrivateKey signPrivateKey = certificateResponse.getCertificateEntry().getPrivateKey();
		X509Certificate signCert = certificateResponse.getCertificateEntry().getChain()[0];
		X500Principal signerPrincipal = signCert.getSubjectX500Principal();
		// Need to add new method to keymanager CertificateUtility class to generate certificate without CA 
		// and digital signature key usage
		X509Certificate signedCert = CertificateUtility.generateX509Certificate(signPrivateKey, publicKey, certParams,
                signerPrincipal, signAlgorithm, keyStore.getKeystoreProviderName(), false);
		String certThumbprint = generateHashAndDigestAsPlainText(signedCert.getEncoded());
		String certificateData = keymanagerUtil.getPEMFormatedData(signedCert);

		return new SimpleEntry<>(certThumbprint, certificateData);
	}

	@WithRetry
	public String jwtEncrypt(String dataToEncrypt, String certificateData) {
		JWTEncryptRequestDto encryptRequestDto = new JWTEncryptRequestDto();
		encryptRequestDto.setData(CryptoUtil.encodeBase64Url(dataToEncrypt.getBytes()));
		encryptRequestDto.setX509Certificate(certificateData);
		encryptRequestDto.setEnableDefCompression(true);
		encryptRequestDto.setIncludeCertHash(true);
		JWTCipherResponseDto cipherResponseDto = cryptomanagerService.jwtEncrypt(encryptRequestDto);
		return cipherResponseDto.getData();
	}
}
