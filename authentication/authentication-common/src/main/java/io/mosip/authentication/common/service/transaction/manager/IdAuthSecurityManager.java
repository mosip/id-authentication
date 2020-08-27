package io.mosip.authentication.common.service.transaction.manager;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerErrorConstant;
import io.mosip.kernel.keymanagerservice.entity.DataEncryptKeystore;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.exception.NoUniqueAliasException;
import io.mosip.kernel.keymanagerservice.repository.DataEncryptKeystoreRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyAliasRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;
import io.mosip.kernel.signature.dto.SignatureRequestDto;
import io.mosip.kernel.zkcryptoservice.dto.ReEncryptRandomKeyResponseDto;
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

	private static final String HASH_ALGO = "SHA-256";

	private static final int GCM_NONCE_LENGTH = 12;

	private static final int GCM_AAD_LENGTH = 32;

	private static final String WRAPPING_TRANSFORMATION = "AES/ECB/NoPadding";

	private static final int GCM_TAG_LENGTH = 16;

	private static final int INT_BYTES_LEN = 4;

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
	private KeymanagerService keyManager;

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
	private KeyAliasRepository keyAliasRepository;
	
	@Autowired
	private KeyStore keystore;
	
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

	public byte[] encryptWithAES(String id, byte[] dataToEncrypt) {
		try {
			int randomKeyIndex = getRandomKeyIndex();
			String encryptedKeyData = repo.findKeyById(randomKeyIndex);
			Key secretKey = getDecryptedKey(encryptedKeyData);

			Key derivedKey = getDerivedKey(id, secretKey);

			SecureRandom sRandom = new SecureRandom();
			byte[] nonce = new byte[GCM_NONCE_LENGTH];
			byte[] aad = new byte[GCM_AAD_LENGTH];

			sRandom.nextBytes(nonce);
			sRandom.nextBytes(aad);

			byte[] encryptedData = doCipherOps(derivedKey, dataToEncrypt, Cipher.ENCRYPT_MODE, nonce, aad);
			byte[] dbIndexBytes = getIndexBytes(randomKeyIndex);

			byte[] finalEncData = new byte[encryptedData.length + dbIndexBytes.length + GCM_AAD_LENGTH
					+ GCM_NONCE_LENGTH];
			System.arraycopy(dbIndexBytes, 0, finalEncData, 0, dbIndexBytes.length);
			System.arraycopy(nonce, 0, finalEncData, dbIndexBytes.length, nonce.length);
			System.arraycopy(aad, 0, finalEncData, dbIndexBytes.length + nonce.length, aad.length);
			System.arraycopy(encryptedData, 0, finalEncData, dbIndexBytes.length + nonce.length + aad.length,
					encryptedData.length);
			return Base64.getEncoder().encode(finalEncData);
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.FAILED_TO_ENCRYPT, e);
		}
	}
	
	private int getRandomKeyIndex() {
		List<Integer> indexes = repo.getIdsByKeyStatus(IdAuthCommonConstants.ACTIVE_STATUS);
		int randomNum = ThreadLocalRandom.current().nextInt(0, indexes.size() + 1);
		return indexes.get(randomNum);
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
		if(!repo.existsById(index)) {
			String reEncryptedKey = reEncryptRandomKey(key);
			DataEncryptKeystore randomKeyEntity = new DataEncryptKeystore();
			randomKeyEntity.setId(Integer.valueOf(index));
			randomKeyEntity.setKey(reEncryptedKey);
			randomKeyEntity.setCrBy("IDA");
			randomKeyEntity.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			repo.save(randomKeyEntity);
		}
	}

	public byte[] decryptWithAES(String id, byte[] dataToDecrypt) throws IdAuthenticationBusinessException {
		try {
			byte[] decodedData = Base64.getDecoder().decode(dataToDecrypt);

	        byte[] dbIndexBytes = Arrays.copyOfRange(decodedData, 0, INT_BYTES_LEN);
			byte[] nonce = Arrays.copyOfRange(decodedData, INT_BYTES_LEN, GCM_NONCE_LENGTH + INT_BYTES_LEN);
			byte[] aad = Arrays.copyOfRange(decodedData, INT_BYTES_LEN + GCM_NONCE_LENGTH,
					GCM_AAD_LENGTH + GCM_NONCE_LENGTH + INT_BYTES_LEN);
			byte[] encryptedData = Arrays.copyOfRange(decodedData, INT_BYTES_LEN + GCM_NONCE_LENGTH + GCM_AAD_LENGTH,
					decodedData.length);
			
			int randomKeyIndex = getIndexInt(dbIndexBytes);
			String encryptedKeyData = repo.findKeyById(randomKeyIndex);
			Key secretKey = getDecryptedKey(encryptedKeyData);

			Key derivedKey = getDerivedKey(id, secretKey);

			return doCipherOps(derivedKey, encryptedData, Cipher.DECRYPT_MODE, nonce, aad);
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
					ExceptionUtils.getStackTrace(e));
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.FAILED_TO_ENCRYPT, e);
		}
	}
	
	 private int getIndexInt(byte[] indexBytes) {
	        ByteBuffer bBuff = ByteBuffer.wrap(indexBytes);
	        return bBuff.getInt();
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
		SignatureRequestDto request = new SignatureRequestDto(signApplicationid, signRefid,
				DateUtils.getUTCCurrentDateTimeString(), data);
		return keyManager.sign(request).getData();
	}

	public String hash(String id) {
		int saltModuloConstant = env.getProperty(IdAuthConfigKeyConstants.UIN_SALT_MODULO, Integer.class);
		Long idModulo = (Long.parseLong(id) % saltModuloConstant);
		String hashSaltValue = uinHashSaltRepo.retrieveSaltById(idModulo);
		return HMACUtils.digestAsPlainTextWithSalt(id.getBytes(), hashSaltValue.getBytes());
	}

	private Key getDecryptedKey(String encryptedKey) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(WRAPPING_TRANSFORMATION);

		byte[] encryptedKeyData = Base64.getDecoder().decode(encryptedKey);
		cipher.init(Cipher.DECRYPT_MODE, getMasterKeyFromHSM());
		byte[] unwrappedKey = cipher.doFinal(encryptedKeyData, 0, encryptedKeyData.length);
		return new SecretKeySpec(unwrappedKey, 0, unwrappedKey.length, "AES");
	}

	private Key getDerivedKey(String id, Key key) throws NoSuchAlgorithmException {
		byte[] idBytes = id.getBytes();
		byte[] keyBytes = key.getEncoded();

		MessageDigest mDigest = MessageDigest.getInstance(HASH_ALGO);
		mDigest.update(idBytes, 0, idBytes.length);
		mDigest.update(keyBytes, 0, keyBytes.length);
		byte[] hashBytes = mDigest.digest();

		return new SecretKeySpec(hashBytes, 0, hashBytes.length, "AES");
	}

	private Key getMasterKeyFromHSM() {
		String keyAlias = getKeyAlias();
		if (Objects.nonNull(keyAlias)) {
			return keystore.getSymmetricKey(keyAlias);
		}
		
		mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, "getMasterKeyFromHSM",
				"No Key Alias found.");
		throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.FAILED_TO_FETCH_KEY);
	}

	private String getKeyAlias() {
		List<KeyAlias> keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, referenceId)
				.stream().sorted((alias1, alias2) -> {
					return alias1.getKeyGenerationTime().compareTo(alias2.getKeyGenerationTime());
				}).collect(Collectors.toList());
		List<KeyAlias> currentKeyAliases = keyAliases.stream().filter((keyAlias) -> {
			return isValidTimestamp(DateUtils.getUTCCurrentDateTime(), keyAlias);
		}).collect(Collectors.toList());

		if (!currentKeyAliases.isEmpty() && currentKeyAliases.size() == 1) {
			mosipLogger.info(getUser(), ID_AUTH_TRANSACTION_MANAGER, "getKeyAlias",
					"CurrentKeyAlias size is one. Will decrypt symmetric key for this alias");
			return currentKeyAliases.get(0).getAlias();
		}

		mosipLogger.error(getUser(), ID_AUTH_TRANSACTION_MANAGER, ENCRYPT_DECRYPT_DATA,
				"CurrentKeyAlias is not unique. KeyAlias count: " + currentKeyAliases.size());
		throw new NoUniqueAliasException(KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorCode(),
				KeymanagerErrorConstant.NO_UNIQUE_ALIAS.getErrorMessage());
	}

	private boolean isValidTimestamp(LocalDateTime timeStamp, KeyAlias keyAlias) {
		return timeStamp.isEqual(keyAlias.getKeyGenerationTime()) || timeStamp.isEqual(keyAlias.getKeyExpiryTime())
				|| timeStamp.isAfter(keyAlias.getKeyGenerationTime())
						&& timeStamp.isBefore(keyAlias.getKeyExpiryTime());
	}

	private byte[] doCipherOps(Key key, byte[] data, int mode, byte[] nonce, byte[] aad)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(aesGCMTransformation);
		GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
		cipher.init(mode, key, gcmSpec);
		cipher.updateAAD(aad);
		return cipher.doFinal(data, 0, data.length);
	}

	private byte[] getIndexBytes(int randomIndex) {
		ByteBuffer byteBuff = ByteBuffer.allocate(INT_BYTES_LEN);
		byteBuff.putInt(randomIndex);
		return byteBuff.array();
	}
}
