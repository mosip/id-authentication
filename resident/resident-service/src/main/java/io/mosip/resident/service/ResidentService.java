package io.mosip.resident.service;

import io.mosip.resident.constant.AuthTypeStatus;
import io.mosip.resident.dto.AuthHistoryRequestDTO;
import io.mosip.resident.dto.AuthHistoryResponseDTO;
import io.mosip.resident.dto.AuthLockOrUnLockRequestDto;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.RegStatusCheckResponseDTO;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.ResidentReprintRequestDto;
import io.mosip.resident.dto.ResidentReprintResponseDto;
import io.mosip.resident.dto.ResidentUpdateRequestDto;
import io.mosip.resident.dto.ResidentUpdateResponseDTO;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;

public interface ResidentService {

	public RegStatusCheckResponseDTO getRidStatus(RequestDTO dto) throws ApisResourceAccessException;

	public byte[] reqEuin(EuinRequestDTO euinRequestDTO) throws ResidentServiceCheckedException;

	public ResidentReprintResponseDto reqPrintUin(ResidentReprintRequestDto dto) throws ResidentServiceCheckedException;

	public ResponseDTO reqAauthTypeStatusUpdate(AuthLockOrUnLockRequestDto dto, AuthTypeStatus authTypeStatus)
			throws ResidentServiceCheckedException;

	public AuthHistoryResponseDTO reqAuthHistory(AuthHistoryRequestDTO dto) throws ResidentServiceCheckedException;
	
	public ResidentUpdateResponseDTO reqUinUpdate(ResidentUpdateRequestDto dto) throws ResidentServiceCheckedException;

}
