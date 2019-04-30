package io.mosip.kernel.auth.demo.service.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDemoServiceRequestDto {

	@NotBlank
    private String applicationId;



}