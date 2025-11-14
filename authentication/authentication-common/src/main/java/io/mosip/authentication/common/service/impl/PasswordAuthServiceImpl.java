package io.mosip.authentication.common.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.PasswordAuthType;
import io.mosip.authentication.common.service.impl.match.PasswordMatchType;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.PasswordAuthService;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class PasswordAuthServiceImpl implements PasswordAuthService {

    @Autowired
    private IdInfoHelper idInfoHelper;

    /** The id info helper. */
    @Autowired
    private MatchInputBuilder matchInputBuilder;

    /** The ida mapping config. */
    @Autowired
    private IDAMappingConfig idaMappingConfig;

    public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO,String individualId,
                                       Map<String,List<IdentityInfoDTO>> idInfo,String partnerId)
            throws IdAuthenticationBusinessException {

        if (idInfo == null || idInfo.isEmpty()) {
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
        }

        List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO, idInfo);

        List<MatchOutput> listMatchOutputs = constructMatchOutput(authRequestDTO, listMatchInputs, idInfo,
                partnerId);
        // Using AND condition on the match output for Bio auth.
        boolean isMatched = !listMatchOutputs.isEmpty() && listMatchOutputs.stream().allMatch(MatchOutput::isMatched);
        return AuthStatusInfoBuilder.buildStatusInfo(isMatched, listMatchInputs, listMatchOutputs,
                PasswordAuthType.values(), idaMappingConfig);

    }

    public List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO,
                                                Map<String, List<IdentityInfoDTO>> idInfo) {
        return matchInputBuilder.buildMatchInput(authRequestDTO, PasswordAuthType.values(), PasswordMatchType.values(),
                idInfo);
    }

    private List<MatchOutput> constructMatchOutput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInputs,
                                                   Map<String,List<IdentityInfoDTO>> idInfo, String partnerId) 
                                                   throws IdAuthenticationBusinessException {
        return idInfoHelper.matchIdentityData(authRequestDTO, idInfo, listMatchInputs, partnerId);
    }
}
