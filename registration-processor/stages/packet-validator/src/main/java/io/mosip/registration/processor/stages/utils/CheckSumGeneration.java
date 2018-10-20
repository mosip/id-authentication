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
 * @author M1048358 Alok Ranjan
 *
 */

public class CheckSumGeneration {
	
	public static final String FILE_SEPARATOR="\\";

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckSumGeneration.class);

	private FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter;

	public CheckSumGeneration(FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter) {
		this.adapter = adapter;
	}

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

	private static void generateHash(final byte[] byteArray) {
		// Hash updation
		if (byteArray != null) {
			HMACUtils.update(byteArray);
		}
	}
	
}
