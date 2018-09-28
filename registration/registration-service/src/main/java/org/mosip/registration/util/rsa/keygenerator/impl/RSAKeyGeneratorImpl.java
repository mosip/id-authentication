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
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
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
	@SuppressWarnings("resource")
	public void saveKey(String filePath, BigInteger modulus, BigInteger exponent) {
		FileOutputStream fileoutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			// initialize file in which to save key file
			File directory = new File(filePath.substring(0, filePath.lastIndexOf("/")));
			if (!directory.exists()) {
				directory.mkdir();
			}
			File file = new File(filePath);
			fileoutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException fileNotFoundException) {
			throw new RegBaseUncheckedException(REG_FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_FILE_NOT_FOUND_ERROR_CODE.getErrorMessage(), fileNotFoundException);
		}

		try {
			objectOutputStream = new ObjectOutputStream(fileoutputStream);

			// write key modulus value
			objectOutputStream.writeObject(modulus);
			// write key exponent value
			objectOutputStream.writeObject(exponent);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(),
					REG_IO_ERROR_CODE.getErrorMessage(), ioException);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mindtree.mosip.utility.rsa.keygenerator.RSAKeyGenerator#readPublickey(
	 * java.lang.String)
	 */
	@SuppressWarnings("resource")
	public PublicKey readPublickey(String publicKeyFile) {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			// initialize file in which to get publickey
			fileInputStream = new FileInputStream(new File(publicKeyFile).getAbsolutePath());
		} catch (FileNotFoundException fileNotFoundException) {
			throw new RegBaseUncheckedException(REG_FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_FILE_NOT_FOUND_ERROR_CODE.getErrorMessage(), fileNotFoundException);
		}
		try {
			objectInputStream = new ObjectInputStream(fileInputStream);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(),
					REG_IO_ERROR_CODE.getErrorMessage(), ioException);
		} 
		
		BigInteger mod = null;
		try {
			// get public key modulus value
			mod = (BigInteger) objectInputStream.readObject();
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseUncheckedException(REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage(), classNotFoundException);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(),
					REG_IO_ERROR_CODE.getErrorMessage(), ioException);
		}
		BigInteger exp = null;
		try {
			// get public key exponent value
			exp = (BigInteger) objectInputStream.readObject();
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseUncheckedException(REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage(), classNotFoundException);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(),
					REG_IO_ERROR_CODE.getErrorMessage(), ioException);
		}

		// Re-Generate RSAPublicKeySpec with obtained modulus and exponent value
		RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(mod, exp);
		KeyFactory keyFactory = null;
		try {
			// initialize key factory with specified algorithm
			keyFactory = KeyFactory.getInstance(RegConstants.RSA_ALG);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		}
		PublicKey publicKey = null;
		try {
			// get public key
			publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
		} catch (InvalidKeySpecException invalidKeySpecException) {
			throw new RegBaseUncheckedException(REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorCode(),
					REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorMessage(), invalidKeySpecException);
		}

		return publicKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mindtree.mosip.utility.rsa.keygenerator.RSAKeyGenerator#readPrivatekey(
	 * java.lang.String)
	 */
	@SuppressWarnings("resource")
	public PrivateKey readPrivatekey(String privateKeyFile) {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			// initialize file in which to get privatekey

			fileInputStream = new FileInputStream(new File(privateKeyFile).getAbsolutePath());
		} catch (FileNotFoundException fileNotFoundException) {
			throw new RegBaseUncheckedException(REG_FILE_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_FILE_NOT_FOUND_ERROR_CODE.getErrorMessage(), fileNotFoundException);
		}
		try {
			objectInputStream = new ObjectInputStream(fileInputStream);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(),
					REG_IO_ERROR_CODE.getErrorMessage(), ioException);
		}
		
		BigInteger mod = null;
		try {
			// get private key modulus value

			mod = (BigInteger) objectInputStream.readObject();
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseUncheckedException(REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage(), classNotFoundException);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(),
					REG_IO_ERROR_CODE.getErrorMessage(), ioException);
		}
		BigInteger exp = null;
		try {
			// get private key exponent value

			exp = (BigInteger) objectInputStream.readObject();
		} catch (ClassNotFoundException classNotFoundException) {
			throw new RegBaseUncheckedException(REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorCode(),
					REG_CLASS_NOT_FOUND_ERROR_CODE.getErrorMessage(), classNotFoundException);
		} catch (IOException ioException) {
			throw new RegBaseUncheckedException(REG_IO_ERROR_CODE.getErrorCode(),
					REG_IO_ERROR_CODE.getErrorMessage(), ioException);
		}

		// Re-Generate RSAPrivateKeySpec with obtained modulus and exponent values
		RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(mod, exp);
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance(RegConstants.RSA_ALG);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		}
		PrivateKey privateKey = null;
		try {
			privateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);
		} catch (InvalidKeySpecException invalidKeySpecException) {
			throw new RegBaseUncheckedException(REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorCode(),
					REG_INVALID_KEY_SPEC_ERROR_CODE.getErrorMessage(), invalidKeySpecException);
		}

		return privateKey;
	}
}
