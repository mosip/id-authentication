package io.mosip.kernel.auth.jwtBuilder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserWithTokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
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

    private MosipUserDto buildMosipUser(Claims claims) {
        MosipUserDto mosipUserDto = new MosipUserDto();
        mosipUserDto.setUserName(claims.getSubject());
        mosipUserDto.setMobile((String) claims.get("mobile"));
        mosipUserDto.setMail((String) claims.get("mail"));
        mosipUserDto.setRole((String) claims.get("role"));

        return mosipUserDto;
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

    public MosipUserWithTokenDto validateForOtpVerification(String token) {
        Claims claims = getClaims(token);
        Boolean isOtpRequired = (Boolean) claims.get("isOtpRequired");
        if (isOtpRequired) {
            MosipUserDto mosipUserDto = buildMosipUser(claims);
            return new MosipUserWithTokenDto(mosipUserDto, token);
        } else {
            throw new RuntimeException("Invalid Token");
        }
    }

    public MosipUserWithTokenDto basicValidate(String token) {
        Claims claims = getClaims(token);
        Boolean isOtpValid = validateOtpDetails(claims);
        if (isOtpValid) {
            MosipUserDto mosipUserDto = buildMosipUser(claims);
            return new MosipUserWithTokenDto(mosipUserDto, token);
        } else {
            throw new RuntimeException("Invalid Token");
        }
    }
}
