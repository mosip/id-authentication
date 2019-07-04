package io.mosip.authentication.partnerdemo.service.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.HMACUtils;

// 
/**
 * @author Sanjay Murali
 * The Class DigitalSign is used to digitally sign the request.
 */
@RestController
public class DigitalSign {

	/** The mapper. */
	ObjectMapper mapper = new ObjectMapper();

	/**
	 * Sign.
	 *
	 * @param data the data
	 * @return the string
	 * @throws KeyStoreException the key store exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 * @throws CertificateException the certificate exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnrecoverableEntryException the unrecoverable entry exception
	 * @throws JoseException the jose exception
	 * @throws InvalidKeySpecException the invalid key spec exception
	 */
	@PostMapping(path = "/sign")
	public String sign(@RequestBody String data) throws KeyStoreException, NoSuchAlgorithmException,
	CertificateException, IOException, UnrecoverableEntryException, JoseException, InvalidKeySpecException {
		FileInputStream pkeyfis = new FileInputStream("lib/Keystore/privkey1.pem");
		String pKey = getFileContent(pkeyfis, "UTF-8");
		FileInputStream certfis = new FileInputStream("lib/Keystore/cert1.pem");
		String cert =  getFileContent(certfis, "UTF-8");
		pKey = pKey.replaceAll("-----BEGIN (.*)-----\n", "");
		pKey = pKey.replaceAll("-----END (.*)----\n", "");
		pKey = pKey.replaceAll("\\s", "");
		cert = cert.replaceAll("-----BEGIN (.*)-----\n", "");
		cert = cert.replaceAll("-----END (.*)----\n", "");
		cert = cert.replaceAll("\\s", "");
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(cert)));
		List<X509Certificate> certList = new ArrayList<>();
		certList.add(certificate);
		X509Certificate[] certArray = certList.toArray(new X509Certificate[]{});
		JsonWebSignature jws = new JsonWebSignature();
		jws.setCertificateChainHeaderValue(certArray);
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
		jws.setPayload(HMACUtils.digestAsPlainText(HMACUtils.generateHash(data.getBytes())));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		jws.setKey(kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pKey))));

		/*FileInputStream is = new FileInputStream("lib/Keystore/opkeystore.jks");
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
		}*/
		return jws.getCompactSerialization();
		
		 //return dynamicCertificateAndSign(data);
		 
		 
	}


	/**
	 * Gets the file content.
	 *
	 * @param fis the fis
	 * @param encoding the encoding
	 * @return the file content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String getFileContent(FileInputStream fis,String encoding ) throws IOException
	{
		try( BufferedReader br =
				new BufferedReader( new InputStreamReader(fis, encoding )))
		{
			StringBuilder sb = new StringBuilder();
			String line;
			while(( line = br.readLine()) != null ) {
				sb.append( line );
				sb.append( '\n' );
			}
			return sb.toString();
		}
	}
	
	/*private String dynamicCertificateAndSign(String data) throws IOException, NoSuchAlgorithmException,
			CertificateException, InvalidKeyException, NoSuchProviderException, SignatureException,
			CertificateParsingException, CertificateEncodingException, JoseException {
		String commonName = "sanz";
		String organizationalUnit = "Mindtree Hi-Tech World";
		String organization = "Mindtree Limited";
		String country = "india";

		int keySize = 2048;
		int validDays = 9999;
		X500Name distinguishedName = new X500Name(commonName, organizationalUnit, organization, country);
		KeyPair kp = generateRSAKeyPair(keySize);

		PrivateKey privkey = kp.getPrivate();
		X509CertInfo info = new X509CertInfo();

		Date since = new Date(); // Since Now
		Date until = new Date(since.getTime() + validDays * 86400000l); // Until x days (86400000 milliseconds in one
																		// day)

		CertificateValidity interval = new CertificateValidity(since, until);
		BigInteger sn = new BigInteger(64, new SecureRandom());

		info.set(X509CertInfo.VALIDITY, interval);
		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
		info.set(X509CertInfo.SUBJECT, distinguishedName);
		info.set(X509CertInfo.ISSUER, distinguishedName);
		info.set(X509CertInfo.KEY, new CertificateX509Key(kp.getPublic()));
		info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));

		AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

		// Sign the cert to identify the algorithm that is used.
		X509CertImpl cert = new X509CertImpl(info);
		cert.sign(privkey, "SHA1withRSA");

		// Update the algorithm and sign again
		algo = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
		info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);

		cert = new X509CertImpl(info);
		cert.sign(privkey, "SHA1withRSA");
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
		List<X509Certificate> certList = new ArrayList<>();
		certList.add(certificate);
		X509Certificate[] certArray = certList.toArray(new X509Certificate[] {});

		JsonWebSignature jws = new JsonWebSignature();
		jws.setCertificateChainHeaderValue(certArray);
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
		byte[] emptyArray = new byte[0];
		jws.setPayload(HMACUtils.digestAsPlainText(HMACUtils.generateHash(emptyArray)));
		jws.setKey(kp.getPrivate());

		return jws.getCompactSerialization();
	}

	private static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {

		KeyPairGenerator kpg;

		kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(keySize);

		KeyPair kp = kpg.genKeyPair();

		return kp;
	}*/
}