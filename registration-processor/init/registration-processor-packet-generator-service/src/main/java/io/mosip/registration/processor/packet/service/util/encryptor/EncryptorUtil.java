package io.mosip.registration.processor.packet.service.util.encryptor;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
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
	@Value("${mosip.kernel.registrationcenterid.length}")
	private int centerIdLength;
	
	/** The center id length. */
	@Value("${mosip.kernel.machineid.length}")
	private int machineIdLength;
	
	@Value("${registration.processor.rid.machineidsubstring}")
	private int machineIdSubStringLength;

	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	private ObjectMapper mapper=new ObjectMapper();

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
	public byte[] encryptUinUpdatePacket(InputStream decryptedFile, String regId, String creationTime) throws IOException,
			ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException, RegBaseCheckedException {
		byte[] dataToEncrypt = IOUtils.toByteArray(decryptedFile);
	byte[] encryptPacketByteArray = encrypt(dataToEncrypt, regId, creationTime).getBytes();
	return encryptPacketByteArray;
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
	public String encrypt(byte[] dataToEncrypt, String regId, String creationTime) throws ApisResourceAccessException,
		InvalidKeySpecException, java.security.NoSuchAlgorithmException, IOException, RegBaseCheckedException {

		try {

			String centerId = regId.substring(0, centerIdLength);
			String machineId = regId.substring(centerIdLength, machineIdSubStringLength);
			String refId = centerId + "_" + machineId;

			//byte[] dataToEncrypt = IOUtils.toByteArray(streamToEncrypt);

			// Enable AES 256 bit encryption
			Security.setProperty("crypto.policy", "unlimited");
			// Generate AES Session Key
			final SecretKey symmetricKey = keyGenerator.getSymmetricKey();
			// Encrypt the Data using AES
			final byte[] encryptedData = encryptor.symmetricEncrypt(symmetricKey, dataToEncrypt);
			// Encrypt the AES Session Key using RSA
			final byte[] rsaEncryptedKey = encryptRSA(symmetricKey.getEncoded(), refId, creationTime);
			return CryptoUtil.encodeBase64(CryptoUtil.combineByteArray(encryptedData, rsaEncryptedKey, AES_KEY_CIPHER_SPLITTER));

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
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private byte[] encryptRSA(final byte[] sessionKey, String refId, String creationTime)
			throws ApisResourceAccessException, InvalidKeySpecException, java.security.NoSuchAlgorithmException, IOException {

		// encrypt AES Session Key using RSA public key
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(APPLICATION_ID);
		ResponseWrapper<?> responseWrapper;
		PublicKeyResponseDto publicKeyResponsedto=null;

		responseWrapper = (ResponseWrapper<?>) registrationProcessorRestClientService.getApi(ApiName.ENCRYPTIONSERVICE,
				pathsegments, "timeStamp,referenceId", creationTime + ',' + refId, ResponseWrapper.class);
		publicKeyResponsedto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()), PublicKeyResponseDto.class);

		PublicKey publicKey = KeyFactory.getInstance(RSA)
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKeyResponsedto.getPublicKey())));

		return encryptor.asymmetricPublicEncrypt(publicKey, sessionKey);

	}

}
