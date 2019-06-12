package io.mosip.kernel.auth.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthErrorCode;
import io.mosip.kernel.auth.dto.MosipUser;
import io.mosip.kernel.auth.dto.MosipUserDto;
import io.mosip.kernel.auth.dto.MosipUserTokenDto;
import io.mosip.kernel.auth.dto.MosipUserToken;
import io.mosip.kernel.auth.exception.AuthManagerException;
import io.mosip.kernel.auth.service.TokenService;

@Component
public class TokenValidator {

	@Autowired
	MosipEnvironment mosipEnvironment;

	@Autowired
	TokenService customTokenServices;

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
		return new MosipUser(claims.getSubject(), (String) claims.get("mobile"), (String) claims.get("mail"),
				(String) claims.get("role"));
	}

	private Claims getClaims(String token) throws Exception {
		String token_base = mosipEnvironment.getTokenBase();
		String secret = mosipEnvironment.getJwtSecret();
		Claims claims = null;

		if (token == null || !token.startsWith(token_base)) {
			throw new NonceExpiredException("Invalid Token");
		}
		try {
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token.substring(token_base.length())).getBody();

		} catch (SignatureException e) {
			throw new AuthManagerException(AuthErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage());
		} catch (JwtException e) {
			if( e instanceof ExpiredJwtException)
			{
				System.out.println("Token expired message "+ e.getMessage() + " Token "+token);
				throw new AuthManagerException(AuthErrorCode.TOKEN_EXPIRED.getErrorCode(), AuthErrorCode.TOKEN_EXPIRED.getErrorMessage());
			}
			else
			{
				throw new AuthManagerException(AuthErrorCode.UNAUTHORIZED.getErrorCode(), e.getMessage());
			}
			
		}
		return claims;
	}

	public MosipUserToken validateForOtpVerification(String token) throws Exception {
		Claims claims = getClaims(token);
		Boolean isOtpRequired = (Boolean) claims.get("isOtpRequired");
		if (isOtpRequired) {
			MosipUser mosipUser = buildMosipUser(claims);
			return new MosipUserToken(mosipUser, token);
		} else {
			throw new AuthManagerException(AuthErrorCode.INVALID_TOKEN.getErrorCode(),AuthErrorCode.INVALID_TOKEN.getErrorMessage());
		}
	}

	public MosipUserToken basicValidate(String token) throws Exception {
		Claims claims = getClaims(token);
		Boolean isOtpValid = validateOtpDetails(claims);
		if (isOtpValid) {
			MosipUser mosipUser = buildMosipUser(claims);
			return new MosipUserToken(mosipUser, token);
		} else {
			throw new AuthManagerException(AuthErrorCode.INVALID_TOKEN.getErrorCode(),AuthErrorCode.INVALID_TOKEN.getErrorMessage());
		}
	}

	public MosipUserTokenDto validateToken(String token) throws Exception {
		Claims claims = getClaims(token);
		MosipUserDto mosipUserDto = buildDto(claims);
		return new MosipUserTokenDto(mosipUserDto, token, null, 0, null, null);
	}

	private MosipUserDto buildDto(Claims claims) {
		MosipUserDto mosipUserDto = new MosipUserDto();
		mosipUserDto.setUserId(claims.getSubject());
		mosipUserDto.setName((String) claims.get("name"));
		mosipUserDto.setRole((String) claims.get("role"));
		mosipUserDto.setMail((String) claims.get("mail"));
		mosipUserDto.setMobile((String) claims.get("mobile"));
		mosipUserDto.setRId((String) claims.get("rId"));
		return mosipUserDto;
	}

	public MosipUserTokenDto validateOTP(String otp) throws Exception {
		Claims claims = getClaims(otp);
		Boolean isOtpRequired = (Boolean) claims.get("isOtpRequired");
		if (isOtpRequired) {
			MosipUserDto mosipUserDto = buildDto(claims);
			return new MosipUserTokenDto(mosipUserDto, otp, null, 0, null, null);
		} else {
			throw new AuthManagerException(AuthErrorCode.UNAUTHORIZED.getErrorCode(), AuthErrorCode.UNAUTHORIZED.getErrorMessage());
		}
	}

	public boolean validateExpiry(String token) throws Exception {
		Claims claims = getClaims(token);
		if (claims != null) {
			Integer expTime = (Integer) claims.get("exp");
			long currentTime = new Date().getTime();
			long exp = expTime.longValue() * 1000;
			if (expTime != 0 && currentTime < exp) {
				return true;
			}
		}
		return false;
	}
}
