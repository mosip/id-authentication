package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.packet.dto.BiometricSequence;
import io.mosip.registration.processor.core.packet.dto.DemographicSequence;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

/**
 * The Class CheckSumGeneration.
 *
 * @author M1048358 Alok Ranjan
 */

public class CheckSumGeneration {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CheckSumGeneration.class);

	/** The adapter. */
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/**
	 * Instantiates a new check sum generation.
	 *
	 * @param adapter the adapter
	 */
	public CheckSumGeneration(FileSystemAdapter<InputStream, Boolean> adapter) {
		this.adapter = adapter;
	}

	/**
	 * Generate packet info hash.
	 *
	 * @param sequence the sequence
	 * @param registrationId the registration id
	 * @return the byte[]
	 */
	public byte[] generatePacketInfoHash(HashSequence sequence, String registrationId) {

		// Sequence
		BiometricSequence biometricSequence = sequence.getBiometricSequence();
		DemographicSequence demographicSequence = sequence.getDemographicSequence();

		// generates biometric hash for applicant and introducer
		generateBiometricsHash(biometricSequence, registrationId);

		// generates demographic hash
		generateDemographicHash(demographicSequence, registrationId);

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();

	}

	/**
	 * Generate biometrics hash.
	 *
	 * @param biometricSequence the biometric sequence
	 * @param registrationId the registration id
	 */
	private void generateBiometricsHash(BiometricSequence biometricSequence, String registrationId) {
		// hash for applicant
		if (biometricSequence.getApplicant() != null) {
			generateBiometricInfosHash(biometricSequence.getApplicant(), registrationId, PacketFiles.APPLICANT.name());
		}

		// hash for introducer
		if (biometricSequence.getIntroducer() != null) {
			generateBiometricInfosHash(biometricSequence.getIntroducer(), registrationId,
					PacketFiles.INTRODUCER.name());
		}

	}

	/**
	 * Generate biometric infos hash.
	 *
	 * @param hashOrder the hash order
	 * @param registrationId the registration id
	 * @param personType the person type
	 */
	private void generateBiometricInfosHash(List<String> hashOrder, String registrationId, String personType) {
		hashOrder.forEach(file -> {
			byte[] filebyte = null;
			try {
				InputStream fileStream = adapter.getFile(registrationId,
						PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR + personType + FILE_SEPARATOR + file.toUpperCase());
				filebyte = IOUtils.toByteArray(fileStream);
			} catch (IOException e) {
				LOGGER.error(StatusMessage.INPUTSTREAM_NOT_READABLE, e);
			}
			generateHash(filebyte);

		});
	}

	/**
	 * Generate demographic hash.
	 *
	 * @param demographicSequence the demographic sequence
	 * @param registrationId the registration id
	 */
	private void generateDemographicHash(DemographicSequence demographicSequence, String registrationId) {
		List<String> hashOrder = demographicSequence.getApplicant();

		hashOrder.forEach(document -> {
			InputStream fileStream = null;
			byte[] filebyte = null;
			try {
				if (document.equalsIgnoreCase(PacketFiles.DEMOGRAPHICINFO.name())) {
					fileStream = adapter.getFile(registrationId,
							PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
				} else {
					fileStream = adapter.getFile(registrationId, PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR
							+ PacketFiles.APPLICANT.name() + FILE_SEPARATOR + document.toUpperCase());
				}
				filebyte = IOUtils.toByteArray(fileStream);
			} catch (IOException e) {
				LOGGER.error(StatusMessage.INPUTSTREAM_NOT_READABLE, e);
			}

			generateHash(filebyte);
		});
	}

	/**
	 * Generate hash.
	 *
	 * @param byteArray the byte array
	 */
	private static void generateHash(final byte[] byteArray) {
		// Hash updation
		if (byteArray != null) {
			HMACUtils.update(byteArray);
		}
	}

}
