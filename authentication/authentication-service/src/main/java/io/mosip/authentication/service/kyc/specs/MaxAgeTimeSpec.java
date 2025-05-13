package io.mosip.authentication.service.kyc.specs;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MATCHED_TRUST_FRAMEWORKS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRUST_FRAMEWORK;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUES;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import io.mosip.authentication.core.indauth.dto.VerifiedClaimsAttributes;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.specs.VerifiedClaimsSpec;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class is used to match the max age time used for Verified Claims data
 * 
 * @author Mahammed Taheer
 */

public class MaxAgeTimeSpec implements VerifiedClaimsSpec<LocalDateTime, Map<String, LocalDateTime>> {

    private Logger mosipLogger = IdaLogger.getLogger(MaxAgeTimeSpec.class);

    private long maxAgeTime;

    private Object trustFramework;

    public MaxAgeTimeSpec(long maxAgeTime, Object trustFramework) {
        this.maxAgeTime = maxAgeTime;
        this.trustFramework = trustFramework;
    }

    @Override
    public LocalDateTime matchVerifiedClaimsMetadata(Map<String, LocalDateTime> trustFrameworkMap, Map<String, Object> respVerificationMap) {

        mosipLogger.info(SESSION_ID, this.getClass().getSimpleName(), 
                        "matchVerifiedClaimsMetadata", "Request MaxAgeTime: " + this.maxAgeTime);
        
        List<LocalDateTime> dateTimeList = new ArrayList<>(trustFrameworkMap.values());
        if (dateTimeList.isEmpty()) {
            return null;
        }

        LocalDateTime currentTime = LocalDateTime.now();

        List<Entry<String, LocalDateTime>> entries = trustFrameworkMap.entrySet().stream()
            .filter(e -> java.time.Duration.between(e.getValue(), currentTime).getSeconds() <= maxAgeTime)
            .collect(Collectors.toList());

        respVerificationMap.put(MATCHED_TRUST_FRAMEWORKS, entries);
        if (entries.isEmpty()) {
            return null;
        }

        Entry<String, LocalDateTime> matchedEntry = entries.stream()
            .filter(entry -> entry.getKey().equals(respVerificationMap.get(TRUST_FRAMEWORK)))
            .findFirst()
            .orElse(null);

        if (matchedEntry != null) {
            return matchedEntry.getValue();
        }
        // If no matched entry, return the first entry
        Entry<String, LocalDateTime> entry = entries.get(0);
        respVerificationMap.put(TRUST_FRAMEWORK, entry.getKey());
        return entry.getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, LocalDateTime> getVerifiedClaimsMetadata(List<VerifiedClaimsAttributes> verifiedClaimsAttributes) {
        if (trustFramework == null) {
            return verifiedClaimsAttributes.stream()
                                           .collect(Collectors.toMap(
                                               attr -> attr.getTrustFramework(),
                                               attr -> attr.getTime()
                                           ));
        }
        
        Map<String, Object> trustFrameworkMap = (Map<String, Object>) trustFramework;
        List<String> trustFrameworks = trustFrameworkMap.containsKey(VERIFICATION_VALUE) ?
            Collections.singletonList((String) trustFrameworkMap.get(VERIFICATION_VALUE)) :
            new ArrayList<>((List<String>) trustFrameworkMap.get(VERIFICATION_VALUES));

        return trustFrameworks.stream()
                .flatMap(tf -> verifiedClaimsAttributes.stream()
                                                       .filter(attr -> tf.equals(attr.getTrustFramework()))
                                                       .map(attr -> attr))
                .collect(Collectors.toMap(attr -> attr.getTrustFramework(), attr -> attr.getTime()));
    }   
}
