package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
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

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(CheckSumGeneration.class);

	/** The adapter. */
	private FileSystemAdapter<InputStream, Boolean> adapter;

	/**
	 * Instantiates a new check sum generation.
	 *
	 * @param adapter
	 *            the adapter
	 */
	public CheckSumGeneration(FileSystemAdapter<InputStream, Boolean> adapter) {
		this.adapter = adapter;
	}

	/**
	 * Generate packet info hash.
	 *
	 * @param hashSequence
	 *            the hash sequence
	 * @param registrationId
	 *            the registration id
	 * @return the byte[]
	 */
	public byte[] generateIdentityHash(List<FieldValueArray> hashSequence, String registrationId) {

		for (FieldValueArray fieldValueArray : hashSequence) {

			if (PacketFiles.APPLICANTBIOMETRICSEQUENCE.name().equalsIgnoreCase(fieldValueArray.getLabel())) {

				generateBiometricsHash(fieldValueArray.getValue(), registrationId);

			} else if (PacketFiles.INTRODUCERBIOMETRICSEQUENCE.name().equalsIgnoreCase(fieldValueArray.getLabel())) {

				generateBiometricsHash(fieldValueArray.getValue(), registrationId);

			} else if (PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name().equalsIgnoreCase(fieldValueArray.getLabel())) {

				generateDemographicHash(fieldValueArray.getValue(), registrationId);
			}
		}

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();

	}

	/**
	 * Generate biometric infos hash.
	 *
	 * @param hashOrder
	 *            the hash order
	 * @param registrationId
	 *            the registration id
	 * @param personType
	 *            the person type
	 */
	private void generateBiometricsHash(List<String> hashOrder, String registrationId) {
		hashOrder.forEach(file -> {
			byte[] filebyte = null;
			try {
				InputStream fileStream = adapter.getFile(registrationId,
						PacketFiles.BIOMETRIC.name() + FILE_SEPARATOR + file.toUpperCase());

				filebyte = IOUtils.toByteArray(fileStream);
			} catch (IOException e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(), StatusMessage.INPUTSTREAM_NOT_READABLE,
						e.getMessage() + ExceptionUtils.getStackTrace(e));
			}
			generateHash(filebyte);

		});
	}

	/**
	 * Generate demographic hash.
	 *
	 * @param fieldValueArray
	 *            the field value array
	 * @param registrationId
	 *            the registration id
	 */
	private void generateDemographicHash(List<String> hashOrder, String registrationId) {
		hashOrder.forEach(document -> {
			byte[] filebyte = null;
			try {
				InputStream fileStream = adapter.getFile(registrationId,
						PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + document.toUpperCase());

				filebyte = IOUtils.toByteArray(fileStream);
			} catch (IOException e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(), StatusMessage.INPUTSTREAM_NOT_READABLE,
						e.getMessage() + ExceptionUtils.getStackTrace(e));
			}

			generateHash(filebyte);
		});
	}

	/**
	 * Generate hash.
	 *
	 * @param byteArray
	 *            the byte array
	 */
	private static void generateHash(final byte[] byteArray) {
		// Hash updation
		if (byteArray != null) {
			HMACUtils.update(byteArray);
		}
	}

	public byte[] generatePacketOSIHash(List<String> hashSequence2, String registrationId) {
		hashSequence2.forEach(value -> {
			byte[] valuebyte = null;
			try {
				InputStream fileStream = adapter.getFile(registrationId, value.toUpperCase());

				valuebyte = IOUtils.toByteArray(fileStream);
			} catch (IOException e) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(), StatusMessage.INPUTSTREAM_NOT_READABLE,
						e.getMessage() + ExceptionUtils.getStackTrace(e));
			}

			generateHash(valuebyte);
		});

		// generated hash
		return HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
	}

}
