package io.mosip.kernel.crypto.jce.core;

import java.security.InvalidAlgorithmParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PSource.PSpecified;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.InvalidParamSpecException;
import io.mosip.kernel.core.crypto.exception.SignatureException;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.util.CryptoUtils;

/**
 * This class provided <b> Basic and Core Cryptographic functionalities </b>.
 * 
 * This class follows {@link CryptoCoreSpec} and implement all basic
 * Cryptographic functions.
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 * 
 * @see CryptoCoreSpec
 * @see PrivateKey
 * @see PublicKey
 * @see Signature
 * @see SecretKey
 * @see Cipher
 * @see GCMParameterSpec
 * @see SecureRandom
 */
//Code optimization remaining (Code Dupe)
@Component
public class CryptoCore implements CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> {

	private static final String MGF1 = "MGF1";

	private static final String HASH_ALGO = "SHA-256";

	private static final String AES = "AES";

	@Value("${mosip.kernel.crypto.gcm-tag-length}")
	private int tagLength;

	@Value("${mosip.kernel.crypto.symmetric-algorithm-name}")
	private String symmetricAlgorithm;

	@Value("${mosip.kernel.crypto.asymmetric-algorithm-name}")
	private String asymmetricAlgorithm;

	@Value("${mosip.kernel.crypto.hash-algorithm-name}")
	private String passwordAlgorithm;

	@Value("${mosip.kernel.crypto.sign-algorithm-name}")
	private String signAlgorithm;

	@Value("${mosip.kernel.crypto.hash-symmetric-key-length}")
	private int symmetricKeyLength;

	@Value("${mosip.kernel.crypto.hash-iteration}")
	private int iterations;

	private Map<String, Cipher> cipherRegistry;

	private SecureRandom secureRandom;

	private SecretKeyFactory secretKeyFactory;

	private Signature signature;

