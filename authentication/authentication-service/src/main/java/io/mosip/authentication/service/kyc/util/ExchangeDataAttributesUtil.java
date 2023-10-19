package io.mosip.authentication.service.kyc.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.OIDCClientData;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.repository.OIDCClientDataRepository;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Utility class to filter the consented attribute and policy allowed attributes.
 *
 * @author Mahammed Taheer
 */

@Component
public class ExchangeDataAttributesUtil {

    /** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(ExchangeDataAttributesUtil.class);

    @Value("${ida.idp.consented.individual_id.attribute.name:individual_id}")
	private String consentedIndividualIdAttributeName;

    @Autowired
	private IdInfoHelper idInfoHelper;

    @Autowired
	private OIDCClientDataRepository oidcClientDataRepo; 

    public void mapConsentedAttributesToIdSchemaAttributes(List<String> consentAttributes, Set<String> filterAttributes, 
			List<String> policyAllowedKycAttribs) throws IdAuthenticationBusinessException {

		if(consentAttributes != null && !consentAttributes.isEmpty()) {
			for (String attrib : consentAttributes) {
				Collection<? extends String> idSchemaAttribute = idInfoHelper.getIdentityAttributesForIdName(attrib);
				filterAttributes.addAll(idSchemaAttribute);
			}
			// removing individual id from consent if the claim is not allowed in policy.
			if (!policyAllowedKycAttribs.contains(consentedIndividualIdAttributeName)) {
				consentAttributes.remove(consentedIndividualIdAttributeName);
			}
		}
	} 

	public Set<String> filterByPolicyAllowedAttributes(Set<String> filterAttributes, List<String> policyAllowedKycAttribs) {
		return policyAllowedKycAttribs.stream()
							.filter(attribute -> filterAttributes.contains(attribute))
							.collect(Collectors.toSet());
	}

	public String getKycExchangeResponseTime(BaseRequestDTO authRequestDTO) {
		String dateTimePattern = EnvUtil.getDateTimePattern();
		return IdaRequestResponsConsumerUtil.getResponseTime(authRequestDTO.getRequestTime(), dateTimePattern);
	}

	public List<String> filterAllowedUserClaims(String oidcClientId, List<String> consentAttributes) {
		mosipLogger.info(IdAuthCommonConstants.IDA, this.getClass().getSimpleName(), "filterAllowedUserClaims", 
					"Checking for OIDC client allowed userclaims");
		Optional<OIDCClientData> oidcClientData = oidcClientDataRepo.findByClientId(oidcClientId);
		if(oidcClientData.isEmpty()) {
			return List.of();
		}

		List<String> oidcClientAllowedUserClaims = List.of(oidcClientData.get().getUserClaims())
													   .stream()
													   .map(String::toLowerCase)
													   .collect(Collectors.toList());
		if (consentAttributes.isEmpty()) {
			return oidcClientAllowedUserClaims;
		}

		return consentAttributes.stream()
							    .filter(claim -> oidcClientAllowedUserClaims.contains(claim.toLowerCase()))
								.collect(Collectors.toList());

	}
    
}
