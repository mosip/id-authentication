package io.mosip.testrig.apirig.auth.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;

import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.EncryptionDecrptionUtil;
import io.mosip.testrig.apirig.utils.PartnerRegistration;

/**
 * Stands in for the prerequisite chain when the rig runs against a local IDA.
 *
 * In an environment run MosipTestRunner registers a partner with PMS, publishes
 * policies, creates Keycloak users and generates a UIN/VID through idrepo. None
 * of those services exist in the local docker-compose stack, but the data they
 * would have produced is already seeded by
 * local-dev-setup/docker-compose/init.sql. This class reads that seeded data
 * from the local profile and installs it into the same static fields and files
 * the prerequisite chain would have populated, so the test scripts downstream
 * cannot tell the difference.
 *
 * Nothing here runs unless local run mode is switched on.
 */
public class LocalRunBootstrap {

	private static final Logger LOGGER = Logger.getLogger(LocalRunBootstrap.class);

	/** Alias and password the rig's KeyMgrUtil expects inside a partner p12. */
	private static final String RIG_KEY_ALIAS = "keyalias";
	private static final String RIG_P12_PASS_DEFAULT = "qwerty@123";

	/** Alias and password used by the keystore shipped with local-dev-setup. */
	private static final String SEED_KEY_ALIAS = "partner-cn";
	private static final String SEED_P12_PASS_DEFAULT = "1234";

	/**
	 * KeyMgrUtil prefixes the partner p12 file name with this thread-local when a
	 * request targets an rp- or ekyc- flavoured endpoint, so the same key is
	 * installed under each name it may be looked up by.
	 */
	private static final String[] KEY_FILE_PREFIXES = { "", "rp-", "ekyc-" };

	private LocalRunBootstrap() {
	}

	public static void initialize() {
		LOGGER.info("==================== LOCAL RUN BOOTSTRAP ====================");
		enableMockSbiDebugLogging();
		seedLanguages();
		seedRequiredSchemaFields();
		seedPartnerIdentity();
		List<String> signingPartners = new ArrayList<>();
		signingPartners.add(PartnerRegistration.partnerId);
		signingPartners.addAll(LocalPartnerSeeder.seed());
		X509Certificate idaCert = installIdaEncryptionCertificate();
		seedThumbprints(idaCert);
		installPartnerSigningKey(signingPartners);
		installFtmAndDeviceSigningKeys();
		installMockSbiDeviceKeys();
		seedPrerequisiteIds();
		LOGGER.info("==================== LOCAL RUN BOOTSTRAP DONE ===============");
	}

	/**
	 * The Mock SBI reports which biometric sample file it tried to read only at
	 * DEBUG. That is the single most useful line when captures come back empty, so
	 * it is turned on with the rig's own debug switch.
	 */
	private static void enableMockSbiDebugLogging() {
		if (IdAuthConfigManager.IsDebugEnabled()) {
			Logger.getLogger("io.mosip.mock.sbi").setLevel(org.apache.log4j.Level.DEBUG);
			LOGGER.info("Mock SBI debug logging enabled");
		}
	}

	/**
	 * BaseTestCase.getLanguageList() would otherwise read the masterdata actuator.
	 */
	private static void seedLanguages() {
		String configured = IdAuthConfigManager.getproperty("local.languages");
		if (configured == null || configured.isBlank()) {
			configured = "eng";
		}
		List<String> languages = new ArrayList<>();
		for (String language : configured.split(",")) {
			if (!language.isBlank()) {
				languages.add(language.trim());
			}
		}
		BaseTestCase.languageList.clear();
		BaseTestCase.languageList.addAll(languages);
		BaseTestCase.currentRunningLanguage = languages.get(0);
		LOGGER.info("Seeded language list: " + languages);
	}

	/**
	 * AdminTestUtil.getRequiredField() normally fills globalRequiredFields from the
	 * masterdata ID schema (/v1/masterdata/idschema/latest), which the local stack
	 * does not serve.
	 *
	 * IdAuthenticationUtil.isTestCaseValidForExecution() reads it for every demo,
	 * bio, eKYC and multi-factor test to decide whether biometrics are part of the
	 * schema. Left null it throws NullPointerException inside isElementPresent()
	 * before the request is ever built, which fails those tests rather than
	 * skipping them.
	 */
	private static void seedRequiredSchemaFields() {
		String configured = IdAuthConfigManager.getproperty("local.requiredFields");
		if (configured == null || configured.isBlank()) {
			configured = "individualBiometrics";
		}
		JSONArray fields = new JSONArray();
		for (String field : configured.split(",")) {
			if (!field.isBlank()) {
				fields.put(field.trim());
			}
		}
		AdminTestUtil.globalRequiredFields = fields;
		LOGGER.info("Seeded required schema fields: " + fields);
	}

