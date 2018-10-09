package io.mosip.authentication.service.impl.indauth.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalFullAddressDTO;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchInput;

@Service
public class DemoAuthServiceImpl implements DemoAuthService {

	public List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {

		List<MatchInput> listMatchInput = new ArrayList<>();
		PersonalFullAddressDTO fad = authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalFullAddressDTO();
		if (null != fad) {

			MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_PRI,
					authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalFullAddressDTO().getMsPri(),
					authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalFullAddressDTO().getMtPri());
			listMatchInput.add(matchInput);

			// FIX it for secondary
		}
		return listMatchInput;

	}
}