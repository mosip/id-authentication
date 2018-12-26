package io.mosip.authentication.service.integration;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
//import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.dto.CryptomanagerRequestDto;
import io.mosip.authentication.service.integration.dto.CryptomanagerResponseDto;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;

/**
 * The Class KeyManager.
 * 
 * @author Sanjay Murali
 */
@Component
public class KeyManager {

	//TODO to be replaced with key manager
	private static final byte[] TEMP_PRIVATE_KEY_BYTES = new byte[] {48,-126,4,-66,2,1,0,48,13,6,9,42,-122,72,-122,-9,13,1,1,1,5,0,4,-126,4,-88,48,-126,4,-92,2,1,0,2,-126,1,1,0,-76,46,-87,-86,-123,64,-10,-99,-82,1,-91,11,92,111,-62,-41,-114,79,15,-72,37,-128,12,-52,101,-113,-46,-80,65,-100,94,85,118,-41,126,66,-114,-54,48,62,-40,113,35,16,-45,112,-16,118,-21,-99,-99,41,47,-98,-4,-119,114,70,-111,-33,-77,108,-80,121,-67,7,107,8,-94,13,40,122,41,-93,-67,123,122,7,120,-88,-92,91,92,-33,-26,118,10,-45,-111,-88,108,6,-61,-99,-63,-1,83,-43,4,-104,82,118,-1,-56,82,123,86,-120,23,2,89,-4,98,22,118,-41,117,-128,-1,-99,-40,124,22,-75,-18,79,109,126,65,-57,-59,-15,16,-83,46,-73,-35,44,97,52,35,91,-16,115,116,8,116,-26,51,-81,-62,120,92,83,12,31,83,-9,-2,84,-22,48,124,-97,8,-126,101,30,118,51,53,62,-63,-72,80,-60,114,-9,98,54,-125,-127,39,-54,66,18,36,7,117,-43,-81,59,-79,77,106,-94,11,-118,-98,53,28,-28,-45,37,93,98,96,-47,98,-64,25,65,63,101,88,49,64,-79,12,51,-69,83,119,111,114,93,-127,-19,-116,-13,24,-119,51,67,11,62,66,-120,7,93,50,-113,-22,82,-25,50,-56,-43,-61,-33,-57,-119,-38,-122,41,111,2,3,1,0,1,2,-126,1,0,43,-95,95,1,69,26,41,125,-98,28,-108,-40,14,-18,-101,42,50,50,15,-42,-31,-15,-61,103,-99,50,57,-20,-25,62,83,-109,115,95,20,26,78,-44,67,-31,123,-20,-51,118,110,20,-37,-115,-104,89,70,-84,-87,-12,-32,38,-14,46,-121,117,57,79,-40,-35,-23,-71,-119,-96,2,9,-104,-93,24,65,39,-119,102,79,-73,-42,114,82,19,71,-66,89,24,31,10,22,-45,-19,-63,97,-118,-113,-26,-65,94,-56,-7,-77,10,21,-6,111,22,81,-122,-20,-47,88,109,114,14,29,-9,60,-96,-23,26,122,85,-49,-127,32,-25,-100,-79,124,-116,-56,-107,25,14,-23,97,121,-39,-68,96,108,89,-15,-92,-118,100,64,38,47,-21,69,-56,-105,-103,62,-121,72,44,-15,96,-120,86,18,-123,111,31,-66,110,-73,79,-106,-124,61,-8,97,45,10,-67,-72,72,6,92,-74,-97,86,21,115,-107,76,-118,-61,-63,23,25,88,-113,47,123,52,101,24,66,-32,123,-77,126,-52,24,53,-19,56,-59,-37,93,79,-15,-124,113,-57,121,-110,-69,96,52,-5,91,-65,-109,86,-125,-91,46,69,8,-89,99,59,72,24,-112,32,-49,7,-118,-128,-72,-33,112,-42,115,106,-40,-50,16,-39,2,-127,-127,0,-8,34,32,-107,-39,61,-74,-127,-110,-64,56,-82,45,-5,52,-89,-79,41,41,-108,-12,52,-126,-123,-111,-70,-125,-65,114,16,-117,60,7,-101,-40,-17,-105,-50,41,94,77,102,-65,42,-7,-19,-1,-62,59,92,3,-33,-11,46,-66,-12,-71,-36,23,-98,33,-34,-99,106,125,-95,-111,-109,119,56,89,-39,90,98,125,-44,-112,-22,-31,15,-75,-76,-6,-128,115,-113,-8,112,-125,96,-80,53,-31,-65,47,113,22,72,38,46,41,-29,113,34,-114,13,0,61,54,-123,-119,-13,-19,-36,-102,-125,39,125,-90,-82,66,-20,69,-82,-79,-7,114,89,2,-127,-127,0,-71,-27,9,-28,-22,-27,-101,-25,119,49,35,52,74,51,-70,-121,-101,6,-42,8,56,-86,45,92,-113,107,71,57,-94,81,118,113,-109,-83,122,-10,-91,24,-41,-40,-51,13,-13,85,106,27,61,15,66,-57,-124,50,-44,33,4,-45,-96,-76,30,-62,39,7,82,53,-85,-17,61,17,-11,92,52,-78,89,104,-50,53,-78,71,12,66,-41,97,98,-126,-76,92,30,75,71,-69,-112,-93,103,14,-49,53,-100,-43,-94,-116,-42,88,15,-115,34,-4,75,14,70,-46,-99,57,-92,-13,-105,-14,62,-26,-104,70,110,-65,-101,-99,8,57,49,7,2,-127,-127,0,-52,-71,-101,-103,7,60,91,-80,92,-100,44,39,-55,-40,81,-127,106,50,68,20,-103,-56,25,72,-117,12,16,87,-116,-115,9,-45,-27,-109,56,81,-74,54,106,-5,91,113,66,-104,-6,-52,-37,16,46,89,-92,-8,-53,26,94,-125,28,53,-13,102,-115,27,87,85,-35,-127,-56,-46,-102,-78,-21,-82,-31,92,17,-27,-88,11,-94,-43,-117,94,94,-80,76,-88,-92,-102,112,74,-2,23,-10,-63,-1,42,-106,-17,-64,80,-90,78,-7,-79,64,-14,-119,23,-8,60,5,-82,52,-70,-90,-31,63,91,-12,-126,34,-59,62,88,-4,-114,-117,65,2,-127,-128,99,-66,-74,-63,99,28,-46,-69,-81,35,-23,-116,-36,39,-57,117,-115,-60,108,65,-69,13,-19,11,22,85,108,-63,75,12,68,-18,-118,91,-14,119,-73,124,-114,12,-31,114,19,-40,7,-8,23,-102,65,-83,-58,-116,115,112,72,35,-71,-66,12,39,7,68,17,99,-123,30,-8,120,-45,91,-89,86,-17,76,-94,33,3,-56,-59,-106,115,-12,-24,35,-45,-36,-3,-48,76,-20,34,-98,-38,-28,-126,96,117,7,-40,-111,-109,77,107,2,-62,-5,-10,-43,-15,64,127,102,119,-48,-48,-2,5,-84,59,-106,-57,67,-2,-98,-110,-44,5,47,2,-127,-127,0,-88,76,20,-119,-70,42,30,-2,-100,-25,113,-46,-115,96,-104,28,-35,96,-70,9,-50,53,27,31,73,42,80,-32,-106,-50,26,28,-128,-32,-61,-41,-97,64,-34,-29,17,18,55,-22,-46,127,-44,-94,90,-90,119,84,126,-27,-5,-49,-99,-114,81,-32,111,-117,93,-55,-36,31,44,89,62,73,-22,39,-19,83,71,-56,118,-22,-123,0,-64,15,39,-92,57,127,-19,-125,62,118,121,-92,-77,-65,39,-107,9,99,-115,-58,-65,-38,-16,-20,-55,-78,-49,11,-9,53,17,-93,-40,-49,-20,-90,106,87,-122,-21,-7,31,-16,57,-82,-35,8,-9};

