package io.mosip.resident.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthHistoryResponseDTO {
	private List<AuthTxnDetailsDTO> authHistory;

	private String message;
}
