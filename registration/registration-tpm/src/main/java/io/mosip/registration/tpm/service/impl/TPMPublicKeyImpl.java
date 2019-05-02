package io.mosip.registration.tpm.service.impl;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.TPMInitialization;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;
import io.mosip.registration.tpm.service.TPMPublicKey;

import tss.tpm.TPM2B_PUBLIC_KEY_RSA;
import tss.tpm.TPMA_OBJECT;
import tss.tpm.TPMS_PCR_SELECTION;
import tss.tpm.TPMS_RSA_PARMS;
import tss.tpm.TPMS_SENSITIVE_CREATE;
import tss.tpm.TPMS_SIG_SCHEME_RSASSA;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMT_SYM_DEF_OBJECT;
import tss.tpm.TPM_ALG_ID;
import tss.tpm.TPM_HANDLE;
import tss.tpm.TPM_RH;

/**
 * The implementation of the {@link TPMPublicKey} interface to get the Public
 * Key
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class TPMPublicKeyImpl implements TPMPublicKey {

	private static final Logger LOGGER = TPMLogger.getLogger(TPMPublicKeyImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.registration_tpm.service.TPMPublicKey#getPublicKey()
	 */
	public byte[] getPublicKey() {
		try {
			LOGGER.info(Constants.LOG_PUBLIC_KEY, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					"Getting the Public Key from Platform TPM");

			TPMT_PUBLIC signingKeyPublicPart = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
					new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sign,
							TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth),
					new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL),
							new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256), 2048, 65537),
					new TPM2B_PUBLIC_KEY_RSA());

			TPM_HANDLE parentHandleForSigningKey = TPM_HANDLE.from(TPM_RH.ENDORSEMENT);

			TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(Constants.NULL_VECTOR, Constants.NULL_VECTOR);

			LOGGER.info(Constants.LOG_PUBLIC_KEY, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					"Completed getting the Public Key from Platform TPM");

			return TPMInitialization.getTPMInstance().CreatePrimary(parentHandleForSigningKey, sens,
					signingKeyPublicPart, Constants.NULL_VECTOR, new TPMS_PCR_SELECTION[0]).outPublic.toTpm();
		} catch (RuntimeException runtimeException) {
			LOGGER.error(Constants.LOG_PUBLIC_KEY, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
					String.format("Exception while getting the Public Key from Platform TPM --> %s",
							ExceptionUtils.getStackTrace(runtimeException)));

			throw runtimeException;
		}
	}

}