	/**
	 * The test scripts build $partnerKeyURL$ inline from these three fields, so
	 * setting them is equivalent to having run the whole PMS registration chain.
	 */
	private static void seedPartnerIdentity() {
		PartnerRegistration.mispLicKey = required("local.partner.mispLicKey");
		PartnerRegistration.partnerId = required("local.partner.id");
		PartnerRegistration.apiKey = required("local.partner.apiKey");
		PartnerRegistration.updatedApiKey = PartnerRegistration.apiKey;

		PartnerRegistration.ekycPartnerId = valueOrDefault("local.partner.ekyc.id", PartnerRegistration.partnerId);
		PartnerRegistration.kycApiKey = valueOrDefault("local.partner.ekyc.apiKey", PartnerRegistration.apiKey);

		PartnerRegistration.partnerKeyUrl = PartnerRegistration.mispLicKey + "/" + PartnerRegistration.partnerId + "/"
				+ PartnerRegistration.apiKey;
		PartnerRegistration.updatedpartnerKeyUrl = PartnerRegistration.partnerKeyUrl;
		PartnerRegistration.ekycPartnerKeyUrl = PartnerRegistration.mispLicKey + "/"
				+ PartnerRegistration.ekycPartnerId + "/" + PartnerRegistration.kycApiKey;

		LOGGER.info("Seeded partnerKeyUrl: " + PartnerRegistration.partnerKeyUrl);
	}

