package io.mosip.registration.processor.core.util;

import java.util.List;

import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;

public class IdentityIteratorUtil {

	public IdentityIteratorUtil() {
		super();
	}

	public Document forDocument(List<Document> documents, String DocLabel) {
		for (Document doc : documents) {
			if (doc.getDocumentCategory().equalsIgnoreCase(DocLabel)) {
				return doc;
			}

		}
		return null;

	}

	public List<String> forHashSequence(List<FieldValueArray> hashSequence, String field) {
		for (FieldValueArray dochash : hashSequence) {
			if (dochash.getLabel().equalsIgnoreCase(field)) {
				return dochash.getValue();
			}
		}
		return null;

	}

	public String forMetaData(List<FieldValue> metaData, String label) {
		for (FieldValue field : metaData) {
			if (field.getLabel().matches(label))
				return field.getValue();

		}
		return null;
	}
}
