/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.bouncycastle.constant;

/**
 * Mosip Security methods
 * {@link #HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING} ,
 * {@link #HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING} ,
 * {@link #HYBRID_RSA_AES_WITH_PKCS1PADDING} ,
 * {@link #AES_WITH_CBC_AND_PKCS7PADDING} ,
 * {@link #DES_WITH_CBC_AND_PKCS7PADDING},{@link #TWOFISH_WITH_CBC_AND_PKCS7PADDING}
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum SecurityMethod {

	/**
	 * Hybrid RSA cryptosystem and {@link #AES_WITH_CBC_AND_PKCS7PADDING} security
	 * method with PKCS1 Padding
	 */
	HYBRID_RSA_AES_WITH_PKCS1PADDING,
	/**
	 * Hybrid RSA cryptosystem and {@link #AES_WITH_CBC_AND_PKCS7PADDING} security
	 * method with OEAP(with MD5) Padding
	 */
	HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING,
	/**
	 * Hybrid RSA cryptosystem and {@link #AES_WITH_CBC_AND_PKCS7PADDING} security
	 * method with OEAP(with SHA3 512) Padding
	 */
	HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING,
	/**
	 * <b>Advanced Encryption Standard</b> symmetric-key block cipher security
	 * method with PKCS7 Padding
	 */
	AES_WITH_CBC_AND_PKCS7PADDING,
	/**
	 * <b>Data Encryption Standard</b> symmetric-key block cipher security method
	 * with PKCS7 Padding
	 */
	DES_WITH_CBC_AND_PKCS7PADDING,
	/**
	 * <b>TWOFISH</b> symmetric-key block cipher security method with PKCS7 Padding
	 */
	TWOFISH_WITH_CBC_AND_PKCS7PADDING,
	/**
	 * RSA cryptosystem security method with PKCS1 Padding
	 */
	RSA_WITH_PKCS1PADDING,
	/**
	 * RSA cryptosystem security method with OEAP(with MD5) Padding
	 */
	RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING,
	/**
	 * RSA cryptosystem security method with OEAP(with SHA3 512) Padding
	 */
	RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING;

}
