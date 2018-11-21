package io.kernel.core.idrepo.dto;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;

@Data
@JsonFilter("responseFilter")
public class ResponseDTO {
	private String entity;
	private Object identity;
}
