package io.mosip.authentication.common.service.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;

/**
 * The Class AuthTransactionHelper.
 */
@Component
public class AuthTransactionHelper {
	
	/** The uin encrypt salt repo. */
	@Autowired
	private UinEncryptSaltRepo uinEncryptSaltRepo;

	/** The uin hash salt repo. */
	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	/** The security manager. */
	@Autowired
	private IdAuthSecurityManager securityManager;

	/**
	 * Builds the auth transaction entity.
	 *
	 * @param authTxnBuilder the auth txn builder
	 * @return the autn txn
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public AutnTxn buildAuthTransactionEntity(AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {
		return authTxnBuilder.build(env, uinEncryptSaltRepo, uinHashSaltRepo, securityManager);
	}
	
	public void setAuthTransactionBuilderMetadataToRequest(ObjectWithMetadata requestDTO, AuthTransactionBuilder authTxnBuilder) {
		Map<String, Object> reqMetadata = requestDTO.getMetadata();
		if(reqMetadata == null) {
			reqMetadata = new HashMap<>();
			requestDTO.setMetadata(reqMetadata);
		}
		reqMetadata.put(AuthTransactionBuilder.class.getSimpleName(), authTxnBuilder);
	}

	public void setAuthTransactionMetadataToException(AuthTransactionBuilder authTxnBuilder,
			IdAuthenticationAppException idAuthenticationAppException) throws IdAuthenticationBusinessException {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put(AutnTxn.class.getSimpleName(), buildAuthTransactionEntity(authTxnBuilder));
		idAuthenticationAppException.setMetadata(metadata);
	}
	
	public AuthTransactionBuilder createAndSetAuthTxnBuilderMetadataToRequest(ObjectWithMetadata requestDTO, boolean isInternal, Optional<PartnerDTO> partner)
			throws IdAuthenticationBusinessException {
		AuthTransactionBuilder authTxnBuilder = createAuthTxnBuilder(requestDTO,
				isInternal, partner);
		setAuthTransactionBuilderMetadataToRequest(requestDTO, authTxnBuilder);
		return authTxnBuilder;
	}
	
	public IdAuthenticationAppException createDataValidationException(AuthTransactionBuilder authTxnBuilder, IDDataValidationException e)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		return createAppException(authTxnBuilder, e, IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED);
	}

	public IdAuthenticationAppException createUnableToProcessException(AuthTransactionBuilder authTxnBuilder, IdAuthenticationBusinessException e)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		return createAppException(authTxnBuilder, e, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
	}
	
	private IdAuthenticationAppException createAppException(AuthTransactionBuilder authTxnBuilder, IdAuthenticationBusinessException e, IdAuthenticationErrorConstants erroConst)
			throws IdAuthenticationBusinessException {
		IdAuthenticationAppException authenticationAppException = new IdAuthenticationAppException(erroConst, e);
		setAuthTransactionMetadataToException(authTxnBuilder, authenticationAppException);
		return authenticationAppException;
	}
	
	private AuthTransactionBuilder createAuthTxnBuilder(ObjectWithMetadata requestDTO,
			boolean isInternal, Optional<PartnerDTO> partner) throws IdAuthenticationBusinessException {
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance()
						.withInternal(isInternal)
						.withPartner(partner);
		
		if(requestDTO instanceof AuthRequestDTO) {
			AuthRequestDTO authRequestDTO = (AuthRequestDTO) requestDTO;
			authTransactionBuilder.withAuthRequest(authRequestDTO);
		} else if(requestDTO instanceof OtpRequestDTO) {
			OtpRequestDTO otpRequestDTO = (OtpRequestDTO) requestDTO;
			authTransactionBuilder.withOtpRequest(otpRequestDTO);
		}
		
		return authTransactionBuilder;
	}
}
