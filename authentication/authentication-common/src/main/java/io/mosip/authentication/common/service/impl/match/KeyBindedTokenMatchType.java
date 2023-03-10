package io.mosip.authentication.common.service.impl.match;

import io.mosip.authentication.core.indauth.dto.*;
import io.mosip.authentication.core.spi.indauth.match.*;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum KeyBindedTokenMatchType implements MatchType {


    KEYBINDEDTOKEN(IdaIdMapping.KEYBINDEDTOKEN, MatchType.setOf(KeyBindedTokenMatchingStrategy.EXACT));

    private IdMapping idMapping;
    private Category category;
    private Set<MatchingStrategy> allowedMatchingStrategy;

    private KeyBindedTokenMatchType(IdMapping idMapping, Set<MatchingStrategy> allowedMatchingStrategy) {
        this.idMapping = idMapping;
        this.allowedMatchingStrategy = Collections.unmodifiableSet(allowedMatchingStrategy);
        this.category = Category.KEYBINDEDTOKEN;
    }

    @Override
    public IdMapping getIdMapping() {
        return idMapping;
    }

    @Override
    public Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType) {
        return allowedMatchingStrategy.stream().filter(ms -> ms.getType().equals(matchStrategyType)).findAny();
    }

    @Override
    public Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction() {
        return id -> Collections.emptyMap();
    }

    @Override
    public Function<AuthRequestDTO, Map<String, String>> getReqestInfoFunction() {
        return (AuthRequestDTO authRequestDto) -> {
            Map<String, String> map = new HashMap<>();
            KycAuthRequestDTO kycAuthRequestDTO =  (KycAuthRequestDTO)authRequestDto;
            if(kycAuthRequestDTO != null && !CollectionUtils.isEmpty(kycAuthRequestDTO.getRequest().getKeyBindedTokens())) {
                map.put("token", kycAuthRequestDTO.getRequest().getKeyBindedTokens().get(0).getToken());
                map.put("tokenType", kycAuthRequestDTO.getRequest().getKeyBindedTokens().get(0).getType());
                map.put("tokenFormat", kycAuthRequestDTO.getRequest().getKeyBindedTokens().get(0).getFormat());
            }
            map.put("individualId", kycAuthRequestDTO.getIndividualId());
            return map;
        };
    }

    @Override
    public BiFunction<Map<String, String>, Map<String, Object>, Map<String, String>> getEntityInfoMapper() {
        return null;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean isMultiLanguage() {
        return false;
    }

    @Override
    public boolean isMultiLanguage(String propName, Map<String, List<IdentityInfoDTO>> identityEntity, MappingConfig mappingConfig) {
        return false;
    }

    @Override
    public boolean isPropMultiLang(String propName, MappingConfig cfg) {
        return false;
    }

    @Override
    public boolean hasRequestEntityInfo() {
        return true;
    }
}