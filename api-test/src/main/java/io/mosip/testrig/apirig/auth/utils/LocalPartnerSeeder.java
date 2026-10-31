package io.mosip.testrig.apirig.auth.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.testrig.apirig.auth.testrunner.MosipTestRunner;
import io.mosip.testrig.apirig.utils.PartnerRegistration;

/**
 * Stands in for the PMS policy calls MosipTestRunner makes before the
 * edited-policy tests in an environment run (createEditAndPublishPolicy and the
 * key URL it hands to PartnerRegistration.updatedpartnerKeyUrl).
 *
 * PMS does not run locally. What it would have pushed to IDA over websub ends
 * up in the ida.policy_data, api_key_data and partner_mapping tables, so those
 * rows are written here directly over JDBC. The policy body comes from the same
 * config/AuthPolicyUpdateAllowedAuthTypes.json the environment run publishes.
 *
 * Every write is an upsert keyed on fixed local ids, so reruns are idempotent.
 * Called only from LocalRunBootstrap, i.e. only when local run mode is on.
 */
final class LocalPartnerSeeder {

	private static final Logger LOGGER = Logger.getLogger(LocalPartnerSeeder.class);

	private static final String CREATED_BY = "apitest-local";

	private LocalPartnerSeeder() {
	}

	/** One policy row, built from a rig policy file. */
	private static final class LocalPolicy {
		final String id;
		final String file;

		LocalPolicy(String id, String file) {
			this.id = id;
			this.file = file;
		}
	}

	/** One partner-to-policy mapping and the api key it is reached by. */
	private static final class LocalMapping {
		final String partnerId;
		final String policyId;
		final String apiKey;

		LocalMapping(String partnerId, String policyId, String apiKey) {
			this.partnerId = partnerId;
			this.policyId = policyId;
			this.apiKey = apiKey;
		}

		String keyUrl() {
			return PartnerRegistration.mispLicKey + "/" + partnerId + "/" + apiKey;
		}
	}

	/**
	 * Seeds the rows and points the rig's edited-policy key URL at them. Returns
	 * any extra partner ids the caller must install signing keys under (none on
	 * this branch: the edited policy is mapped to the main partner).
	 */
	static List<String> seed() {
		List<String> extraPartners = new ArrayList<>();
		String url = IdAuthConfigManager.getproperty("local.db.url");
		if (url == null || url.isBlank()) {
			LOGGER.warn("local.db.url not set - edited-policy partner is not seeded");
			return extraPartners;
		}

		String mainPartner = PartnerRegistration.partnerId;
		LocalPolicy updatedPolicy = new LocalPolicy(value("local.policy.updated.id", "local_pol_updated"),
				"config/AuthPolicyUpdateAllowedAuthTypes.json");
		LocalMapping updated = new LocalMapping(mainPartner, updatedPolicy.id, "lk-p1-updated");

		try (Connection connection = DriverManager.getConnection(url, value("local.db.user", "postgres"),
				value("local.db.password", ""))) {
			upsertPolicy(connection, updatedPolicy);
			upsertMapping(connection, updated);
			resetTemporaryVid(connection);
			trustDeviceRootForFtm(connection);
			clearHotlist(connection);
		} catch (Exception e) {
			LOGGER.error("Failed to seed local partners/policies: " + e.getMessage(), e);
			return extraPartners;
		}

		PartnerRegistration.updatedpartnerKeyUrl = updated.keyUrl();
		LOGGER.info("Seeded local edited-policy partner; updatedpartnerKeyUrl=" + updated.keyUrl());
		return extraPartners;
	}

