package io.mosip.registration.processor.stages.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.packet.dto.BiometricSequence;
import io.mosip.registration.processor.core.packet.dto.DemographicSequence;
import io.mosip.registration.processor.core.packet.dto.HashSequence;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;

public class CheckSumGeneration {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckSumGeneration.class);

	private static FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter = new FilesystemCephAdapterImpl();

	private CheckSumGeneration() {

	}

	public static byte[] generatePacketInfoHash(HashSequence sequence, String registrationId) {

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

	private static void generateBiometricsHash(BiometricSequence biometricSequence, String registrationId) {
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

	private static void generateBiometricInfosHash(List<String> hashOrder, String registrationId, String Person) {
		hashOrder.forEach(file -> {
			byte[] filebyte = null;
			try {
				InputStream fileStream = adapter.getFile(registrationId,
						PacketFiles.BIOMETRIC.name() + File.separator + Person + File.separator + file);
				filebyte = toByteArray(fileStream);
			} catch (IOException e) {
				LOGGER.error("Unable to read the input stream", e);
			}
			generateHash(filebyte);

		});
	}

	private static void generateDemographicHash(DemographicSequence demographicSequence, String registrationId) {
		List<String> hashOrder = demographicSequence.getApplicant();

		hashOrder.forEach(document -> {
			InputStream fileStream = null;
			byte[] filebyte = null;
			try {
				if (document.equalsIgnoreCase(PacketFiles.DEMOGRAPHICINFO.name())) {
					fileStream = adapter.getFile(registrationId,
							PacketFiles.DEMOGRAPHIC.name() + File.separator + PacketFiles.DEMOGRAPHICINFO.name());
				} else {
					fileStream = adapter.getFile(registrationId, PacketFiles.DEMOGRAPHIC.name() + File.separator
							+ PacketFiles.APPLICANT.name() + File.separator + document);
				}
				filebyte = toByteArray(fileStream);
			} catch (IOException e) {
				LOGGER.error("Unable to read the input stream", e);
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

	private static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		return out.toByteArray();
	}
}
