package io.mosip.resident.service;

import io.mosip.resident.dto.AuthLockRequestDto;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.ResidentReprintRequestDto;
import io.mosip.resident.dto.ResponseDTO;

public interface ResidentService {

	public ResponseDTO getRidStatus(RequestDTO dto);

	public byte[] reqEuin(EuinRequestDTO euinRequestDTO);

	public ResponseDTO reqPrintUin(ResidentReprintRequestDto dto);

	public ResponseDTO reqUin(RequestDTO dto);

	public ResponseDTO reqRid(RequestDTO dto);

	public ResponseDTO reqUpdateUin(RequestDTO dto);

	public ResponseDTO generatVid(RequestDTO dto);

	public ResponseDTO revokeVid(RequestDTO dto);

	public ResponseDTO reqAauthLock(AuthLockRequestDto dto);

	public ResponseDTO reqAuthUnlock(RequestDTO dto);

	public ResponseDTO reqAuthHistory(RequestDTO dto);

}
