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

public class LocalRunBootstrap {

	private static final Logger LOGGER = Logger.getLogger(LocalRunBootstrap.class);

	private static final String RIG_KEY_ALIAS = "keyalias";
	private static final String RIG_P12_PASS_DEFAULT = "qwerty@123";

	private static final String SEED_KEY_ALIAS = "partner-cn";
	private static final String SEED_P12_PASS_DEFAULT = "1234";

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
		String delegatedUrl = valueOrDefault("local.delegated.partialPartnerKeyUrl", "").trim();
		if (delegatedUrl.contains("/")) {
			signingPartners.add(delegatedUrl.substring(delegatedUrl.lastIndexOf('/') + 1));
		}
		if (!signingPartners.contains(PartnerRegistration.organizationName)) {
			signingPartners.add(PartnerRegistration.organizationName);
		}
		X509Certificate idaCert = installIdaEncryptionCertificate();
		seedThumbprints(idaCert);
		installPartnerSigningKey(signingPartners);
		installFtmAndDeviceSigningKeys();
		installMockSbiDeviceKeys();
		seedPrerequisiteIds();
		LOGGER.info("==================== LOCAL RUN BOOTSTRAP DONE ===============");
	}

	private static void enableMockSbiDebugLogging() {
		if (IdAuthConfigManager.IsDebugEnabled()) {
			Logger.getLogger("io.mosip.mock.sbi").setLevel(org.apache.log4j.Level.DEBUG);
			LOGGER.info("Mock SBI debug logging enabled");
		}
	}

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

		LOGGER.info("Seeded local partner: " + PartnerRegistration.partnerId);
	}

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

	private static void installFtmAndDeviceSigningKeys() {
		Path ftmSource = resolveResource(required("local.certs.ftmP12"));
		String ftmPass = required("local.certs.ftmP12Password");
		String ftmAlias = required("local.certs.ftmP12Alias");
		installGenericSigningKey(ftmSource, ftmAlias, ftmPass, "ftm-partner.p12");
		installGenericSigningKey(ftmSource, ftmAlias, ftmPass, "ftm-csk-partner.p12");

		Path deviceSource = Paths.get(System.getProperty("user.dir"), "Biometric Devices", "Face", "Keys",
				"mosipface.p12");
		installGenericSigningKey(deviceSource, "mosipface", "mosipface", "device-partner.p12");
		installGenericSigningKey(deviceSource, "mosipface", "mosipface", "device-dsk-partner.p12");
	}

	private static void installGenericSigningKey(Path source, String sourceAlias, String sourcePass,
			String targetFileName) {
		try {
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
