package io.mosip.resident.service;

import io.mosip.resident.dto.VidRequestDto;
import io.mosip.resident.dto.VidResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ResidentVidService {

    public VidResponseDto generateVid(VidRequestDto requestDto);
}
