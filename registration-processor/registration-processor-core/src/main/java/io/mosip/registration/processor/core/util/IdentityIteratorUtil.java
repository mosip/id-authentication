package io.mosip.registration.processor.core.util;

import java.util.Collections;
import java.util.List;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;

/**
 * The Class IdentityIteratorUtil.
 * 
 * M1039285
 */
public class IdentityIteratorUtil {
	private static final String EMPTY_STRING = "";
	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(IdentityIteratorUtil.class);

	/**
	 * Instantiates a new identity iterator util.
	 */
	public IdentityIteratorUtil() {
		super();
	}

	/**
	 * Gets the hash sequence.
	 *
	 * @param hashSequence
	 *            the hash sequence
	 * @param field
	 *            the field
	 * @return the hash sequence
	 */
	public List<String> getHashSequence(List<FieldValueArray> hashSequence, String field) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"IdentityIteratorUtil::getHashSequence()::entry");

		for (FieldValueArray hash : hashSequence) {
			if (hash.getLabel().equalsIgnoreCase(field)) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
						"IdentityIteratorUtil::getHashSequence()::exit");

				return hash.getValue();
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"IdentityIteratorUtil::getHashSequence()::exit");

		return Collections.emptyList();

	}

	/**
	 * Gets the metadata label value.
	 *
	 * @param metaDataList
	 *            the meta data list
	 * @param field
	 *            the field
	 * @return the metadata label value
	 */
	public String getMetadataLabelValue(List<FieldValue> metaDataList, String field) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"IdentityIteratorUtil::getMetadataLabelValue()::entry");

		for (FieldValue metadataObjects : metaDataList) {
			if (metadataObjects.getLabel().equalsIgnoreCase(field)) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
						"IdentityIteratorUtil::getMetadataLabelValue()::exit");
				return metadataObjects.getValue();
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"IdentityIteratorUtil::getMetadataLabelValue()::exit");

		return null;

	}

	/**
	 * Gets the field value.
	 *
	 * @param data
	 *            the data
	 * @param label
	 *            the label
	 * @return the field value
	 */
	public String getFieldValue(List<FieldValue> data, String label) {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"IdentityIteratorUtil::getFieldValue()::entry");

		String fieldValue = null;
		for (FieldValue field : data) {
			if (field.getLabel().equalsIgnoreCase(label)) {
				fieldValue = field.getValue();
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
						"IdentityIteratorUtil::getFieldValue()::exit");

				return fieldValue != null && fieldValue.trim().equals(EMPTY_STRING) ? null : fieldValue;

			}

		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.USERID.toString(), "",
				"IdentityIteratorUtil::getFieldValue()::exit");

		return fieldValue;
	}
}
