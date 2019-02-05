package io.mosip.kernel.auth.jwtBuilder;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenGenerator {

    @Autowired
    MosipEnvironment mosipEnvironment;

    private Claims getBasicClaims(MosipUserDto mosipUserDto) {
        Claims claims = Jwts.claims().setSubject(mosipUserDto.getUserName());
        claims.put("mobile", mosipUserDto.getMobile());
        claims.put("mail", mosipUserDto.getMail());
        claims.put("role", mosipUserDto.getRole());

        return claims;
    }

    private String buildToken(Claims claims) {
        String secret = mosipEnvironment.getJwtSecret();
        String token_base = mosipEnvironment.getTokenBase();
        Long token_expiry = mosipEnvironment.getTokenExpiry();

        long currentTimeInMs = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeInMs);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .signWith(SignatureAlgorithm.HS512, secret);


        if (token_expiry >= 0) {
            long expTimeInMs = currentTimeInMs + token_expiry;
            builder.setExpiration(new Date(expTimeInMs));
        }

        return token_base.concat(builder.compact());
    }

    public String basicGenerate(MosipUserDto mosipUserDto) {
        Claims claims = getBasicClaims(mosipUserDto);
        return buildToken(claims);
    }

    public String generateForOtp(MosipUserDto mosipUserDto, Boolean isOtpVerifiedYet) {
        Claims claims = getBasicClaims(mosipUserDto);
        claims.put("isOtpRequired", true);
        claims.put("isOtpVerified", isOtpVerifiedYet);
        return buildToken(claims);
    }

}
