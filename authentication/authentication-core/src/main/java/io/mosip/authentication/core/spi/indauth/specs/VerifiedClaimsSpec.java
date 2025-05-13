package io.mosip.authentication.core.spi.indauth.specs;

import java.util.List;
import java.util.Map;

import io.mosip.authentication.core.indauth.dto.VerifiedClaimsAttributes;

/**
 * This interface is used to match Verified Claims metadata of individual identity.
 * 
 * @author Mahammed Taheer
 */

public interface VerifiedClaimsSpec<S, U> {    

    S matchVerifiedClaimsMetadata(U trustFrameworks, Map<String, Object> respVerificationMap);

    U getVerifiedClaimsMetadata(List<VerifiedClaimsAttributes> verifiedClaimsAttributes);
}
