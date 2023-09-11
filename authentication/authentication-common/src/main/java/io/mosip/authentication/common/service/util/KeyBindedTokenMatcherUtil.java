package io.mosip.authentication.common.service.util;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CERT_TP_AF_SEPERATOR;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class KeyBindedTokenMatcherUtil {

    private static Logger mosipLogger = IdaLogger.getLogger(KeyBindedTokenMatcherUtil.class);

    private final static String X5t_HEADER = "x5t#S256";

    private final static String TOKEN = "token";

    private final static String FORMAT = "format";

    private final static String TYPE = "type";

    private final static String INDIVIDUAL_ID = "individualId";

    private final static String JWT_CONST = "jwt";

    @Autowired
    private KeymanagerUtil keymanagerUtil;

    @Value("${mosip.ida.key.binding.token.audience-id:ida-binding}")
    private String audienceId;

    @Value("${mosip.ida.key.binding.token.iat.adjustment.seconds:30}")
    private int iatAdjSeconds;

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
        
        return validateBindedToken(input, bindingCertificates) ? 100 : 0;
    }

    private boolean validateBindedToken(Map<String, String> input, Map<String, String> bindingCertificates) 
                throws IdAuthenticationBusinessException {
        String token = input.get(TOKEN);
        String tokenFormat = input.get(FORMAT);
        String tokenType = input.get(TYPE);
        String individualId = input.get(INDIVIDUAL_ID);
        try {
            SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);
            JWSHeader jwsHeader = signedJWT.getHeader();
            Base64URL thumbprintObj = jwsHeader.getX509CertSHA256Thumbprint();
            if (Objects.isNull(thumbprintObj)) {
                mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "",
						String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
                        X5t_HEADER));
				
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
                    String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), X5t_HEADER));
            }
            
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            Date issuedDateTime = jwtClaimsSet.getIssueTime();
            if (!isIatWithinAllowedTime(issuedDateTime)) {
                mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "",
						IdAuthenticationErrorConstants.BINDED_TOKEN_EXPIRED.getErrorMessage());
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BINDED_TOKEN_EXPIRED.getErrorCode(),
                        IdAuthenticationErrorConstants.BINDED_TOKEN_EXPIRED.getErrorMessage());
            }

            byte[] thumbprintBytes = jwsHeader.getX509CertSHA256Thumbprint().decode();
            String thumbprint = Hex.encodeHexString(thumbprintBytes).toUpperCase();
            String certificateData = bindingCertificates.get((thumbprint + CERT_TP_AF_SEPERATOR + tokenType).toUpperCase());
            if (Objects.isNull(certificateData)) {
                mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "",
						String.format(IdAuthenticationErrorConstants.BINDED_KEY_NOT_FOUND.getErrorMessage(),
                        thumbprint, tokenType));
                throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BINDED_KEY_NOT_FOUND.getErrorCode(),
                        String.format(IdAuthenticationErrorConstants.BINDED_KEY_NOT_FOUND.getErrorMessage(), thumbprint, tokenType));
            }

            if (tokenFormat.equalsIgnoreCase(JWT_CONST))
                return verifyWLAAsJWT(individualId, signedJWT, certificateData);
        } catch (ParseException e) {
            mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "",
                        "Failed to verify WLA token", e);
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    private boolean verifyWLAAsJWT(String individualId, JWT jwt, String certificateData) 
        throws IdAuthenticationBusinessException {
        try {
            X509Certificate x509Certificate = (X509Certificate) keymanagerUtil.convertToCertificate(certificateData);
            JWSKeySelector keySelector = new JWSVerificationKeySelector(JWSAlgorithm.RS256,
                    new ImmutableJWKSet(new JWKSet(RSAKey.parse(x509Certificate))));

            JWTClaimsSetVerifier claimsSetVerifier = new DefaultJWTClaimsVerifier(new JWTClaimsSet.Builder()
                    .audience(audienceId)
                    .subject(individualId)
                    .build(), REQUIRED_WLA_CLAIMS);

            ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
            jwtProcessor.setJWSKeySelector(keySelector);
            jwtProcessor.setJWTClaimsSetVerifier(claimsSetVerifier);
            jwtProcessor.process(jwt, null); //If invalid throws exception
            return true;
        } catch (BadJOSEException | JOSEException e) {
            mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "",
                        "Failed to verify WLA token", e);
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ERROR_TOKEN_VERIFICATION.getErrorCode(),
                        String.format(IdAuthenticationErrorConstants.ERROR_TOKEN_VERIFICATION.getErrorMessage(), e.getMessage()));
        }
    }

    private boolean isIatWithinAllowedTime(Date issuedDateTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        
        LocalDateTime issuedLDT = DateUtils.parseDateToLocalDateTime(issuedDateTime);
		long diffSeconds = ChronoUnit.SECONDS.between(issuedLDT, currentTime);
        
		if (issuedDateTime != null && diffSeconds >= 0 && diffSeconds <= iatAdjSeconds) {
			return true;
		}
		return false;
    }
}
