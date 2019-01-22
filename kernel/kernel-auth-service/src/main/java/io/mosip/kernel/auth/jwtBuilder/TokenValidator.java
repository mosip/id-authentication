package io.mosip.kernel.auth.jwtBuilder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.entities.MosipUser;
import io.mosip.kernel.auth.entities.MosipUserWithToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenValidator {

    @Autowired
    MosipEnvironment mosipEnvironment;

    private Boolean validateOtpDetails(Claims claims) {
        if (claims.get("isOtpRequired") == null) {
            return true;
        }

        Boolean isOtpVerified = (Boolean) claims.get("isOtpVerified");
        Boolean isOtpRequired = (Boolean) claims.get("isOtpRequired");
        if (isOtpRequired && !isOtpVerified) {
            return false;
        }
        return true;
    }

    private MosipUser buildMosipUser(Claims claims) {
        return new MosipUser(
                claims.getSubject(),
                (String) claims.get("mobile"),
                (String) claims.get("mail"),
                (String) claims.get("role")
        );
    }

    private Claims getClaims(String token) {
        String token_base = mosipEnvironment.getTokenBase();
        String secret = mosipEnvironment.getJwtSecret();

        if (token == null || !token.startsWith(token_base)) {
            throw new RuntimeException("Invalid Token");
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token.substring(token_base.length()))
                    .getBody();

            return claims;
        } catch (Exception e) {
            throw new RuntimeException("Invalid Token");
        }
    }

    public MosipUserWithToken validateForOtpVerification(String token) {
        Claims claims = getClaims(token);
        Boolean isOtpRequired = (Boolean) claims.get("isOtpRequired");
        if (isOtpRequired) {
            MosipUser mosipUser = buildMosipUser(claims);
            return new MosipUserWithToken(mosipUser, token);
        } else {
            throw new RuntimeException("Invalid Token");
        }
    }

    public MosipUserWithToken basicValidate(String token) {
        Claims claims = getClaims(token);
        Boolean isOtpValid = validateOtpDetails(claims);
        if (isOtpValid) {
            MosipUser mosipUser = buildMosipUser(claims);
            return new MosipUserWithToken(mosipUser, token);
        } else {
            throw new RuntimeException("Invalid Token");
        }
    }
}
