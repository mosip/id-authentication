package io.mosip.registration.processor.packet.storage.utils;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.crypto.SecretKey;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.processor.core.auth.dto.AuthRequestDTO;
import io.mosip.registration.processor.core.auth.dto.AuthResponseDTO;
import io.mosip.registration.processor.core.auth.dto.AuthTypeDTO;
import io.mosip.registration.processor.core.auth.dto.BioInfo;
import io.mosip.registration.processor.core.auth.dto.DataInfoDTO;
import io.mosip.registration.processor.core.auth.dto.PublicKeyResponseDto;
import io.mosip.registration.processor.core.auth.dto.RequestDTO;
import io.mosip.registration.processor.core.auth.util.BioTypeMapperUtil;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.BioType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.BioTypeException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.RequestWrapper;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.CbeffToBiometricUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.manager.dto.CryptomanagerResponseDto;
import io.mosip.registration.processor.packet.storage.dto.CryptoManagerEncryptDto;

/**
 * @author Ranjitha Siddegowda
 *
 */
public class AuthUtil {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(AuthUtil.class);

	/** The key generator. */
	@Autowired
	private KeyGenerator keyGenerator;

	/** The encryptor. */
	@Autowired
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> encryptor;

	/** The registration processor rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;

	/** The Constant APPLICATION_ID. */
	public static final String IDA_APP_ID = "IDA";

	/** The Constant RSA. */
	public static final String RSA = "RSA";

	/** The Constant RSA. */
	public static final String PARTNER_ID = "INTERNAL";

	@Value("${mosip.identity.auth.internal.requestid}")
	private String authRequestId;

	@Value("${registration.processor.application.id}")
	private String applicationId;

	@Autowired
	private Environment env;

	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String VERSION = "1.0";
	private static final String DUMMY_TRANSACTION_ID = "1234567890";
	private static final String FACE = "FACE";
	private static final String KERNEL_KEY_SPLITTER = "mosip.kernel.data-key-splitter";

