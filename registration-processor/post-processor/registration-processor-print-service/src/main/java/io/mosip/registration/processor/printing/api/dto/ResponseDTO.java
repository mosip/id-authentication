package io.mosip.registration.processor.printing.api.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] file;
}
