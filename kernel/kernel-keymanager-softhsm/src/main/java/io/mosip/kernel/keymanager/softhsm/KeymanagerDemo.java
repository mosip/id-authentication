package io.mosip.kernel.keymanager.softhsm;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.kernel.core.keymanager.spi.KeyStore;

@SpringBootApplication
public class KeymanagerDemo {

	@Autowired
	private KeyStore keyStoreImpl;

	public static void main(String[] args) {
		SpringApplication.run(KeymanagerDemo.class, args);
	}

	@PostConstruct
	public void demo() {

		List<String> allAlias = keyStoreImpl.getAllAlias();

		allAlias.forEach(alias -> {
			Key key = keyStoreImpl.getKey(alias);
			System.out.println(alias + "," + key);
			keyStoreImpl.deleteKey(alias);
		});

		secretkeyDemo();

		keypairDemo();
	}

	/**
	 * 
	 */
	private void secretkeyDemo() {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		SecureRandom secureRandom = new SecureRandom();
		int keyBitSize = 256;
		keyGenerator.init(keyBitSize, secureRandom);
		SecretKey secretKey = keyGenerator.generateKey();

		keyStoreImpl.storeSymmetricKey(secretKey, "test-alias-secret");

		SecretKey fetchedSecretKey = keyStoreImpl.getSymmetricKey("test-alias-secret");
		System.out.println(fetchedSecretKey.toString());
	}

	/**
	 * 
	 */
	private void keypairDemo() {
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		keyStoreImpl.storeAsymmetricKey(keyPair, "test-alias-private", 365);

		PrivateKey privateKey = keyStoreImpl.getPrivateKey("test-alias-private");

		System.out.println(privateKey.toString());
		System.out.println(privateKey.getEncoded());

		PublicKey publicKey = keyStoreImpl.getPublicKey("test-alias-private");
		System.out.println(publicKey.toString());
		System.out.println(publicKey.getEncoded());
		
		X509Certificate certificate = (X509Certificate) keyStoreImpl.getCertificate("test-alias-private");
		try {
			System.out.println(certificate.toString());
			System.out.println("!!!!!!!!!!!!!!"+certificate.getSubjectX500Principal());
			certificate.checkValidity();
//			certificate.checkValidity(new Date(1999, 12, 12));
			System.out.println("@@@@@@@@@@2"+certificate.getIssuerDN());
			System.out.println("##############"+certificate.getIssuerX500Principal());
			System.out.println("$$$$$$$$$$$$$$$$"+certificate.getSubjectDN());
		} catch (CertificateExpiredException | CertificateNotYetValidException e) {
			e.printStackTrace();
		}

		System.out.println("OK");
	}
}
