package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;


@Data
public class AbisRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	
	private String abisAppCode;

	private String bioRefId;

	private String crBy;

	private LocalDateTime crDtimes;

	private LocalDateTime delDtimes;

	private Boolean isDeleted;

	private String langCode;

	private String refRegtrnId;

	private String reqBatchId;

	private byte[] reqText;

	private LocalDateTime requestDtimes;

	private String requestType;

	private String statusCode;

	private String statusComment;

	private String updBy;

	private LocalDateTime updDtimes;

}
