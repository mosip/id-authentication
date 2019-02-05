package io.mosip.registration.processor.core.util;

import java.util.Collections;
import java.util.List;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;

/**
 * The Class IdentityIteratorUtil.
 * 
 * M1039285
 */
public class IdentityIteratorUtil {
	private static final String EMPTY_STRING = "";

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
		for (FieldValueArray hash : hashSequence) {
			if (hash.getLabel().equalsIgnoreCase(field)) {
				return hash.getValue();
			}
		}
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
		for (FieldValue metadataObjects : metaDataList) {
			if (metadataObjects.getLabel().equalsIgnoreCase(field)) {
				return metadataObjects.getValue();
			}
		}
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
		String fieldValue = null;
		for (FieldValue field : data) {
			if (field.getLabel().equalsIgnoreCase(label)) {
				fieldValue = field.getValue();
				return fieldValue != null && fieldValue.trim().equals(EMPTY_STRING) ? null : fieldValue;

			}

		}
		return fieldValue;
	}
}
