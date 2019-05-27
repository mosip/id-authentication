package io.mosip.dbdto;

import java.io.Serializable;

import lombok.Data;

@Data
public class PrintQueueDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private byte[] pdfBytes;
	
	private byte[] textBytes;
	
	private String uin;

	public byte[] getPdfBytes() {
		return pdfBytes;
	}

	public void setPdfBytes(byte[] pdfBytes) {
		this.pdfBytes = pdfBytes;
	}

	public byte[] getTextBytes() {
		return textBytes;
	}

	public void setTextBytes(byte[] textBytes) {
		this.textBytes = textBytes;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}
	
	
}
