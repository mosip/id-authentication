package io.mosip.authentication.service.impl.id.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.id.service.IdRepoService;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

@PropertySource("classpath:sample-output.properties")
@Service
public class IdRepoServiceImpl implements IdRepoService {

	@Value("${sample.demo.entity}")
	private String value;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, List<IdentityInfoDTO>> getIdInfo(String uinRefId) throws IdAuthenticationDaoException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> outputMap = mapper.readValue(value, new TypeReference<Map>() {
			});

			return outputMap.entrySet().parallelStream()
					.filter(entry -> entry.getKey().equals("response") && entry.getValue() instanceof Map)
					.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream())
					.filter(entry -> entry.getKey().equals("identity") && entry.getValue() instanceof Map)
					.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream())
					.collect(Collectors.toMap(Entry<String, Object>::getKey, entry -> {
						Object val = entry.getValue();
						if (val instanceof List) {
							List<Map> arrayList = (List) val;
							return arrayList.stream().filter(elem -> elem instanceof Map)
									.map(elem -> (Map<String, Object>) elem).map(map1 -> {
										IdentityInfoDTO idInfo = new IdentityInfoDTO();
										idInfo.setLanguage(String.valueOf(map1.get("language")));
										idInfo.setValue(String.valueOf(map1.get("value")));
										return idInfo;
									}).collect(Collectors.toList());

						}
						return Collections.emptyList();
					}));
		} catch (IOException e) {
			throw new IdAuthenticationDaoException();
		}

	}

}
