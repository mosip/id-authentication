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

/**
 * The Class ApplicantDocumentValidation.
 * 
 * M1039285
 */
public class ApplicantDocumentValidation {

	/** The identity iterator. */
	IdentityIteratorUtil identityIterator = new IdentityIteratorUtil();

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto;

	/** The reg id. */
	String regId;

	/**
	 * Instantiates a new applicant document validation.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	public ApplicantDocumentValidation(InternalRegistrationStatusDto registrationStatusDto) {
		this.registrationStatusDto = registrationStatusDto;
	}

	/**
	 * Validate document.
	 *
	 * @param identity
	 *            the identity
	 * @param registrationId
	 *            the registration id
	 * @return true, if successful
	 */
	public boolean validateDocument(Identity identity, String registrationId) {
		boolean isApplicantDocumentVerified = false;
		regId = registrationId;

		String applicantType = identityIterator.getFieldValue(identity.getMetaData(), JsonConstant.APPLICANTTYPE);

		if (applicantType.equalsIgnoreCase(ApplicantType.CHILD.name())
				&& checkDocumentAvailability(identity, DocumentCategory.POR.name())) {
			isApplicantDocumentVerified = true;
		}

		else if (applicantType.equalsIgnoreCase(ApplicantType.ADULT.name())
				&& checkDocumentAvailability(identity, DocumentCategory.POI.name())
				&& checkDocumentAvailability(identity, DocumentCategory.POA.name()))
			isApplicantDocumentVerified = true;

		if (isApplicantDocumentVerified) {
			isApplicantDocumentVerified = validateRegistrationStatus(identity);
		}
		return isApplicantDocumentVerified;
	}

	/**
	 * Validate registration status.
	 *
	 * @param identity
	 *            the identity
	 * @return true, if successful
	 */
	public boolean validateRegistrationStatus(Identity identity) {

		Boolean isValidStatus = false;
		String isVerfied = identityIterator.getFieldValue(identity.getMetaData(), JsonConstant.ISVERIFIED);
		String registrationType = identityIterator.getFieldValue(identity.getMetaData(), JsonConstant.REGISTRATIONTYPE);

		if (isVerfied.equalsIgnoreCase(JsonConstant.VERIFIED)
				&& registrationType.equalsIgnoreCase(SyncTypeDto.NEW.name())
				&& checkDocumentAvailability(identity, DocumentCategory.POB.name()))
			isValidStatus = true;

		return isValidStatus;
	}

	/**
	 * Check document availability.
	 *
	 * @param identity
	 *            the identity
	 * @param category
	 *            the category
	 * @return true, if successful
	 */
	public boolean checkDocumentAvailability(Identity identity, String category) {

		for (Document doc : identity.getDocuments()) {

			if (doc.getDocumentCategory().equalsIgnoreCase(category)) {
				String documentname = doc.getDocumentName();

				List<String> hashSequence = identityIterator.getHashSequence(identity.getHashSequence(),
						PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name());
				if (hashSequence != null && hashSequence.contains(documentname))
					return true;

			}
		}
		registrationStatusDto.setStatusComment(category + " Document was not available for " + regId);
		return false;
	}

}
