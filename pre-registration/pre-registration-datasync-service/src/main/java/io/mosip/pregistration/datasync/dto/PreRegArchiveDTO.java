package io.mosip.pregistration.datasync.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreRegArchiveDTO {
	private byte[] zipBytes;
	private String fileName;
}
