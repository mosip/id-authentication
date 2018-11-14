package io.mosip.kernel.keymanager.softhsm.util;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class X509CertUtil {

	/**
	 * 
	 */
	private X509CertUtil() {
	}

	/**
	 * @param keyPair
	 * @return
	 */
	public static X509CertImpl generateX509Certificate(KeyPair keyPair) {

		String commonName = "Mosip";
		String organizationalUnit = "Mosip.io";
		String organization = "IITB";
		String country = "IND";
		int validDays = 365;

		X509CertImpl cert = null;
		try {
			X500Name distinguishedName = new X500Name(commonName, organizationalUnit, organization, country);
			PrivateKey privkey = keyPair.getPrivate();
			X509CertInfo info = new X509CertInfo();
			CertificateValidity interval = getCertificateValidity(validDays);
			BigInteger sn = new BigInteger(64, new SecureRandom());
			info.set(X509CertInfo.VALIDITY, interval);
			info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
			info.set(X509CertInfo.SUBJECT, distinguishedName);
			info.set(X509CertInfo.ISSUER, distinguishedName);
			info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
			info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
			AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
			info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));
			cert = signCertificate(privkey, info);
			algo = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
			info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
			cert = signCertificate(privkey, info);
			// pemEncodeToFile("cert-demo.pem", cert);
		} catch (IOException | NoSuchAlgorithmException | CertificateException | InvalidKeyException
				| NoSuchProviderException | SignatureException e) {
			e.printStackTrace();
		}
		return cert;
	}

	/**
	 * @param privkey
	 * @param info
	 * @return
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 */
	private static X509CertImpl signCertificate(PrivateKey privkey, X509CertInfo info) throws CertificateException,
			NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
		X509CertImpl cert;
		cert = new X509CertImpl(info);
		cert.sign(privkey, "SHA1withRSA");
		return cert;
	}

	/**
	 * @param validDays
	 * @return
	 */
	private static CertificateValidity getCertificateValidity(int validDays) {
		LocalDateTime since = LocalDateTime.now();
		LocalDateTime until = since.plusDays(validDays);
		return new CertificateValidity(Timestamp.valueOf(since), Timestamp.valueOf(until));
	}

	/**
	 * @param filename
	 * @param obj
	 */
	public static void pemEncodeToFile(String filename, Object obj) {
		try (JcaPEMWriter pw = new JcaPEMWriter(new FileWriter(filename))) {
			pw.writeObject(obj);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}