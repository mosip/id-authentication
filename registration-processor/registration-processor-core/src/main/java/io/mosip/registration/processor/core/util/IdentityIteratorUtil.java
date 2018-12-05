package io.mosip.registration.processor.core.util;

import java.util.Collections;
import java.util.List;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;

public class IdentityIteratorUtil {

	public IdentityIteratorUtil() {
		super();
	}

	public List<String> getHashSequence(List<FieldValueArray> hashSequence, String field) {
		for (FieldValueArray hash : hashSequence) {
			if (hash.getLabel().equalsIgnoreCase(field)) {
				return hash.getValue();
			}
		}
		return Collections.emptyList();

	}

	public String getFieldValue(List<FieldValue> metaData, String label) {
		for (FieldValue field : metaData) {
			if (field.getLabel().equalsIgnoreCase(label))
				return field.getValue();

		}
		return null;
	}
}
