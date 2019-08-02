package io.mosip.kernel.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sabbu Uday Kumar
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MosipUserDto {
	private String userId;
	private String mobile;
	private String mail;
	private String langCode;
	private String userPassword;
	private String name;
	private String role;
	private String rId;
	private String token;
}
