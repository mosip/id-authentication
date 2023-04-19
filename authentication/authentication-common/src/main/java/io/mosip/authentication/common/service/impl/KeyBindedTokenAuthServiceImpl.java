package io.mosip.authentication.common.service.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.CERT_TP_AF_SEPERATOR;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.KeyBindedTokenAuthType;
import io.mosip.authentication.common.service.impl.match.KeyBindedTokenMatchType;
import io.mosip.authentication.common.service.repository.IdentityBindingCertificateRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.*;
import io.mosip.authentication.core.spi.indauth.match.EntityValueFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.KeyBindedTokenAuthService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class KeyBindedTokenAuthServiceImpl implements KeyBindedTokenAuthService {

    @Autowired
    private IdInfoHelper idInfoHelper;

    /** The id info helper. */
    @Autowired
    private MatchInputBuilder matchInputBuilder;

    /** The ida mapping config. */
    @Autowired
    private IDAMappingConfig idaMappingConfig;

    @Autowired
    private IdAuthSecurityManager securityManager;

    @Autowired
    private IdentityBindingCertificateRepository identityBindingCertificateRepository;


    public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO,String individualId,
                                       Map<String,List<IdentityInfoDTO>> idInfo,String partnerId)
            throws IdAuthenticationBusinessException {

        if (idInfo == null || idInfo.isEmpty()) {
            throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
        }

        List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO, idInfo);

        List<MatchOutput> listMatchOutputs = constructMatchOutput(authRequestDTO, listMatchInputs, individualId,
                partnerId);
        // Using AND condition on the match output for Bio auth.
        boolean isMatched = !listMatchOutputs.isEmpty() && listMatchOutputs.stream().allMatch(MatchOutput::isMatched);
        return AuthStatusInfoBuilder.buildStatusInfo(isMatched, listMatchInputs, listMatchOutputs,
                KeyBindedTokenAuthType.values(), idaMappingConfig);

    }

    public List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO,
                                                Map<String, List<IdentityInfoDTO>> idInfo) {
        return matchInputBuilder.buildMatchInput(authRequestDTO, KeyBindedTokenAuthType.values(), KeyBindedTokenMatchType.values(),
                idInfo);
    }

    private List<MatchOutput> constructMatchOutput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInputs,
                                                   String individualId, String partnerId) throws IdAuthenticationBusinessException {
        return idInfoHelper.matchIdentityData(authRequestDTO, individualId, listMatchInputs, new EntityValueFetcher() {
                        @Override
                        public Map<String, String> fetch(String individualId, AuthRequestDTO authReq, String partnerID)
                                throws IdAuthenticationBusinessException {
                            Map<String, String> entityInfo = new HashMap<>();
                            String idVidHash = securityManager.hash(authReq.getIndividualId());
                            LocalDateTime currentDateTime = LocalDateTime.now();
                            List<Object[]> resultList = identityBindingCertificateRepository.findAllByIdVidHashAndCertNotExpired(idVidHash, 
                                                currentDateTime);
                            if(resultList != null && !resultList.isEmpty()) {
                                for(Object[] entry : resultList) {
                                    String mapKey = ((String) entry[0]) + CERT_TP_AF_SEPERATOR + ((String) entry[1]);
                                    entityInfo.put(mapKey.toUpperCase(), (String) entry[2]);
                                }
                            } 
                            return entityInfo;
                        }
                    },
                partnerId);
    }
}
