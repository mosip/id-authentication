package io.mosip.registration.processor.print.service.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class PrintQueueDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private byte[] pdfBytes;
	
	private byte[] textBytes;
}
