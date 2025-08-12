package io.mosip.authentication.service.kyc.specs;


import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUES;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.mosip.authentication.core.indauth.dto.VerifiedClaimsAttributes;
import io.mosip.authentication.core.spi.indauth.specs.VerifiedClaimsSpec;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.authentication.core.logger.IdaLogger;

/**
 * This class is used to match the trust framework used for Verified Claims data
 * 
 * @author Mahammed Taheer
 */

public class TrustFrameworkSpec implements VerifiedClaimsSpec<List<String>, List<String>> {

    private Logger mosipLogger = IdaLogger.getLogger(TrustFrameworkSpec.class);	

    private List<String> reqTrustFrameworks;

    public TrustFrameworkSpec(Object trustFramework) {
        if (trustFramework == null) {
            this.reqTrustFrameworks = null;
        }
        else {
            @SuppressWarnings("unchecked")
            Map<String, Object> trustFrameworkMap = (Map<String, Object>) trustFramework;
            this.reqTrustFrameworks = getRequestTrustFrameworks(trustFrameworkMap);
        }
    }

    @Override
    public List<String> matchVerifiedClaimsMetadata(List<String> trustFrameworks, Map<String, Object> trustFrameworkMap) {
        mosipLogger.info(SESSION_ID, this.getClass().getSimpleName(), 
                        "matchVerifiedClaimsMetadata", "Request TrustFrameworks: " + this.reqTrustFrameworks);

        if (this.reqTrustFrameworks == null) {
            return trustFrameworks;
        }
        return trustFrameworks.stream()
                .filter(tf -> this.reqTrustFrameworks.contains(tf))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
	private List<String> getRequestTrustFrameworks(Map<String, Object> trustFrameworkMap) {
		if (trustFrameworkMap.containsKey(VERIFICATION_VALUE)) {
			return Collections.singletonList((String) trustFrameworkMap.get(VERIFICATION_VALUE));
		}
		return new ArrayList<>((List<String>) trustFrameworkMap.get(VERIFICATION_VALUES));
	}

    @Override
    public List<String> getVerifiedClaimsMetadata(List<VerifiedClaimsAttributes> verifiedClaimsAttributes) {
        // This method is not used for TrustFrameworkSpec
        return null;
    }
}
