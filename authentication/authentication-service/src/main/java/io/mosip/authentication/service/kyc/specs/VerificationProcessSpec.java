package io.mosip.authentication.service.kyc.specs;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MATCHED_TRUST_FRAMEWORKS;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRUST_FRAMEWORK;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFICATION_VALUES;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.VERIFIED_ATTRIB_TIME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;
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
 * This class is used to match the verification process used for Verified Claims data
 * 
 * @author Mahammed Taheer
 */

public class VerificationProcessSpec implements VerifiedClaimsSpec<String, Map<String, VerifiedClaimsAttributes>> {

    private Logger mosipLogger = IdaLogger.getLogger(VerificationProcessSpec.class);

    private String reqVerificationProcess;

    private Object trustFramework;

    public VerificationProcessSpec(String reqVerificationProcess, Object trustFramework) {
        this.reqVerificationProcess = reqVerificationProcess;
        this.trustFramework = trustFramework;
    }

    @Override
    public String matchVerifiedClaimsMetadata(Map<String, VerifiedClaimsAttributes> trustFrameworkMap, Map<String, Object> respVerificationMap) {

        mosipLogger.info(SESSION_ID, this.getClass().getSimpleName(), 
                        "matchVerifiedClaimsMetadata", "Request VerificationProcess: " + this.reqVerificationProcess);

        List<VerifiedClaimsAttributes> dbVerifiedClaimsAttribsList = new ArrayList<>(trustFrameworkMap.values());

        // If the verification process is provided, but no verification process is found in the database, return null
        if (dbVerifiedClaimsAttribsList.isEmpty() && this.reqVerificationProcess != null) {
            return null;
        }

        // If the verification process is provided as null, but verification process is found in the database, return matched with trust framework & time
        if (this.reqVerificationProcess == null) {
            String matchedTrustFramework = (String) respVerificationMap.get(TRUST_FRAMEWORK);
            LocalDateTime matchedTime = (LocalDateTime) respVerificationMap.get(VERIFIED_ATTRIB_TIME);

            VerifiedClaimsAttributes matchedVerifiedClaimsAttributes = dbVerifiedClaimsAttribsList.stream()
                                                                                       .filter(v -> v.getTrustFramework().equals(matchedTrustFramework) &&
                                                                                                    v.getTime().equals(matchedTime))
                                                                                       .findFirst()
                                                                                       .orElse(null);
            if (matchedVerifiedClaimsAttributes != null && matchedVerifiedClaimsAttributes.getVerificationProcess() != null) {
                return matchedVerifiedClaimsAttributes.getVerificationProcess();
            }
            // If the verification process is provided as null and verification process is found in the database, 
            // return from list (dbVerifiedClaimsAttribsList) matched trust framework & time
            List<VerifiedClaimsAttributes> matchedVerificationProcessList = dbVerifiedClaimsAttribsList.stream()
                                                                                             .filter(v -> v.getVerificationProcess() != null)
                                                                                             .collect(Collectors.toList());
            return getMatchedVerificationProcess(matchedVerificationProcessList, respVerificationMap);
        }        
        // If the verification process is provided, and verification process is found in the database
        List<VerifiedClaimsAttributes> matchedVerificationProcessList = dbVerifiedClaimsAttribsList.stream()
                                                                                             .filter(v -> v.getVerificationProcess().equals(this.reqVerificationProcess))
                                                                                             .collect(Collectors.toList());

        return getMatchedVerificationProcess(matchedVerificationProcessList, respVerificationMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, VerifiedClaimsAttributes> getVerifiedClaimsMetadata(List<VerifiedClaimsAttributes> verifiedClaimsAttributes) {
        if (trustFramework == null) {
            return verifiedClaimsAttributes.stream()
                                            .map(attr -> attr)
                                            .collect(Collectors.toMap(
                                                attr -> attr.getVerificationProcess(),
                                                attr -> attr
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
                .collect(Collectors.toMap(attr -> attr.getVerificationProcess(), attr -> attr));
    }

    @SuppressWarnings("unchecked")
    private String getMatchedVerificationProcess(List<VerifiedClaimsAttributes> matchedVerificationProcessList, Map<String, Object> respVerificationMap) {  
        if (matchedVerificationProcessList.isEmpty()) {
            return null;
        }

        List<Entry<String, LocalDateTime>> matchedTrustFrameworks = (List<Entry<String, LocalDateTime>>) respVerificationMap.get(MATCHED_TRUST_FRAMEWORKS);
        
        VerifiedClaimsAttributes matchedVerificationProcess = matchedVerificationProcessList.stream()
            .filter(verificationProcess -> matchedTrustFrameworks.stream()
                .anyMatch(trustFramework -> 
                    verificationProcess.getTrustFramework().equals(trustFramework.getKey()) &&
                    verificationProcess.getTime().equals(trustFramework.getValue())
                ))
            .findFirst()
            .orElse(null);
        
        if (matchedVerificationProcess == null) {
            return null;
        }
        respVerificationMap.put(TRUST_FRAMEWORK, matchedVerificationProcess.getTrustFramework());
        respVerificationMap.put(VERIFIED_ATTRIB_TIME, matchedVerificationProcess.getTime());
        return matchedVerificationProcess.getVerificationProcess();
         /*  // Scenario 1: If the verification process is not found in the database, return null
        if (matchedVerificationProcessList.isEmpty()) {
            return null;
        }

        List<Entry<String, LocalDateTime>> matchedTrustFrameworks = (List<Entry<String, LocalDateTime>>) respVerificationMap.get(MATCHED_TRUST_FRAMEWORKS);
        // Scenario 2: If the verification process is found in the database, return matched verification process
        VerifiedClaimsAttributes matchedVerificationProcess = matchedVerificationProcessList.stream()
                .filter(verificationProcess -> matchedTrustFrameworks.stream()
                    .anyMatch(trustFramework -> 
                        verificationProcess.getTrustFramework().equals(trustFramework.getKey()) &&
                        verificationProcess.getTime().equals(trustFramework.getValue())
                    ))
                .findFirst()
                .orElse(null);
            
        if (matchedVerificationProcess == null) {
            return null;
        }
        respVerificationMap.put(TRUST_FRAMEWORK, matchedVerificationProcess.getTrustFramework());
        respVerificationMap.put(VERIFIED_ATTRIB_TIME, matchedVerificationProcess.getTime());
        return matchedVerificationProcess.getVerificationProcess(); */
    }
    
}