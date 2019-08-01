package io.mosip.kernel.auth.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class KeycloakRequestDto {

	@JsonInclude(value=Include.NON_NULL)
	private String username;

	@JsonInclude(value=Include.NON_NULL)
	private String firstName;

	@JsonInclude(value=Include.NON_NULL)
	private String email;

	@JsonInclude(value=Include.NON_EMPTY)
	private Map<String,List<Object>> attributes;
	
	@JsonInclude(value=Include.NON_EMPTY)
	private Map<String,Object> credentials;
	
	@JsonInclude(value=Include.NON_EMPTY)
	private List<String> realmRoles;
	
	private boolean enabled;
}
