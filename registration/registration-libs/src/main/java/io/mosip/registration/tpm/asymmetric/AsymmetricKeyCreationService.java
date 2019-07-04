package io.mosip.registration.tpm.asymmetric;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPM2B_PUBLIC_KEY_RSA;
import tss.tpm.TPMA_OBJECT;
import tss.tpm.TPMS_NULL_ASYM_SCHEME;
import tss.tpm.TPMS_PCR_SELECTION;
import tss.tpm.TPMS_RSA_PARMS;
import tss.tpm.TPMS_SENSITIVE_CREATE;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPMT_SYM_DEF_OBJECT;
import tss.tpm.TPM_ALG_ID;
import tss.tpm.TPM_HANDLE;
import tss.tpm.TPM_RH;

/**
 * The class to create the asymmetric key for encryption and decryption
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class AsymmetricKeyCreationService {

	/**
	 * Creates the asymmetric key
	 * 
	 * @param tpm
	 *            the instance of {@link Tpm}
	 * @return the {@link TPM_HANDLE} of the asymmetric key
	 */
	public TPM_HANDLE createPersistentKey(Tpm tpm) {
		try {
			// This policy is a "standard" policy that is used with vendor-provided
			// EKs
			byte[] standardEKPolicy = new byte[] { (byte) 0x83, 0x71, (byte) 0x97, 0x67, 0x44, (byte) 0x84, (byte) 0xb3,
					(byte) 0xf8, 0x1a, (byte) 0x90, (byte) 0xcc, (byte) 0x8d, 0x46, (byte) 0xa5, (byte) 0xd7, 0x24,
					(byte) 0xfd, 0x52, (byte) 0xd7, 0x6e, 0x06, 0x52, 0x0b, 0x64, (byte) 0xf2, (byte) 0xa1, (byte) 0xda,
					0x1b, 0x33, 0x14, 0x69, (byte) 0xaa };

			// Create a TPMT Public object for RSA Key
			TPMT_PUBLIC tpmtPublic = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
					new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.decrypt,
							TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth),
					standardEKPolicy, new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.NULL, 128, TPM_ALG_ID.NULL),
							new TPMS_NULL_ASYM_SCHEME(), 2048, 65537),
					new TPM2B_PUBLIC_KEY_RSA());

			// Create TPMS Sensitive Create object
			TPMS_SENSITIVE_CREATE sens = new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]);

			// Create Hierarchy for Storing Key
			TPM_HANDLE hierarchy = TPM_HANDLE.from(TPM_RH.ENDORSEMENT);

			// Create the Primary Key in TPM under primary handle
			CreatePrimaryResponse rsaSrk = tpm.CreatePrimary(hierarchy, sens, tpmtPublic, new byte[0],
					new TPMS_PCR_SELECTION[0]);

			return rsaSrk.handle;
		} catch (RuntimeException runtimeException) {
			throw new BaseUncheckedException("TPM-AKC-001", runtimeException.getMessage(), runtimeException);
		}
	}

}
