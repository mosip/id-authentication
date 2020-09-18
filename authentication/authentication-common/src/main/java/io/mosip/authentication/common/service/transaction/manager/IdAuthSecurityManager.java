package io.mosip.authentication.common.service.transaction.manager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.keymanagerservice.entity.DataEncryptKeystore;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.repository.DataEncryptKeystoreRepository;
import io.mosip.kernel.signature.dto.SignRequestDto;
import io.mosip.kernel.signature.service.SignatureService;
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

	@Value("${mosip.kernel.keymanager.softhsm.config-path}")
	private String configPath;

	@Value("${mosip.kernel.crypto.symmetric-algorithm-name}")
	private String aesGCMTransformation;

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
	 * @param dataToEncrypt
	 *            the data to encrypt
	 * @param refId
	 *            the ref id
	 * @param aad
	 *            the aad
	 * @param saltToEncrypt
	 *            the salt to encrypt
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
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
	 * @param dataToDecrypt
	 *            the data to decrypt
	 * @param refId
	 *            the ref id
	 * @param aad
	 *            the aad
	 * @param saltToDecrypt
	 *            the salt to decrypt
	 * @return the byte[]
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public byte[] decrypt(String dataToDecrypt, String refId, String aad, String saltToDecrypt)
			throws IdAuthenticationBusinessException {
		try {
			CryptomanagerRequestDto request = new CryptomanagerRequestDto();
			request.setApplicationId(env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID));
			request.setTimeStamp(DateUtils.getUTCCurrentDateTime());
			request.setData(dataToDecrypt);
			request.setReferenceId(refId);
			request.setAad(aad);
			request.setSalt(saltToDecrypt);
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
	
	public String reEncryptRandomKey(String encryptedKey) {
		 ReEncryptRandomKeyResponseDto zkReEncryptRandomKeyRespDto = zkCryptoManagerService.zkReEncryptRandomKey(encryptedKey);
		 return zkReEncryptRandomKeyRespDto.getEncryptedKey();
	}
	
	public void reEncryptAndStoreRandomKey(String index, String key) {
		Integer indexInt = Integer.valueOf(index);
		if(repo.findKeyById(indexInt) == null) {
			String reEncryptedKey = reEncryptRandomKey(key);
			DataEncryptKeystore randomKeyEntity = new DataEncryptKeystore();
			randomKeyEntity.setId(indexInt);
			randomKeyEntity.setKey(reEncryptedKey);
			randomKeyEntity.setCrBy("IDA");
			randomKeyEntity.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			repo.save(randomKeyEntity);
		}
	}

	public Map<String, String> zkDecrypt(String id, Map<String, String> encryptedAttributes) throws IdAuthenticationBusinessException {
		ZKCryptoRequestDto cryptoRequestDto = new ZKCryptoRequestDto();
		cryptoRequestDto.setId(id);
		List<CryptoDataDto> zkDataAttributes = encryptedAttributes.entrySet()
														.stream()
														.map(entry -> new CryptoDataDto(entry.getKey(), entry.getValue()))
														.collect(Collectors.toList());
		cryptoRequestDto.setZkDataAttributes(zkDataAttributes);
		ZKCryptoResponseDto zkDecryptResponse = zkCryptoManagerService.zkDecrypt(cryptoRequestDto);
		return zkDecryptResponse.getZkDataAttributes()
							.stream()
							.collect(Collectors.toMap(CryptoDataDto::getIdentifier, CryptoDataDto::getValue));
		
	}
	
	/**
	 * Sign.
	 *
	 * @param data
	 *            the data
	 * @return the string
	 */
	public String sign(String data) {
		// TODO: check whether any exception will be thrown
		SignRequestDto request = new SignRequestDto(data);
		return signatureService.sign(request).getData();
	}

	public String hash(String id) {
		int saltModuloConstant = env.getProperty(IdAuthConfigKeyConstants.UIN_SALT_MODULO, Integer.class);
		Long idModulo = (Long.parseLong(id) % saltModuloConstant);
		String hashSaltValue = uinHashSaltRepo.retrieveSaltById(idModulo);
		return HMACUtils.digestAsPlainTextWithSalt(id.getBytes(), hashSaltValue.getBytes());
	}
}
