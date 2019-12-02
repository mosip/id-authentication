package io.mosip.resident.service;

import io.mosip.resident.dto.VidRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface ResidentVidService {

    public boolean isAuthenticationSuccessful(VidRequestDto requestDto);
}
