package io.kernel.core.idrepo.dto;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonFilter;

import lombok.Data;

@Data
@JsonFilter("responseFilter")
public class ResponseDTO {
	private String entity;
	private JSONObject identity;
}
