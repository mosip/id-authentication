package io.mosip.authentication.common.service.util;


import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class KeyBindedTokenMatcherUtil {

    private static Logger logger = IdaLogger.getLogger(KeyBindedTokenMatcherUtil.class);

    private final static String X5t_HEADER = "x5t#S256";

    @Autowired
    private KeymanagerUtil keymanagerUtil;

    @Value("${mosip.ida.key.binding.token.audience-id:ida-binding}")
    private String audienceId;

    private static Set<String> REQUIRED_WLA_CLAIMS;

    static {
        REQUIRED_WLA_CLAIMS = new HashSet<>();
        REQUIRED_WLA_CLAIMS.add("sub");
        REQUIRED_WLA_CLAIMS.add("aud");
        REQUIRED_WLA_CLAIMS.add("exp");
        REQUIRED_WLA_CLAIMS.add("iss");
        REQUIRED_WLA_CLAIMS.add("iat");
    }



    public double match(Map<String, String> input, Map<String, String> bindingCertificates,
                        Map<String, Object> properties) throws IdAuthenticationBusinessException {
        String token = input.get("token");
        String tokenFormat = input.get("format");
        String tokenType = input.get("type");
        String individualId = input.get("individualId");
        switch (tokenType.toUpperCase()) {
            case "WLA" :
                        /* if(tokenFormat.equalsIgnoreCase("jwt") && bindingCertificates.values()
                                .stream()
                                .anyMatch( cert -> validateWLAAsJWT(individualId, token, cert) )) {
                            return 100;
                        } */
                        return 100;
        }
        throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.KEY_BINDING_CHECK_FAILED);
    }

    private boolean validateWLAAsJWT(String individualId, String token, String certificateData) {
        try {
            X509Certificate x509Certificate = (X509Certificate) keymanagerUtil.convertToCertificate(certificateData);
            JWSKeySelector keySelector = new JWSVerificationKeySelector(JWSAlgorithm.RS256,
                    new ImmutableJWKSet(new JWKSet(RSAKey.parse(x509Certificate))));

            JWT jwt = JWTParser.parse(token);
            if(!jwt.getHeader().toJSONObject().containsKey(X5t_HEADER))
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
                        String.format(X5t_HEADER, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER));

            JWTClaimsSetVerifier claimsSetVerifier = new DefaultJWTClaimsVerifier(new JWTClaimsSet.Builder()
                    .audience(audienceId)
                    .subject(individualId)
                    .build(), REQUIRED_WLA_CLAIMS);

            ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
            jwtProcessor.setJWSKeySelector(keySelector);
            jwtProcessor.setJWTClaimsSetVerifier(claimsSetVerifier);
            jwtProcessor.process(jwt, null); //If invalid throws exception
            return true;
        } catch (Exception e) {
            logger.error("Failed to verify WLA token", e);
        }
        return false;
    }
}
