package io.mosip.resident.service;

import io.mosip.resident.dto.ResponseWrapper;
import io.mosip.resident.dto.VidRequestDto;
import io.mosip.resident.dto.VidResponseDto;
import io.mosip.resident.exception.OtpValidationFailedException;
import org.springframework.stereotype.Service;

@Service
public interface ResidentVidService {

    public ResponseWrapper<VidResponseDto> generateVid(VidRequestDto requestDto) throws OtpValidationFailedException;
}