	/**
	 * EncryptionDecrptionUtil encrypts every auth request with IDA's public key,
	 * which KeyMgrUtil.getCertificate() reads from <keysDir>/ida-partner.cer. In an
	 * environment run that file comes from
	 * /idauthentication/v1/internal/getCertificate, served by
	 * authentication-internal-service, which the local stack does not run.
	 */
	private static X509Certificate installIdaEncryptionCertificate() {
		try {
			Path source = resolveResource(required("local.certs.idaPartner"));
			X509Certificate certificate = readCertificate(source);

			File keysDir = new File(AdminTestUtil.getKeysDirPath());
			if (!keysDir.exists() && !keysDir.mkdirs()) {
				LOGGER.error("Could not create keys directory: " + keysDir.getAbsolutePath());
				return certificate;
			}

			File target = new File(keysDir, "ida-partner.cer");
			Files.copy(source, target.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("Installed IDA encryption certificate at " + target.getAbsolutePath());
			return certificate;
		} catch (Exception e) {
			LOGGER.error("Failed to install the IDA encryption certificate: " + e.getMessage(), e);
			return null;
		}
	}

	private static void seedThumbprints(X509Certificate certificate) {
		if (certificate == null) {
			return;
		}
		String thumbprint = EncryptionDecrptionUtil.getCertificateThumbprint(certificate);
		EncryptionDecrptionUtil.partnerThumbPrint = thumbprint;
		EncryptionDecrptionUtil.internalThumbPrint = thumbprint;
		EncryptionDecrptionUtil.idaFirThumbPrint = thumbprint;
		LOGGER.info("Seeded IDA certificate thumbprint: " + thumbprint);
	}

	/**
	 * IDA verifies the JWS on every auth request against the certificate held in
	 * ida.partner_data. The matching private key is the keystore shipped with
	 * local-dev-setup, but it uses a different alias and password than the one the
	 * rig's KeyMgrUtil expects, so it is re-packaged rather than copied.
	 *
	 * Any extra partners LocalPartnerSeeder returns carry the same certificate, so
	 * the key is installed under each of their names as well.
	 */
	private static void installPartnerSigningKey(List<String> partnerIds) {
		try {
			Path source = resolveResource(required("local.certs.p12"));
			String sourcePass = valueOrDefault("local.certs.p12Password", SEED_P12_PASS_DEFAULT);
			String sourceAlias = valueOrDefault("local.certs.p12Alias", SEED_KEY_ALIAS);

			KeyStore sourceStore = KeyStore.getInstance("PKCS12");
			try (InputStream in = new FileInputStream(source.toFile())) {
				sourceStore.load(in, sourcePass.toCharArray());
			}

			Key privateKey = sourceStore.getKey(sourceAlias, sourcePass.toCharArray());
			Certificate[] chain = sourceStore.getCertificateChain(sourceAlias);
			if (privateKey == null || chain == null) {
				LOGGER.error("Alias '" + sourceAlias + "' not found in " + source);
				return;
			}

			String rigPass = System.getProperty("p12.password", RIG_P12_PASS_DEFAULT);
			File keysDir = new File(AdminTestUtil.getKeysDirPath());
			if (!keysDir.exists() && !keysDir.mkdirs()) {
				LOGGER.error("Could not create keys directory: " + keysDir.getAbsolutePath());
				return;
			}

			for (String partnerId : partnerIds) {
				for (String prefix : KEY_FILE_PREFIXES) {
					KeyStore target = KeyStore.getInstance("PKCS12");
					target.load(null, rigPass.toCharArray());
					target.setKeyEntry(RIG_KEY_ALIAS, privateKey, rigPass.toCharArray(), chain);

					File targetFile = new File(keysDir, prefix + partnerId + "-partner.p12");
					try (FileOutputStream out = new FileOutputStream(targetFile)) {
						target.store(out, rigPass.toCharArray());
					}
					LOGGER.info("Installed partner signing key at " + targetFile.getAbsolutePath());
				}

				File partnerCer = new File(keysDir, partnerId + "-partner.cer");
				String pem = Base64.getMimeEncoder().encodeToString(chain[0].getEncoded());
				Files.write(partnerCer.toPath(), pem.getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			LOGGER.error("Failed to install the partner signing key: " + e.getMessage(), e);
		}
	}

	/**
	 * BioDataUtility.constractBioIdentityRequest() signs the captured digitalId
	 * and device data with two more partner-type keys, on top of the main partner
	 * key above: KeyMgrUtility.getKeyEntry(FTM) and getKeyEntry(DEVICE), read from
	 * <keysDir>/ftm-partner.p12 and <keysDir>/device-partner.p12. In an environment
	 * run these come from a device/FTM partner registered through PMS
	 * (PartnerRegistration.deviceGeneration()), which local runs skip.
	 *
	 * Left uninstalled, KeyMgrUtility.getKeyEntry() throws, the caller in
	 * BioDataUtility swallows it and leaves the signature null, a subsequent
	 * response.replace(...) NPEs, constractBioIdentityRequest() returns null, the
	 * outer encrypt() call throws on the null payload, and
	 * getEncryptSessionKeyValue() swallows THAT and returns an empty map - so the
	 * request template's literal "requestSessionKey":"sessionkey" placeholder ships
	 * unresolved. IDA then throws javax.crypto.BadPaddingException trying to
	 * RSA-decrypt the literal string "sessionkey" as if it were the encrypted AES
	 * key, surfaced as IDA-MPA-003 "Unable to decrypt Request".
	 *
	 * The per-modality certs already shipped under "Biometric Devices" (one Keys
	 * subfolder per modality) chain to exactly the root CAs
	 * local-dev-setup/docker-compose/init.sql seeds
	 * into ida.ca_cert_store (RootCAFTM for FTM, RootCA for DEVICE) - confirmed via
	 * keytool. BioDataUtility signs generically, not per modality, so any one
	 * modality's cert covers every capture; Face's is used here.
	 */
	private static void installFtmAndDeviceSigningKeys() {
		installGenericSigningKey("Biometric Devices/Face/Keys/mosipfaceftm.p12", "mosipfaceftm", "mosipfaceftm",
				"ftm-partner.p12");
		installGenericSigningKey("Biometric Devices/Face/Keys/mosipface.p12", "mosipface", "mosipface",
				"device-partner.p12");

		installGenericSigningKey("Biometric Devices/Face/Keys/mosipface.p12", "mosipface", "mosipface",
				"ftm-csk-partner.p12");
		installGenericSigningKey("Biometric Devices/Face/Keys/mosipface.p12", "mosipface", "mosipface",
				"device-dsk-partner.p12");
	}

	private static void installGenericSigningKey(String relativeSourcePath, String sourceAlias, String sourcePass,
			String targetFileName) {
		try {
			Path source = Paths.get(System.getProperty("user.dir"), relativeSourcePath.split("/"));
			if (!source.toFile().isFile()) {
				LOGGER.error("Signing key source not found: " + source);
				return;
			}

			KeyStore sourceStore = KeyStore.getInstance("PKCS12");
			try (InputStream in = new FileInputStream(source.toFile())) {
				sourceStore.load(in, sourcePass.toCharArray());
			}

			Key privateKey = sourceStore.getKey(sourceAlias, sourcePass.toCharArray());
			Certificate[] chain = sourceStore.getCertificateChain(sourceAlias);
			if (privateKey == null || chain == null) {
				LOGGER.error("Alias '" + sourceAlias + "' not found in " + source);
				return;
			}

			String rigPass = System.getProperty("p12.password", RIG_P12_PASS_DEFAULT);
			File keysDir = new File(AdminTestUtil.getKeysDirPath());
			if (!keysDir.exists() && !keysDir.mkdirs()) {
				LOGGER.error("Could not create keys directory: " + keysDir.getAbsolutePath());
				return;
			}

			KeyStore target = KeyStore.getInstance("PKCS12");
			target.load(null, rigPass.toCharArray());
			target.setKeyEntry(RIG_KEY_ALIAS, privateKey, rigPass.toCharArray(), chain);

			File targetFile = new File(keysDir, targetFileName);
			try (FileOutputStream out = new FileOutputStream(targetFile)) {
				target.store(out, rigPass.toCharArray());
			}
			LOGGER.info("Installed signing key at " + targetFile.getAbsolutePath());
		} catch (Exception e) {
			LOGGER.error("Failed to install signing key '" + targetFileName + "': " + e.getMessage(), e);
		}
	}

	/**
	 * The Mock SBI resolves its device keystores
	 * (mosip.mock.sbi.file.*.keys.keystorefilename) against the certificate
	 * directory it is started with, not against the module directory. In an
	 * environment run PartnerRegistration.deviceGeneration() creates
	 * device-dsk-partner.p12 inside that directory, which is why the shipped
	 * defaults resolve.
	 *
	 * Locally there is no device-partner registration, so the checked-in device
	 * keystores under "Biometric Devices" are mirrored into the certificate
	 * directory. Without this the SBI logs "Failed to load keystore into local
	 * cache", every capture returns an empty "data", and the biometric map stays
	 * empty for all modalities.
	 */
	private static void installMockSbiDeviceKeys() {
		try {
			File source = new File(System.getProperty("user.dir"), "Biometric Devices");
			if (!source.isDirectory()) {
				LOGGER.warn("No 'Biometric Devices' directory at " + source.getAbsolutePath()
						+ " - Mock SBI captures will fail");
				return;
			}

			File target = new File(AdminTestUtil.getKeysDirPath(), "Biometric Devices");
			FileUtils.copyDirectory(source, target);
			LOGGER.info("Mirrored Mock SBI device keystores to " + target.getAbsolutePath());

			File profileSource = new File(System.getProperty("user.dir"), "resource");
			if (profileSource.isDirectory()) {
				File profileTarget = new File(AdminTestUtil.getKeysDirPath(), "resource");
				FileUtils.copyDirectory(profileSource, profileTarget);
				LOGGER.info("Mirrored Mock SBI biometric profiles to " + profileTarget.getAbsolutePath());
			} else {
				LOGGER.warn("No 'resource' directory at " + profileSource.getAbsolutePath()
						+ " - Mock SBI has no biometric profile data to capture from");
			}
		} catch (Exception e) {
			LOGGER.error("Failed to install Mock SBI device keystores: " + e.getMessage(), e);
		}
	}

	/**
	 * Replaces authPrerequisiteSuite.xml. Every entry becomes a $ID:key$ that the
	 * test scripts resolve exactly as if a prerequisite test had produced it.
	 */
	private static void seedPrerequisiteIds() {
		String relativePath = IdAuthConfigManager.getproperty("local.seed.idFile");
		if (relativePath == null || relativePath.isBlank()) {
			LOGGER.warn("No local.seed.idFile configured - prerequisite ids will not resolve");
			return;
		}

		Path seedFile = resolveResource(relativePath);
		if (!Files.exists(seedFile)) {
			LOGGER.error("Seed id file not found: " + seedFile);
			return;
		}

		Properties seeds = new Properties();
		try (InputStream in = new FileInputStream(seedFile.toFile())) {
			seeds.load(in);
		} catch (Exception e) {
			LOGGER.error("Failed to read " + seedFile + " : " + e.getMessage());
			return;
		}

		for (String key : seeds.stringPropertyNames()) {
			String value = seeds.getProperty(key);
			if (value != null && !value.isBlank()) {
				AdminTestUtil.autoGeneratedIDValueCache.put(key.trim(), value.trim());
			}
		}
		LOGGER.info("Seeded " + AdminTestUtil.autoGeneratedIDValueCache.size() + " prerequisite ids from "
				+ seedFile.getFileName());
	}

	private static X509Certificate readCertificate(Path path) throws Exception {
		String pem = new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
				.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "")
				.replaceAll("\\s", "");
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		return (X509Certificate) factory
				.generateCertificate(new java.io.ByteArrayInputStream(Base64.getDecoder().decode(pem)));
	}

	/** Resolves a path relative to the staged test resource directory. */
	private static Path resolveResource(String relativePath) {
		return Paths.get(MosipTestRunner.getGlobalResourcePath(), relativePath);
	}

	private static String required(String key) {
		String value = IdAuthConfigManager.getproperty(key);
		if (value == null || value.isBlank()) {
			throw new IllegalStateException(
					"Local run requires property '" + key + "' to be set in config/Ida-local.properties");
		}
		return value;
	}

	private static String valueOrDefault(String key, String fallback) {
		String value = IdAuthConfigManager.getproperty(key);
		return (value == null || value.isBlank()) ? fallback : value;
	}
}