	/**
	 * IDA stores policy_data as the base64 text of the policy JSON (the form PMS
	 * publishes), so the rig's policy file is compacted and encoded the same way.
	 */
	private static void upsertPolicy(Connection connection, LocalPolicy policy) throws Exception {
		Path file = Paths.get(MosipTestRunner.getGlobalResourcePath(), policy.file);
		String json = new JSONObject(new String(Files.readAllBytes(file), StandardCharsets.UTF_8)).toString();
		byte[] data = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8))
				.getBytes(StandardCharsets.US_ASCII);
		String sql = "INSERT INTO ida.policy_data (policy_id, policy_data, policy_name, policy_status,"
				+ " policy_description, policy_commence_on, policy_expires_on, cr_by, cr_dtimes, is_deleted)"
				+ " VALUES (?, ?, ?, 'ACTIVE', ?, now() - interval '1 day', now() + interval '15 years', ?, now(), false)"
				+ " ON CONFLICT (policy_id) DO UPDATE SET policy_data = EXCLUDED.policy_data,"
				+ " policy_status = 'ACTIVE', policy_expires_on = EXCLUDED.policy_expires_on, is_deleted = false";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, policy.id);
			statement.setBytes(2, data);
			statement.setString(3, policy.id);
			statement.setString(4, "apitest local run: " + policy.file);
			statement.setString(5, CREATED_BY);
			statement.executeUpdate();
		}
	}

	private static void upsertMapping(Connection connection, LocalMapping mapping) throws Exception {
		String apiKeySql = "INSERT INTO ida.api_key_data (api_key_id, api_key_commence_on, api_key_expires_on,"
				+ " api_key_status, cr_by, cr_dtimes, is_deleted)"
				+ " VALUES (?, now() - interval '1 day', now() + interval '15 years', 'ACTIVE', ?, now(), false)"
				+ " ON CONFLICT (api_key_id) DO UPDATE SET api_key_status = 'ACTIVE',"
				+ " api_key_expires_on = EXCLUDED.api_key_expires_on, is_deleted = false";
		try (PreparedStatement statement = connection.prepareStatement(apiKeySql)) {
			statement.setString(1, mapping.apiKey);
			statement.setString(2, CREATED_BY);
			statement.executeUpdate();
		}
		String mappingSql = "INSERT INTO ida.partner_mapping (partner_id, policy_id, api_key_id, cr_by, cr_dtimes,"
				+ " is_deleted) VALUES (?, ?, ?, ?, now(), false)"
				+ " ON CONFLICT (partner_id, policy_id, api_key_id) DO UPDATE SET is_deleted = false";
		try (PreparedStatement statement = connection.prepareStatement(mappingSql)) {
			statement.setString(1, mapping.partnerId);
			statement.setString(2, mapping.policyId);
			statement.setString(3, mapping.apiKey);
			statement.setString(4, CREATED_BY);
			statement.executeUpdate();
		}
	}

	/**
	 * IDA decrements a temporary VID's transaction_limit on every use and deletes
	 * the identity_cache row once it is spent, so the seeded one-time VID only
	 * survives a single run. Its row is copied aside the first time it is seen
	 * and restored with a fresh limit on every run.
	 */
	private static void resetTemporaryVid(Connection connection) throws Exception {
		String idPrefix = IdAuthConfigManager.getproperty("local.tempVid.idPrefix");
		if (idPrefix == null || idPrefix.isBlank()) {
			return;
		}
		int limit = Integer.parseInt(value("local.tempVid.transactionLimit", "1"));
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE SCHEMA IF NOT EXISTS apitest_local");
			statement.execute("CREATE TABLE IF NOT EXISTS apitest_local.identity_cache_backup"
					+ " (LIKE ida.identity_cache INCLUDING ALL)");
		}
		try (PreparedStatement backup = connection.prepareStatement(
				"INSERT INTO apitest_local.identity_cache_backup SELECT * FROM ida.identity_cache"
						+ " WHERE id LIKE ? ON CONFLICT DO NOTHING");
				PreparedStatement restore = connection.prepareStatement(
						"INSERT INTO ida.identity_cache SELECT * FROM apitest_local.identity_cache_backup"
								+ " WHERE id LIKE ? ON CONFLICT (id) DO UPDATE SET transaction_limit = ?")) {
			backup.setString(1, idPrefix.trim() + "%");
			backup.executeUpdate();
			restore.setString(1, idPrefix.trim() + "%");
			restore.setInt(2, limit);
			int restored = restore.executeUpdate();
			try (PreparedStatement setLimit = connection
					.prepareStatement("UPDATE ida.identity_cache SET transaction_limit = ? WHERE id LIKE ?")) {
				setLimit.setInt(1, limit);
				setLimit.setString(2, idPrefix.trim() + "%");
				setLimit.executeUpdate();
			}
			LOGGER.info("Reset temporary VID " + idPrefix + "* to transaction_limit=" + limit + " (" + restored
					+ " row)");
		}
	}

	/**
	 * LocalRunBootstrap signs the biometric digitalId with the Mock SBI's
	 * MosipFace certificate, which chains to the DEVICE-domain RootCA (the shipped
	 * FTM chain's RootCAFTM expired in 2022). IDA validates the digitalId against
	 * the FTM domain, so the same RootCA is registered there too. init.sql does not
	 * carry this row, so a freshly created database rejects every biometric
	 * request with "TRUST VALID : false" until it is added.
	 */
	/**
	 * Starts every local run with no hotlist entries. The env hotlist tests block and
	 * unblock the run's own partner, but UnBlockHotlistAPIForPartnerId.yml never
	 * unblocks the MISP licence key (its third case repeats the API key). An env run
	 * registers a new partner, so that leftover block is harmless there; locally the
	 * partner and MISP key are fixed, so it would fail every auth of the next run.
	 */
	private static void clearHotlist(Connection connection) throws Exception {
		try (PreparedStatement statement = connection.prepareStatement("DELETE FROM ida.hotlist_cache")) {
			int removed = statement.executeUpdate();
			LOGGER.info("Cleared " + removed + " hotlist entr" + (removed == 1 ? "y" : "ies") + " left by a previous run");
		}
	}

	private static void trustDeviceRootForFtm(Connection connection) throws Exception {
		String subject = value("local.trust.ftmRootSubject", "CN=RootCA,OU=Mosip,O=Mosip,ST=Karnataka,C=IN");
		String certId = "apitest-local-ftm-rootca";
		String sql = "INSERT INTO ida.ca_cert_store (cert_id, cert_subject, cert_issuer, issuer_id, cert_not_before,"
				+ " cert_not_after, crl_uri, cert_data, cert_thumbprint, cert_serial_no, partner_domain, cr_by,"
				+ " cr_dtimes, is_deleted, ca_cert_type)"
				+ " SELECT ?, cert_subject, cert_issuer, ?, cert_not_before, cert_not_after, crl_uri, cert_data,"
				+ " cert_thumbprint, cert_serial_no, 'FTM', ?, now(), false, ca_cert_type FROM ida.ca_cert_store"
				+ " WHERE cert_subject = ? AND partner_domain = 'DEVICE' LIMIT 1 ON CONFLICT DO NOTHING";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, certId);
			statement.setString(2, certId);
			statement.setString(3, CREATED_BY);
			statement.setString(4, subject);
			int added = statement.executeUpdate();
			LOGGER.info("Device RootCA trusted for FTM domain (" + (added == 1 ? "added" : "already present") + ")");
		}
	}

	private static String value(String key, String fallback) {
		String value = IdAuthConfigManager.getproperty(key);
		return (value == null || value.isBlank()) ? fallback : value.trim();
	}
}
