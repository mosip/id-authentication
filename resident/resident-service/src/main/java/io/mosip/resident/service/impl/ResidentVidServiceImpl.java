package io.mosip.resident.service.impl;

import io.mosip.resident.constant.ApiName;
import io.mosip.resident.dto.AuthRequestDTO;
import io.mosip.resident.dto.AuthResponseDTO;
import io.mosip.resident.dto.VidRequestDto;
import io.mosip.resident.service.ResidentVidService;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ResidentVidServiceImpl implements ResidentVidService {

    @Autowired
    private Environment env;

    @Autowired
    private ResidentServiceRestClient residentServiceRestClient;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Override
    public boolean isAuthenticationSuccessful(VidRequestDto requestDto) {
        AuthRequestDTO reqDto = new AuthRequestDTO();
        reqDto.setTransactionID(requestDto.getTransactionID());

        AuthResponseDTO response;
        try {
            response = (AuthResponseDTO) residentServiceRestClient.postApi(env.getProperty(ApiName.INTERNALAUTH.name()), null,
                    reqDto, AuthResponseDTO.class, tokenGenerator.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
