package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.demo.authentication.service.EncryptHelper.CryptoUtility;
import io.mosip.demo.authentication.service.dto.EncryptionRequestDto;
import io.mosip.demo.authentication.service.dto.EncryptionResponseDto;
import io.mosip.kernel.core.util.FileUtils;
import io.swagger.annotations.ApiOperation;

@RestController
public class OldEncrypt {

	/** The obj mapper. */
	@Autowired
	private ObjectMapper objMapper;
	
	/** The Constant ASYMMETRIC_ALGORITHM. */
	private static final String ASYMMETRIC_ALGORITHM ="RSA";	
	
	/** The Constant fileInfoPath. */
	private static final String fileInfoPath ="lib\\Keystore\\PublicKey";
	
	@PostMapping(path = "/identity/oldEncrypt")
	@ApiOperation(value = "Encrypt Identity with sessionKey and Encrypt Session Key with Public Key", response = EncryptionResponseDto.class)
	public EncryptionResponseDto oldEncrypt(@RequestBody EncryptionRequestDto encryptionRequestDto)
			throws NoSuchAlgorithmException, JsonProcessingException, InvalidKeySpecException, IOException {
		EncryptionResponseDto encryptionResponseDto = new EncryptionResponseDto();
		CryptoUtility cryptoUtil=new CryptoUtility();
		SecretKey secKey=cryptoUtil.genSecKey();
		byte encryptedDateArr[]=null;
		try {
			encryptedDateArr = cryptoUtil.symmetricEncrypt(objMapper.writeValueAsString(encryptionRequestDto.getIdentityRequest()).getBytes(), secKey);
		} catch (InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException
				| IllegalBlockSizeException | BadPaddingException e) {
			encryptionResponseDto.setEncryptedIdentity(e.getMessage());
		}
		encryptionResponseDto.setEncryptedIdentity(Base64.encodeBase64URLSafeString(encryptedDateArr));
		byte encryptedSessionKeyArr[]=null;
		try {
			encryptedSessionKeyArr = cryptoUtil.asymmetricEncrypt(secKey.getEncoded(),loadPublicKey());
		} catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			encryptionResponseDto.setEncryptedSessionKey(e.getMessage());
		}
		encryptionResponseDto.setEncryptedSessionKey(Base64.encodeBase64URLSafeString(encryptedSessionKeyArr));
		System.out.println(Base64.encodeBase64URLSafeString(encryptedSessionKeyArr).length());
		return encryptionResponseDto;
	}
	
	/**
	 * Gets the public key.
	 *
	 * @return the public key
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private byte[] getPublicKey() throws IOException {
		byte[] publicKeyByteArr=null;
		File publicKeyFile= FileUtils.getFile(OldEncrypt.fileInfoPath);
			if(publicKeyFile.exists())
			{
				byte[] publicKeyByteEncodedArr=Files.readAllBytes(publicKeyFile.toPath());
				publicKeyByteArr= Base64.decodeBase64(publicKeyByteEncodedArr);
			}
		 return publicKeyByteArr;
		
	}
	
	/**
	 * Load public key.
	 *
	 * @return the public key
	 * @throws InvalidKeySpecException the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private PublicKey loadPublicKey()
			throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		byte[] publicKeyBytes = getPublicKey();
		PublicKey publicKey = KeyFactory.getInstance(OldEncrypt.ASYMMETRIC_ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        return publicKey;
	}
}
