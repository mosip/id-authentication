/**
 * 
 */
package io.mosip.kernel.keymanager.softhsm.demo;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import io.mosip.kernel.keymanager.softhsm.SofthsmKeystore;
import io.mosip.kernel.keymanager.softhsm.impl.SofthsmKeystoreImpl;

public class KeystoreDemo {

	public static void main(String[] args) throws CertificateEncodingException, IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		SofthsmKeystore mosipSoftHSM = new SofthsmKeystoreImpl();

		List<String> allAlias = mosipSoftHSM.getAllAlias();

		allAlias.forEach(alias -> {
			Key key = mosipSoftHSM.getKeyByAlias(alias);
			System.out.println(alias + "," + key);
		});

		mosipSoftHSM.createSymmetricKey("test-alias-secret");

		SecretKey secretKey = mosipSoftHSM.getSymmetricKey("test-alias-secret");
		System.out.println(secretKey.toString());

		mosipSoftHSM.createAsymmetricKey("test-alias-private");

		PrivateKey privateKey = mosipSoftHSM.getPrivateKey("test-alias-private");

		System.out.println(privateKey.toString());
		System.out.println(privateKey.getEncoded());

		PublicKey publicKey = mosipSoftHSM.getPublicKey("test-alias-private");
		System.out.println(publicKey.toString());
		System.out.println(publicKey.getEncoded());

		Certificate certificate = mosipSoftHSM.getCertificate("test-alias-private");
		System.out.println(certificate.toString());
		System.out.println(certificate.getEncoded());


		System.out.println("OK");
	}
}