	/** The Constant AES. */
	private static final String AES = "AES";
	
	/** The Constant SESSION_KEY. */
	private static final String SESSION_KEY = "sessionKey";
	
	/** The Constant KEY. */
	private static final String KEY = "key";
	
	/** The Constant RSA. */
	private static final String RSA = "RSA";
	
	/** The Constant REQUEST. */
	private static final String REQUEST = "request";
	
	/** The Constant TSP_ID. */
	private static final String TSP_ID = "tspID";
	
	/** The Constant FILEPATH. */
	private static final String FILEPATH = "sample.privatekey.filepath";
	
	/** KeySplitter */
	
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;
	
	/** The Constant FILEPATH. */
	private static final String referenceID = "REF01";
	
	@Value("${application.id}")
	private String appId;
	
	
	@Autowired
	private RestHelper restHelper;

	@Autowired
	private RestRequestFactory restRequestFactory;
	
	private static Logger logger = IdaLogger.getLogger(KeyManager.class);

	/**
	 * Request data.
	 *
	 * @param requestBody the request body
	 * @param env the env
	 * @param decryptor the decryptor
	 * @param mapper the mapper
	 * @return the map
	 * @throws IdAuthenticationAppException 
	 * @throws IdAuthenticationBusinessException 
	 */
	public Map<String, Object> requestData(Map<String, Object> requestBody, Environment env, DecryptorImpl decryptor, ObjectMapper mapper) throws IdAuthenticationAppException {
		Map<String, Object> request = null;
		try {
			/*String tspId = (String) requestBody.get(TSP_ID);
			byte[] privateKey = fileReader(tspId, env);
			byte[] privateKey = getPrivateKey();*/
	    /*KeyFactory kf = KeyFactory.getInstance(RSA);
			PrivateKey priKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKey));*/
			byte[] reqBody = (byte[]) requestBody.get(REQUEST);
			Optional<String> encryptedSessionKey = Optional.ofNullable(requestBody.get(KEY))
					.filter(obj -> obj instanceof Map)
					.map(obj -> String.valueOf(((Map<String, Object>)obj).get(SESSION_KEY)));
			if(encryptedSessionKey.isPresent()) {
				byte[] encyptedSessionkey = Base64.decodeBase64(encryptedSessionKey.get());
				
				/*byte[] decryptedKey = decryptor.asymmetricPrivateDecrypt(priKey, encyptedSessionkey);
				byte[] finalDecryptedData = decryptor
						.symmetricDecrypt(new SecretKeySpec(decryptedKey, 0, decryptedKey.length, AES), reqBody);*/
				
				
				RestRequestDTO restRequestDTO = null;
				CryptomanagerRequestDto cryptoManagerRequestDto = new CryptomanagerRequestDto();
				CryptomanagerResponseDto cryptomanagerResponseDto=null;
				String reqData = null;
				
	          try {
					cryptoManagerRequestDto.setApplicationId(appId);
					cryptoManagerRequestDto.setReferenceId(referenceID);
					cryptoManagerRequestDto.setTimeStamp(LocalDateTime.now());
					cryptoManagerRequestDto.setData(CryptoUtil
							.encodeBase64(CryptoUtil.combineByteArray(reqBody, encyptedSessionkey, keySplitter)));
					restRequestDTO = restRequestFactory.buildRequest(RestServicesConstants.DECRYPTION_SERVICE,
							cryptoManagerRequestDto, CryptomanagerResponseDto.class);
					cryptomanagerResponseDto = restHelper.requestSync(restRequestDTO);
					reqData = new String(Base64.decodeBase64(cryptomanagerResponseDto.getData()));
					logger.info("NA", "NA", "NA", "cryptomanagerResponseDto " + reqData);
				} catch (RestServiceException e) {
					e.printStackTrace();
					logger.error("NA", "NA", e.getErrorCode(), e.getErrorText());
					throw new IdAuthenticationAppException(IdAuthenticationErrorConstants.SERVER_ERROR);
				} catch (IDDataValidationException e) {
					throw new IdAuthenticationAppException(
							IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST, e);
				}
				
				request = mapper.readValue(
						reqData,
						new TypeReference<Map<String, Object>>() {
						});				
			}
		} catch (IOException  e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());		
		}
		return request;
	}

	/*private byte[] getPrivateKey() {
		return TEMP_PRIVATE_KEY_BYTES;
	}*/

//	/**
//	 * File reader.
//	 *
//	 * @param filename the filename
//	 * @param env the env
//	 * @return the byte[]
//	 * @throws IOException Signals that an I/O exception has occurred.
//	 */
//	public byte[] fileReader(String filename, Environment env) throws IOException {
//		String localpath = env.getProperty(FILEPATH);
//		Object[] homedirectory = new Object[] { System.getProperty("user.home") + File.separator };
//		String finalpath = MessageFormat.format(localpath, homedirectory);
//		File fileInfo = new File(finalpath + File.separator + filename);
//		byte[] output = null;
//		if (fileInfo.exists()) {
//			output = Files.readAllBytes(fileInfo.toPath());
//		}else {
//			throw new IOException();
//		}
//		return output;
//	}

}
