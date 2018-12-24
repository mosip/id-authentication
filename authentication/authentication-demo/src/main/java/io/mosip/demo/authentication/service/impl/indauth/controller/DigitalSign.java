package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.HMACUtils;

@RestController
public class DigitalSign {

	ObjectMapper mapper = new ObjectMapper();

	@PostMapping(path = "/sign")
	public String sign(@RequestBody String data) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableEntryException, JoseException {
		FileInputStream is = new FileInputStream("lib/Keystore/opkeystore.jks");
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		JsonWebSignature jws = new JsonWebSignature();
		keystore.load(is, "Cpassword".toCharArray());
		Enumeration<?> e = keystore.aliases();
		for (; e.hasMoreElements();) {
			String alias = (String) e.nextElement();
			KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias,
					new KeyStore.PasswordProtection("Cpassword".toCharArray()));
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
			byte[] digest = HMACUtils.generateHash(data.getBytes());
			String hmac = HMACUtils.digestAsPlainText(digest);
			jws.setPayload(hmac);
			jws.setKey(entry.getPrivateKey());
			X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
			jws.setCertificateChainHeaderValue(cert);
		}
		return jws.getCompactSerialization();
	}
}