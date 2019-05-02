package io.mosip.registration.tpm.sign.impl;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.tpm.constants.Constants;
import io.mosip.registration.tpm.sign.SignatureService;
import io.mosip.registration.tpm.util.TPMFileUtils;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPM2B_PUBLIC_KEY_RSA;
import tss.tpm.TPMA_OBJECT;
import tss.tpm.TPMS_NULL_SIG_SCHEME;
import tss.tpm.TPMS_PCR_SELECTION;
import tss.tpm.TPMS_RSA_PARMS;
import tss.tpm.TPMS_SENSITIVE_CREATE;
import tss.tpm.TPMS_SIGNATURE_RSASSA;
import tss.tpm.TPMS_SIG_SCHEME_RSASSA;
import tss.tpm.TPMT_HA;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMT_SYM_DEF_OBJECT;
import tss.tpm.TPMT_TK_HASHCHECK;
import tss.tpm.TPMU_SIGNATURE;
import tss.tpm.TPM_ALG_ID;
import tss.tpm.TPM_HANDLE;
import tss.tpm.TPM_RH;

/**
 * Class to Sign the Data using TPM.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class SignatureServiceImpl implements SignatureService {

	private static final Logger LOGGER = AppConfig.getLogger(SignatureServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.tpm.sign.SignatureService#signData(tss.Tpm,
	 * byte[])
	 */
	@Override
	public void signData(Tpm tpm, byte[] dataToSign) {
		try {
			LOGGER.info(LoggerConstants.TPM_SIGN_DATA, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Signing data using TPM");

			TPMT_PUBLIC signingKeyPublicPart = new TPMT_PUBLIC(TPM_ALG_ID.SHA1,
					new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sign,
							TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth),
					new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL, 0, TPM_ALG_ID.NULL),
							new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256), 2048, 65537),
					new TPM2B_PUBLIC_KEY_RSA());

			TPM_HANDLE parentHandleForSigningKey = TPM_HANDLE.from(TPM_RH.ENDORSEMENT);

			TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(Constants.NULL_VECTOR, Constants.NULL_VECTOR);

			CreatePrimaryResponse signingKey = tpm.CreatePrimary(parentHandleForSigningKey, sens, signingKeyPublicPart,
					Constants.NULL_VECTOR, new TPMS_PCR_SELECTION[0]);

			TPMU_SIGNATURE signedData = tpm.Sign(signingKey.handle,
					TPMT_HA.fromHashOf(TPM_ALG_ID.SHA256, dataToSign).digest, new TPMS_NULL_SIG_SCHEME(),
					TPMT_TK_HASHCHECK.nullTicket());

			TPMFileUtils.writeToFile(signingKey.outPublic.toTpm(), Constants.PUBLIC_PART_FILE_NAME);

			TPMFileUtils.writeToFile(((TPM2B_PUBLIC_KEY_RSA) signingKey.outPublic.unique).buffer,
					Constants.PUBLIC_KEY_FILE_NAME);

			TPMFileUtils.writeToFile(((TPMS_SIGNATURE_RSASSA) signedData).sig, Constants.SIGNED_DATA_FILE_NAME);

			tpm.FlushContext(signingKey.handle);

			LOGGER.info(LoggerConstants.TPM_SIGN_DATA, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, "Completed Signing data using TPM");
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException("TPM-SSI-001", runtimeException.getMessage(), runtimeException);
		}
	}

}