	@PostConstruct
	public void init() {
		cipherRegistry = new HashMap<>();
		try {
			cipherRegistry.put(symmetricAlgorithm, Cipher.getInstance(symmetricAlgorithm));
			cipherRegistry.put(asymmetricAlgorithm, Cipher.getInstance(asymmetricAlgorithm));
			secretKeyFactory = SecretKeyFactory.getInstance(passwordAlgorithm);
			signature = Signature.getInstance(signAlgorithm);
		} catch (java.security.NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage(), e);
		}
		secureRandom = new SecureRandom();
	}

	@Override
	public byte[] symmetricEncrypt(SecretKey key, byte[] data, byte[] aad) {
		Objects.requireNonNull(key, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		Cipher cipher = cipherRegistry.get(symmetricAlgorithm);
		byte[] output = null;
		byte[] randomIV = generateIV(cipher.getBlockSize());
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), AES);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(tagLength, randomIV);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
			output = new byte[cipher.getOutputSize(data.length) + cipher.getBlockSize()];
			if (aad != null && aad.length != 0) {
				cipher.updateAAD(aad);
			}
			byte[] processData = doFinal(data, cipher);
			System.arraycopy(processData, 0, output, 0, processData.length);
			System.arraycopy(randomIV, 0, output, processData.length, randomIV.length);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorMessage(), e);
		}
		return output;
	}

	@Override
	public byte[] symmetricEncrypt(SecretKey key, byte[] data, byte[] iv, byte[] aad) {
		Objects.requireNonNull(key, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		if (iv == null) {
			symmetricEncrypt(key, data, aad);
		}
		Cipher cipher = cipherRegistry.get(symmetricAlgorithm);
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), AES);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(tagLength, iv);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
			if (aad != null && aad.length != 0) {
				cipher.updateAAD(aad);
			}
			return doFinal(data, cipher);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidParamSpecException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorMessage(), e);
		}
	}

	@Override
	public byte[] symmetricDecrypt(SecretKey key, byte[] data, byte[] aad) {
		Objects.requireNonNull(key, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		Cipher cipher = cipherRegistry.get(symmetricAlgorithm);
		byte[] output = null;
		try {
			byte[] randomIV = Arrays.copyOfRange(data, data.length - cipher.getBlockSize(), data.length);
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), AES);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(tagLength, randomIV);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
			if (aad != null && aad.length != 0) {
				cipher.updateAAD(aad);
			}
			output = doFinal(Arrays.copyOf(data, data.length - cipher.getBlockSize()), cipher);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorMessage(), e);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_DATA_LENGTH_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_DATA_LENGTH_EXCEPTION.getErrorMessage(), e);
		}
		return output;
	}

	@Override
	public byte[] symmetricDecrypt(SecretKey key, byte[] data, byte[] iv, byte[] aad) {
		Objects.requireNonNull(key, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		if (iv == null) {
			symmetricDecrypt(key, data, aad);
		}
		Cipher cipher = cipherRegistry.get(symmetricAlgorithm);
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), AES);
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(tagLength, iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
			if (aad != null) {
				cipher.updateAAD(aad);
			}
			return doFinal(data, cipher);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidParamSpecException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorMessage(), e);
		}
	}

	@Override
	public byte[] asymmetricEncrypt(PublicKey key, byte[] data) {
		Objects.requireNonNull(key, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		Cipher cipher = cipherRegistry.get(asymmetricAlgorithm);
		final OAEPParameterSpec oaepParams = new OAEPParameterSpec(HASH_ALGO, MGF1, new MGF1ParameterSpec(HASH_ALGO),
				PSpecified.DEFAULT);
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, oaepParams);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidParamSpecException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorMessage(), e);
		}
		return doFinal(data, cipher);
	}

	@Override
	public byte[] asymmetricDecrypt(PrivateKey key, byte[] data) {
		Objects.requireNonNull(key, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
        Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("RSA/ECB/NoPadding");
		} catch (java.security.NoSuchAlgorithmException | NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        final OAEPParameterSpec oaepParams = new OAEPParameterSpec(HASH_ALGO, MGF1, new MGF1ParameterSpec(HASH_ALGO),
				PSpecified.DEFAULT);
        try {
			cipher.init(Cipher.DECRYPT_MODE, key);
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		} /*catch (InvalidAlgorithmParameterException e) {
			throw new InvalidParamSpecException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorMessage(), e);
		}*/
        byte[] paddedPlainText= doFinal(data, cipher);
        int RSA_KEY_SIZE=2048;
        if (paddedPlainText.length < RSA_KEY_SIZE / 8) {
        	   byte[] tmp = new byte[RSA_KEY_SIZE / 8];
        	   System.arraycopy(paddedPlainText, 0, tmp, tmp.length - paddedPlainText.length, paddedPlainText.length);
        	   System.out.println("Zero padding to " + (RSA_KEY_SIZE / 8));
        	   paddedPlainText = tmp;
        	}
       
        	OAEPParameterSpec paramSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSpecified.DEFAULT);
        	sun.security.rsa.RSAPadding padding = null;
			try {
				padding = sun.security.rsa.RSAPadding.getInstance(sun.security.rsa.RSAPadding.PAD_OAEP_MGF1, RSA_KEY_SIZE / 8, new SecureRandom(), paramSpec);
			} catch (java.security.InvalidKeyException | InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	try {
				return padding.unpad(paddedPlainText);
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return paddedPlainText;
	
	}

	@Override
	public byte[] hash(byte[] data, byte[] salt) {
		CryptoUtils.verifyData(data);
		CryptoUtils.verifyData(salt, SecurityExceptionCodeConstant.SALT_PROVIDED_IS_NULL_OR_EMPTY.getErrorCode(),
				SecurityExceptionCodeConstant.SALT_PROVIDED_IS_NULL_OR_EMPTY.getErrorMessage());
		char[] convertedData = new char[data.length];
		System.arraycopy(data, 0, convertedData, 0, data.length);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(convertedData, salt, iterations, symmetricKeyLength);
		SecretKey key;
		try {
			key = secretKeyFactory.generateSecret(pbeKeySpec);
		} catch (InvalidKeySpecException e) {
			throw new InvalidParamSpecException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_PARAM_SPEC_EXCEPTION.getErrorCode(), e.getMessage(), e);
		}
		return key.getEncoded();
	}

	@Override
	public String sign(byte[] data, PrivateKey privateKey) {
		Objects.requireNonNull(privateKey, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		try {
			signature.initSign(privateKey);
			signature.update(data);
			return CryptoUtil.encodeBase64String(signature.sign());
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		} catch (java.security.SignatureException e) {
			throw new SignatureException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
	}

	@Override
	public boolean verifySignature(byte[] data, String sign, PublicKey publicKey) {
		if (EmptyCheckUtils.isNullEmpty(sign)) {
			throw new SignatureException(SecurityExceptionCodeConstant.MOSIP_SIGNATURE_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_SIGNATURE_EXCEPTION.getErrorMessage());
		}
		Objects.requireNonNull(publicKey, SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		CryptoUtils.verifyData(data);
		try {
			signature.initVerify(publicKey);
			signature.update(data);
			return signature.verify(CryptoUtil.decodeBase64(sign));
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		} catch (java.security.SignatureException e) {
			throw new SignatureException(SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public SecureRandom random() {
		return secureRandom;
	}

	/**
	 * Generator for IV(Initialisation Vector)
	 * 
	 * @param blockSize blocksize of current cipher
	 * @return generated IV
	 */
	private byte[] generateIV(int blockSize) {
		byte[] byteIV = new byte[blockSize];
		secureRandom.nextBytes(byteIV);
		return byteIV;
	}

	private byte[] doFinal(byte[] data, Cipher cipher) {
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException e) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_DATA_SIZE_EXCEPTION.getErrorCode(), e.getMessage(), e);
		} catch (BadPaddingException e) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION.getErrorCode(),
					e.getMessage(), e);
		}
	}
}