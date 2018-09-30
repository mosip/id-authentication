package org.mosip.registration.util.rsa.keygenerator.impl;

import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_CLASS_NOT_FOUND_ERROR_CODE;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_FILE_NOT_FOUND_ERROR_CODE;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_INVALID_KEY_SPEC_ERROR_CODE;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_IO_ERROR_CODE;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;
import org.springframework.stereotype.Component;

/**
 * RSA key generation
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class RSAKeyGeneratorImpl implements RSAKeyGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mindtree.mosip.utility.rsa.keygenerator.RSAKeyGenerator#generateKey()
	 */
	public void generateKey() {
		KeyPairGenerator keyPairGenerator = null;
		try {
			// Generate key pair generator
			keyPairGenerator = KeyPairGenerator.getInstance(RegConstants.RSA_ALG);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		}
		// initialize key pair generator
		keyPairGenerator.initialize(2048);
		// get key pair
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		// get public key from key pair
		PublicKey publicKey = keyPair.getPublic();
		// getting private key from key pair
		PrivateKey privateKey = keyPair.getPrivate();

		KeyFactory keyFactory = null;
		try {
			// inialize keyfactory to specified algorithm
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		}
		RSAPublicKeySpec rsaPublicKeySpec = null;
		try {
			// get rsaPublicKeySpec from key factory
			rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		} catch (InvalidKeySpecException invalidKeySpecException) {
			throw new RegBaseUncheckedException(REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorCode(),
					REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorMessage(), invalidKeySpecException);
		}
		RSAPrivateKeySpec rsaPrivateKeySpec = null;
		try {
			// get rsa private key spec from key factory
			rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
		} catch (InvalidKeySpecException invalidKeySpecException) {
			throw new RegBaseUncheckedException(REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorCode(),
					REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorMessage(), invalidKeySpecException);
		}

		// Save public key in public.key file
		saveKey(RegConstants.RSA_PUBLIC_KEY_FILE, rsaPublicKeySpec.getModulus(), rsaPublicKeySpec.getPublicExponent());

		// save private key in private.key file
		saveKey(RegConstants.RSA_PRIVATE_KEY_FILE, rsaPrivateKeySpec.getModulus(),
				rsaPrivateKeySpec.getPrivateExponent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mindtree.mosip.utility.rsa.keygenerator.RSAKeyGenerator#saveKeyFile(java.
	 * lang.String, java.math.BigInteger, java.math.BigInteger)
	 */
	public void saveKey(String filePath, BigInteger modulus, BigInteger exponent) {
		File directory = new File(filePath.substring(0, filePath.lastIndexOf('/')));
		if (!directory.exists()) {
			directory.mkdir();
		}

		try (FileOutputStream fileoutputStream = new FileOutputStream(new File(filePath));
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileoutputStream);) {

			objectOutputStream.writeObject(modulus);
			// write key exponent value
			objectOutputStream.writeObject(exponent);
		} catch (FileNotFoundException fileNotFoundException) {
			throw new RegBaseUncheckedException(REG_FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_FILE_NOT_FOUND_ERROR_CODE.getErrorMessage(), fileNotFoundException);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(), REG_IO_ERROR_CODE.getErrorMessage(),
					ioException);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mindtree.mosip.utility.rsa.keygenerator.RSAKeyGenerator#readPublickey(
	 * java.lang.String)
	 */
	public PublicKey readPublickey(String publicKeyFile) {
		return (PublicKey) getKey(publicKeyFile, true);
	}

	private Key getKey(String keyFile, boolean isPublic) {
		BigInteger mod = null;
		BigInteger exp = null;

		KeyFactory keyFactory = null;
		Key key = null;

		try (FileInputStream fileInputStream = new FileInputStream(new File(keyFile).getAbsolutePath());
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {
			mod = (BigInteger) objectInputStream.readObject();
			exp = (BigInteger) objectInputStream.readObject();

			// Re-Generate RSAPublicKeySpec with obtained modulus and exponent value
			KeySpec keySpec = isPublic ? new RSAPublicKeySpec(mod, exp) : new RSAPrivateKeySpec(mod, exp);

			// initialize key factory with specified algorithm
			keyFactory = KeyFactory.getInstance(RegConstants.RSA_ALG);

			// get public key or private key
			key = isPublic ? keyFactory.generatePublic(keySpec) : keyFactory.generatePrivate(keySpec);

		} catch (FileNotFoundException fileNotFoundException) {
			throw new RegBaseUncheckedException(REG_FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_FILE_NOT_FOUND_ERROR_CODE.getErrorMessage(), fileNotFoundException);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(), REG_IO_ERROR_CODE.getErrorMessage(),
					ioException);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseUncheckedException(REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage(), classNotFoundException);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		} catch (InvalidKeySpecException invalidKeySpecException) {
			throw new RegBaseUncheckedException(REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorCode(),
					REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorMessage(), invalidKeySpecException);
		}
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mindtree.mosip.utility.rsa.keygenerator.RSAKeyGenerator#readPrivatekey(
	 * java.lang.String)
	 */
	public PrivateKey readPrivatekey(String privateKeyFile) {
		return (PrivateKey)getKey(privateKeyFile, false);
	}
}
