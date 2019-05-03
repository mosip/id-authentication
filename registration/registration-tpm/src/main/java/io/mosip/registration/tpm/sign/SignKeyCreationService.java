package io.mosip.registration.tpm.sign;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.tpm.config.TPMLogger;
import io.mosip.registration.tpm.constants.Constants;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
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
 * Class to create the Key for Signing the data using TPM
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class SignKeyCreationService {

	private static final Logger LOGGER = TPMLogger.getLogger(SignKeyCreationService.class);

	/**
	 * Creates the TPM key for signing the data
	 * 
	 * @param tpm
	 *            the instance of {@link Tpm}
	 * @return the TPM key for signing the data
	 */
	public CreatePrimaryResponse getKey(Tpm tpm) {
		LOGGER.info(Constants.LOG_PUBLIC_KEY, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
				"Creating the Key from Platform TPM for Signing data");

		TPMT_PUBLIC signingKeyPublicPart = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
				new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sign,
						TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth),
				new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL),
						new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256), 2048, 65537),
				new TPM2B_PUBLIC_KEY_RSA());

		TPM_HANDLE parentHandleForSigningKey = TPM_HANDLE.from(TPM_RH.ENDORSEMENT);

		TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(Constants.NULL_VECTOR, Constants.NULL_VECTOR);

		LOGGER.info(Constants.LOG_PUBLIC_KEY, Constants.APPLICATION_ID, Constants.APPLICATION_NAME,
				"Completed creating the Key from Platform TPM for Signing data");

		return tpm.CreatePrimary(parentHandleForSigningKey, sens, signingKeyPublicPart, Constants.NULL_VECTOR,
				new TPMS_PCR_SELECTION[0]);
	}

}
