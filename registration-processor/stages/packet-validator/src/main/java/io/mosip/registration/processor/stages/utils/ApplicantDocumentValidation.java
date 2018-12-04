package io.mosip.registration.processor.stages.utils;

import java.util.List;

import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.stages.packet.validator.code.DocumentCategory;
import io.mosip.registration.processor.status.code.ApplicantType;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;

public class ApplicantDocumentValidation {
	IdentityIteratorUtil identityIterator = new IdentityIteratorUtil();
	InternalRegistrationStatusDto registrationStatusDto;

	boolean isApplicantDocumentVerified = false;
	String applicantType;
	String registrationType;
	String isVerfied;
	String regId;

	public ApplicantDocumentValidation(InternalRegistrationStatusDto registrationStatusDto) {
		this.registrationStatusDto = registrationStatusDto;
	}

	public boolean documentValidation(Identity identity, String registrationId) {
		regId = registrationId;

		isVerfied = identityIterator.forMetaData(identity.getMetaData(), JsonConstant.ISVERIFIED.name());
		if (isVerfied.equalsIgnoreCase(JsonConstant.VERIFIED.name())
				&& checkDocumentAvailability(DocumentCategory.POB.name(), identity)) {

			registrationType = identityIterator.forMetaData(identity.getMetaData(),
					JsonConstant.REGISTRATIONTYPE.name());
			if (registrationType.equalsIgnoreCase(SyncTypeDto.NEW.name())) {

				applicantType = identityIterator.forMetaData(identity.getMetaData(), JsonConstant.APPLICANTTYPE.name());

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
		}

		return isApplicantDocumentVerified;
	}

	public boolean checkDocumentAvailability(String category, Identity identity) {

		Document document = identityIterator.forDocument(identity.getDocuments(), category);
		if (document != null) {
			String documentname = document.getDocumentName();
			List<String> hashSequence = identityIterator.forHashSequence(identity.getHashSequence(),
					PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name());
			if (hashSequence.contains(documentname))
				return true;

		}

		registrationStatusDto.setStatusComment(category + " Document was not available for " + regId);
		return false;
	}

}
