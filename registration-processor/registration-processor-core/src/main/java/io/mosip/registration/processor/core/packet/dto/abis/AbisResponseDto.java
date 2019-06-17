package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import lombok.Data;

@Data
public class AbisResponseDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String crBy;

	private LocalDateTime crDtimes;

	private LocalDateTime delDtimes;

	private Boolean isDeleted;

	private String langCode;

	private LocalDateTime respDtimes;

	private byte[] respText;
	public byte[] getRespText() {
		return Arrays.copyOf(respText, respText.length);
	}

	public void setRespText(byte[] respText) {
		this.respText = respText!=null?respText:null;
	}

	private String statusCode;

	private String statusComment;

	private String updBy;

	private LocalDateTime updDtimes;

	private String abisRequest;

}
