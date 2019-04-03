package io.mosip.registration.processor.packet.service.util.encryptor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.service.dto.PublicKeyResponseDto;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;

/**
 * 
 * The Class EncryptorUtil.
 * 
 * @author Sowmya
 */
@Component
public class EncryptorUtil {

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/** The encryptor. */
	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/** The Constant RSA. */
	public static final String RSA = "RSA";

	/** The Constant AES_KEY_CIPHER_SPLITTER. */
	public static final String AES_KEY_CIPHER_SPLITTER = "#KEY_SPLITTER#";

	/** The Constant APPLICATION_ID. */
	public static final String APPLICATION_ID = "REGISTRATION";

	/** The registration processor rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	/** The center id length. */
	@Value("${mosip.kernel.rid.centerid-length}")
	private int centerIdLength;

	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	/**
	 * Encrypt uin update packet.
	 *
	 * @param decryptedFile
	 *            the decrypted file
	 * @param regId
	 *            the reg id
	 * @param creationTime
	 *            the creation time
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws InvalidKeySpecException
	 *             the invalid key spec exception
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public void encryptUinUpdatePacket(InputStream decryptedFile, String regId, String creationTime) throws IOException,
			ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException, RegBaseCheckedException {
		try (InputStream decryptedPacketStream = new BufferedInputStream(decryptedFile);
				InputStream encryptPacketStream = encrypt(decryptedPacketStream, regId, creationTime)) {// close input
																										// stream
			byte[] bytes = IOUtils.toByteArray(encryptPacketStream);

			filemanager.put(regId, new ByteArrayInputStream(bytes), DirectoryPathDto.PACKET_GENERATED_ENCRYPTED);

		}
	}

	/**
	 * Encrypt.
	 *
	 * @param streamToEncrypt
	 *            the stream to encrypt
	 * @param regId
	 *            the reg id
	 * @param creationTime
	 *            the creation time
	 * @return the input stream
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws InvalidKeySpecException
	 *             the invalid key spec exception
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public InputStream encrypt(final InputStream streamToEncrypt, String regId, String creationTime)
			throws ApisResourceAccessException, InvalidKeySpecException, java.security.NoSuchAlgorithmException,
			IOException, RegBaseCheckedException {

		try {

			String centerId = regId.substring(0, centerIdLength);

			byte[] dataToEncrypt = IOUtils.toByteArray(streamToEncrypt);

			// Enable AES 256 bit encryption
			Security.setProperty("crypto.policy", "unlimited");
			System.out.println("1");
			// Generate AES Session Key
			final SecretKey symmetricKey = keyGenerator.getSymmetricKey();
			System.out.println("2");
			// Encrypt the Data using AES
			final byte[] encryptedData = encryptor.symmetricEncrypt(symmetricKey, dataToEncrypt);
			System.out.println("3");
			// Encrypt the AES Session Key using RSA
			final byte[] rsaEncryptedKey = encryptRSA(symmetricKey.getEncoded(), centerId, creationTime);
			System.out.println("4");
			return new ByteArrayInputStream(CryptoUtil
					.encodeBase64(CryptoUtil.combineByteArray(encryptedData, rsaEncryptedKey, AES_KEY_CIPHER_SPLITTER))
					.getBytes());

		} catch (MosipInvalidDataException mosipInvalidDataException) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_ENCRYPTOR_INVLAID_DATA_EXCEPTION,
					mosipInvalidDataException);

		} catch (MosipInvalidKeyException mosipInvalidKeyException) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_ENCRYPTOR_INVLAID_KEY_EXCEPTION,
					mosipInvalidKeyException);

		} catch (RuntimeException runtimeException) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_SYS_SERVER_ERROR, runtimeException);
		}
	}

	/**
	 * Encrypt RSA.
	 *
	 * @param sessionKey
	 *            the session key
	 * @param centerId
	 *            the center id
	 * @param creationTime
	 *            the creation time
	 * @return the byte[]
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws InvalidKeySpecException
	 *             the invalid key spec exception
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 */
	private byte[] encryptRSA(final byte[] sessionKey, String centerId, String creationTime)
			throws ApisResourceAccessException, InvalidKeySpecException, java.security.NoSuchAlgorithmException {

		// encrypt AES Session Key using RSA public key
		List<String> pathsegments = new ArrayList<>();

		pathsegments.add(APPLICATION_ID);

		String publicKeytest = (String) registrationProcessorRestClientService.getApi(ApiName.ENCRYPTIONSERVICE,
				pathsegments, "timeStamp,referenceId", creationTime + ',' + centerId, String.class);

		Gson gsonObj = new Gson();
		PublicKeyResponseDto publicKeyResponsedto = gsonObj.fromJson(publicKeytest, PublicKeyResponseDto.class);
		PublicKey publicKey = KeyFactory.getInstance(RSA)
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKeyResponsedto.getPublicKey())));

		return encryptor.asymmetricPublicEncrypt(publicKey, sessionKey);

	}

}