	public AuthResponseDTO authByIdAuthentication(String individualId, String individualType, byte[] biometricFile)
			throws ApisResourceAccessException, InvalidKeySpecException, NoSuchAlgorithmException, IOException,
			ParserConfigurationException, SAXException, BiometricException, BioTypeException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), individualId,
				"AuthUtil::authByIdAuthentication()::entry");

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId(authRequestId);
		authRequestDTO.setVersion(VERSION);
		authRequestDTO.setRequestTime(DateUtils.getUTCCurrentDateTimeString());
		authRequestDTO.setTransactionID(DUMMY_TRANSACTION_ID);

		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(Boolean.TRUE);
		authRequestDTO.setRequestedAuth(authType);

		authRequestDTO.setConsentObtained(true);
		authRequestDTO.setIndividualId(individualId);
		authRequestDTO.setIndividualIdType(individualType);
		List<BioInfo> biometrics;
		biometrics = getBiometricsList(biometricFile);
		RequestDTO request = new RequestDTO();
		request.setBiometrics(biometrics);
		request.setTimestamp(DateUtils.getUTCCurrentDateTimeString());
		ObjectMapper mapper = new ObjectMapper();
		String identityBlock = mapper.writeValueAsString(request);

		final SecretKey secretKey = keyGenerator.getSymmetricKey();
		// Encrypted request with session key
		byte[] encryptedIdentityBlock = encryptor.symmetricEncrypt(secretKey, identityBlock.getBytes(), null);
		// rbase64 encoded for request
		authRequestDTO.setRequest(Base64.encodeBase64URLSafeString(encryptedIdentityBlock));
		// encrypted with MOSIP public key and encoded session key
		byte[] encryptedSessionKeyByte = encryptRSA(secretKey.getEncoded(), PARTNER_ID,
				DateUtils.getUTCCurrentDateTimeString(), mapper);
		authRequestDTO.setRequestSessionKey(Base64.encodeBase64URLSafeString(encryptedSessionKeyByte));

		// sha256 of the request block before encryption and the hash is encrypted
		// using the requestSessionKey
		byte[] byteArray = encryptor.symmetricEncrypt(secretKey,
				HMACUtils.digestAsPlainText(HMACUtils.generateHash(identityBlock.getBytes())).getBytes(), null);
		authRequestDTO.setRequestHMAC(Base64.encodeBase64String(byteArray));
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), individualId,
				"AuthUtil::authByIdAuthentication()::INTERNALAUTH POST service call started with request data "
						+ JsonUtil.objectMapperObjectToJson(authRequestDTO));
		AuthResponseDTO response;
		response = (AuthResponseDTO) registrationProcessorRestClientService.postApi(ApiName.INTERNALAUTH, null, null,
				authRequestDTO, AuthResponseDTO.class, MediaType.APPLICATION_JSON);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), individualId,
				"AuthUtil::authByIdAuthentication()::INTERNALAUTH POST service call ended with response data "
						+ JsonUtil.objectMapperObjectToJson(response));

		return response;

	}

	private byte[] encryptRSA(final byte[] sessionKey, String refId, String creationTime, ObjectMapper mapper)
			throws ApisResourceAccessException, InvalidKeySpecException, java.security.NoSuchAlgorithmException,
			IOException {

		// encrypt AES Session Key using RSA public key
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(IDA_APP_ID);
		ResponseWrapper<?> responseWrapper;
		PublicKeyResponseDto publicKeyResponsedto;

		responseWrapper = (ResponseWrapper<?>) registrationProcessorRestClientService.getApi(ApiName.ENCRYPTIONSERVICE,
				pathsegments, "timeStamp,referenceId", creationTime + ',' + refId, ResponseWrapper.class);
		publicKeyResponsedto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
				PublicKeyResponseDto.class);
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), refId,
				"AuthUtil::encryptRSA():: ENCRYPTIONSERVICE GET service call ended with response data "
						+ JsonUtil.objectMapperObjectToJson(responseWrapper));

		PublicKey publicKey = KeyFactory.getInstance(RSA)
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKeyResponsedto.getPublicKey())));

		return encryptor.asymmetricEncrypt(publicKey, sessionKey);

	}

	private DataInfoDTO getBioType(DataInfoDTO dataInfoDTO, String bioType) {

		BioTypeMapperUtil bioTypeMapperUtil = new BioTypeMapperUtil();
		if (bioType.equalsIgnoreCase(BioType.FINGER.toString())) {
			dataInfoDTO.setBioType(bioTypeMapperUtil.getStatusCode(BioType.FINGER));
		} else if (bioType.equalsIgnoreCase(BioType.FACE.toString())) {
			dataInfoDTO.setBioType(bioTypeMapperUtil.getStatusCode(BioType.FACE));
		} else if (bioType.equalsIgnoreCase(BioType.IRIS.toString())) {
			dataInfoDTO.setBioType(bioTypeMapperUtil.getStatusCode(BioType.IRIS));
		}
		return dataInfoDTO;
	}

	private List<BioInfo> getBiometricsList(byte[] cbefByteFile) throws BiometricException, BioTypeException {

		String previousHash = HMACUtils.digestAsPlainText(HMACUtils.generateHash("".getBytes()));
		List<BIR> list;
		CbeffToBiometricUtil CbeffToBiometricUtil = new CbeffToBiometricUtil();
		List<BioInfo> biometrics = new ArrayList<>();
		try {
			list = CbeffToBiometricUtil.convertBIRTYPEtoBIR(CbeffToBiometricUtil.getBIRDataFromXML(cbefByteFile));

			for (BIR bir : list) {
				BioInfo bioInfo = new BioInfo();
				DataInfoDTO dataInfoDTO = new DataInfoDTO();
				BIR birApiResponse = CbeffToBiometricUtil.extractTemplate(bir, null);

				getBioType(dataInfoDTO, birApiResponse.getBdbInfo().getType().get(0).toString());
				List<String> bioSubType = birApiResponse.getBdbInfo().getSubtype();
				// converting list to string
				String bioSubTypeValue = StringUtils.join(bioSubType, " ");
				if (dataInfoDTO.getBioType().equals(BioType.FACE.name()))
					dataInfoDTO.setBioSubType(FACE);
				else
					dataInfoDTO.setBioSubType(bioSubTypeValue);
				String timeStamp = DateUtils.getUTCCurrentDateTimeString();
				SplittedEncryptedData splittedEncryptData = getSessionKey(timeStamp, birApiResponse.getBdb());
				dataInfoDTO.setBioValue(splittedEncryptData.getEncryptedData());
				dataInfoDTO.setTimestamp(timeStamp);
				String encodedData = CryptoUtil
						.encodeBase64String(JsonUtil.objectMapperObjectToJson(dataInfoDTO).getBytes());
				bioInfo.setData(encodedData);
				String presentHash = HMACUtils.digestAsPlainText(
						HMACUtils.generateHash(JsonUtil.objectMapperObjectToJson(dataInfoDTO).getBytes()));
				StringBuilder concatenatedHash = new StringBuilder();
				concatenatedHash.append(previousHash);
				concatenatedHash.append(presentHash);
				// String concatenatedHash = previousHash + presentHash;
				String finalHash = HMACUtils
						.digestAsPlainText(HMACUtils.generateHash(concatenatedHash.toString().getBytes()));
				bioInfo.setHash(finalHash);
				bioInfo.setSessionKey(splittedEncryptData.getEncryptedSessionKey());
				bioInfo.setSignature("");
				biometrics.add(bioInfo);
				previousHash = finalHash;
			}

			return biometrics;

		} catch (Exception e) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", PlatformErrorMessages.OSI_VALIDATION_BIO_TYPE_EXCEPTION.getMessage() + "-" + e.getMessage());
			throw new BioTypeException(
					PlatformErrorMessages.OSI_VALIDATION_BIO_TYPE_EXCEPTION.getMessage() + "-" + e.getMessage());

		}
	}

	public static class SplittedEncryptedData {
		private String encryptedSessionKey;
		private String encryptedData;

		public SplittedEncryptedData() {
			super();
		}

		public SplittedEncryptedData(String encryptedSessionKey, String encryptedData) {
			super();
			this.encryptedData = encryptedData;
			this.encryptedSessionKey = encryptedSessionKey;
		}

		public String getEncryptedData() {
			return encryptedData;
		}

		public void setEncryptedData(String encryptedData) {
			this.encryptedData = encryptedData;
		}

		public String getEncryptedSessionKey() {
			return encryptedSessionKey;
		}

		public void setEncryptedSessionKey(String encryptedSessionKey) {
			this.encryptedSessionKey = encryptedSessionKey;
		}
	}

	private SplittedEncryptedData getSessionKey(String timeStamp, byte[] data) {
		SplittedEncryptedData splittedData = null;
		String aad = CryptoUtil.encodeBase64String(timeStamp.substring(timeStamp.length() - 16).getBytes());
		String salt = CryptoUtil.encodeBase64String(timeStamp.substring(timeStamp.length() - 12).getBytes());
		CryptoManagerEncryptDto encryptDto = new CryptoManagerEncryptDto();
		RequestWrapper<CryptoManagerEncryptDto> request = new RequestWrapper<>();
		encryptDto.setAad(aad);
		encryptDto.setApplicationId(IDA_APP_ID);
		encryptDto.setReferenceId(PARTNER_ID);
		encryptDto.setSalt(salt);
		encryptDto.setTimeStamp(timeStamp);
		encryptDto.setData(CryptoUtil.encodeBase64(data));

		request.setId(authRequestId);
		DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
		LocalDateTime localdatetime = LocalDateTime
				.parse(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)), format);
		request.setRequesttime(localdatetime);
		request.setRequest(encryptDto);
		request.setVersion(VERSION);
		try {

			CryptomanagerResponseDto response = (CryptomanagerResponseDto) registrationProcessorRestClientService
					.postApi(ApiName.ENCRYPTURL, "", "", request, CryptomanagerResponseDto.class);
			splittedData = splitEncryptedData((String) response.getResponse().getData());

		} catch (ApisResourceAccessException e) {
			e.printStackTrace();
		}
		return splittedData;

	}

	public SplittedEncryptedData splitEncryptedData(String data) {
		byte[] dataBytes = CryptoUtil.decodeBase64(data);
		byte[][] splits = splitAtFirstOccurance(dataBytes,
				String.valueOf(env.getProperty(KERNEL_KEY_SPLITTER)).getBytes());
		return new SplittedEncryptedData(CryptoUtil.encodeBase64(splits[0]), CryptoUtil.encodeBase64(splits[1]));
	}

	private static byte[][] splitAtFirstOccurance(byte[] strBytes, byte[] sepBytes) {
		int index = findIndex(strBytes, sepBytes);
		if (index >= 0) {
			byte[] bytes1 = new byte[index];
			byte[] bytes2 = new byte[strBytes.length - (bytes1.length + sepBytes.length)];
			System.arraycopy(strBytes, 0, bytes1, 0, bytes1.length);
			System.arraycopy(strBytes, (bytes1.length + sepBytes.length), bytes2, 0, bytes2.length);
			return new byte[][] { bytes1, bytes2 };
		} else {
			return new byte[][] { strBytes, new byte[0] };
		}
	}

	private static int findIndex(byte arr[], byte[] subarr) {
		int len = arr.length;
		int subArrayLen = subarr.length;
		return IntStream.range(0, len).filter(currentIndex -> {
			if ((currentIndex + subArrayLen) <= len) {
				byte[] sArray = new byte[subArrayLen];
				System.arraycopy(arr, currentIndex, sArray, 0, subArrayLen);
				return Arrays.equals(sArray, subarr);
			}
			return false;
		}).findFirst() // first occurence
				.orElse(-1); // No element found
	}

}
