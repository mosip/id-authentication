package io.mosip.registration.processor.stages.utils;

import java.util.List;

import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.stages.packet.validator.code.DocumentCategory;
import io.mosip.registration.processor.status.code.ApplicantType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;

public class ApplicantDocumentValidation {

	public static final String APPLICANT_TYPE = "applicantType";
	public static final String REGISTRATION_TYPE = "registrationType";
	InternalRegistrationStatusDto registrationStatusDto;

	boolean isApplicantDocumentVerified = false;
	String applicantType;
	String registrationType;
	String regId;

	public ApplicantDocumentValidation(InternalRegistrationStatusDto registrationStatusDto) {
		this.registrationStatusDto = registrationStatusDto;
	}

	public boolean documentValidation(Identity identity, String registrationId) {
		regId = registrationId;
		for (FieldValue field : identity.getMetaData()) {

			if (field.getLabel().matches(APPLICANT_TYPE))
				applicantType = field.getValue();
			if (field.getLabel().matches(REGISTRATION_TYPE))
				registrationType = field.getValue();

		}

		if (registrationType.equalsIgnoreCase(SyncTypeDto.NEW.name())) {

			if (applicantType.equalsIgnoreCase(ApplicantType.CHILD.name())
					&& checkDocumentAvailability(DocumentCategory.POR.name(), identity))
				isApplicantDocumentVerified = true;

			else if (applicantType.equalsIgnoreCase(ApplicantType.ADULT.name())
					&& checkDocumentAvailability(DocumentCategory.POI.name(), identity)
					&& checkDocumentAvailability(DocumentCategory.POA.name(), identity)) {

				isApplicantDocumentVerified = true;

			}

		} else
			return true;

		return isApplicantDocumentVerified;
	}

	public boolean checkDocumentAvailability(String category, Identity identity) {

		for (Document doc : identity.getDocuments()) {

			if (doc.getDocumentCategory().equalsIgnoreCase(category)) {
				String documentname = doc.getDocumentName();
				if (demographicSequenceCheck(identity, documentname))
					return true;

				else
					break;

			}

		}
		registrationStatusDto.setStatusComment(category + " Document was not available for " + regId);
		return false;
	}

	public boolean demographicSequenceCheck(Identity identity, String documentname) {
		List<String> docNames = null;

		for (FieldValueArray dochash : identity.getHashSequence()) {
			if (dochash.getLabel().equalsIgnoreCase(PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name())) {
				docNames = dochash.getValue();
				if (docNames.contains(documentname))
					return true;

			}

		}

		return false;
	}
}
