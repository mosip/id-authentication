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
public class Roles {
	@JsonInclude(value=Include.NON_NULL)
	private String id;

	@JsonInclude(value=Include.NON_NULL)
	private String name;
}
