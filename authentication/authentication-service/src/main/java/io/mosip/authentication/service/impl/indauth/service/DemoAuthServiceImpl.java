package io.mosip.authentication.service.impl.indauth.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalAddressDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalFullAddressDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalIdentityDTO;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatcher;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchInput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchOutput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchStrategyType;


/**
 * @author Arun Bose
 * The Class DemoAuthServiceImpl.
 */
@Service
public class DemoAuthServiceImpl implements DemoAuthService {

	/** The Constant DEFAULT_EXACT_MATCH_VALUE. */
	private static final int DEFAULT_EXACT_MATCH_VALUE = 100;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The demo matcher. */
	@Autowired
	private DemoMatcher demoMatcher;
	

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the list
	 */
	public List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {

		List<MatchInput> listMatchInput=new ArrayList<>();
		constructFadMatchInput(authRequestDTO,listMatchInput);
		constructAdMatchInput(authRequestDTO, listMatchInput);
        constructPIDMatchInput(authRequestDTO, listMatchInput);
		return listMatchInput;
	}

	/**
	 * Construct PID match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param listMatchInput the list match input
	 * @return the list
	 */
	private List<MatchInput> constructPIDMatchInput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInput) {
		Integer matchValue;
		PersonalIdentityDTO pid = authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalIdentityDTO();
		if (null != pid) {
			if (null != pid.getNamePri()) {
				if (pid.getMsPri().equals(MatchStrategyType.PARTIAL.getType())) {
					matchValue = pid.getMtPri();
					if (null == matchValue) {
						matchValue = Integer.parseInt(environment.getProperty("default.match.value"));
					}

					if (pid.getMsPri().equals(MatchStrategyType.EXACT.getType())) {
						matchValue = 100;
					}
					MatchInput matchInput = new MatchInput(DemoMatchType.NAME_PRI, pid.getMsPri(), matchValue);
					listMatchInput.add(matchInput);

				}

				if (null != pid.getAge()) {
					MatchInput matchInput = new MatchInput(DemoMatchType.AGE, MatchStrategyType.EXACT.getType(),
							DEFAULT_EXACT_MATCH_VALUE);
					listMatchInput.add(matchInput);
				}

				if (null != pid.getDobType()) {
					MatchInput matchInput = new MatchInput(DemoMatchType.DOB_TYPE,
							MatchStrategyType.EXACT.getType(), DEFAULT_EXACT_MATCH_VALUE);
					listMatchInput.add(matchInput);
				}

				if (null != pid.getDob()) {
					MatchInput matchInput = new MatchInput(DemoMatchType.DOB, MatchStrategyType.EXACT.getType(),
							DEFAULT_EXACT_MATCH_VALUE);
					listMatchInput.add(matchInput);
				}

				if (null != pid.getEmail()) {
					MatchInput matchInput = new MatchInput(DemoMatchType.EMAIL, MatchStrategyType.EXACT.getType(),
							DEFAULT_EXACT_MATCH_VALUE);
					listMatchInput.add(matchInput);
				}

				if (null != pid.getPhone()) {
					MatchInput matchInput = new MatchInput(DemoMatchType.MOBILE, MatchStrategyType.EXACT.getType(),
							DEFAULT_EXACT_MATCH_VALUE);
					listMatchInput.add(matchInput);
				}

				

			}
		}
		return listMatchInput;
	}

	/**
	 * Construct ad match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param listMatchInput the list match input
	 * @return the list
	 */
	private List<MatchInput> constructAdMatchInput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInput) {
		PersonalAddressDTO ad = authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalAddressDTO();
		if (null != ad) {
			if (null != ad.getAddrLine1Pri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_LINE1_PRI,
						MatchStrategyType.EXACT.getType(), DEFAULT_EXACT_MATCH_VALUE);
				listMatchInput.add(matchInput);
			}

			if (null != ad.getAddrLine2Pri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_LINE2_PRI,
						MatchStrategyType.EXACT.getType(), DEFAULT_EXACT_MATCH_VALUE);
				listMatchInput.add(matchInput);

			}

			if (null != ad.getAddrLine3Pri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_LINE3_PRI,
						MatchStrategyType.EXACT.getType(), DEFAULT_EXACT_MATCH_VALUE);
				listMatchInput.add(matchInput);

			}

			if (null != ad.getCountryPri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.COUNTRY_PRI, MatchStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInput.add(matchInput);
			}

			if (null != ad.getPinCodePri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.PINCODE_PRI, MatchStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInput.add(matchInput);
			}

		}
		return listMatchInput;
	}

	/**
	 * Construct fad match input.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param listMatchInput the list match input
	 * @return the list
	 */
	private List<MatchInput> constructFadMatchInput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInput) {
		Integer matchValue = null;
		PersonalFullAddressDTO fad = authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalFullAddressDTO();
		if (null != fad) {
			if (fad.getMsPri().equals(MatchStrategyType.PARTIAL.getType())) {
				matchValue = fad.getMtPri();
				if (null == matchValue) {
					matchValue = Integer.parseInt(environment.getProperty("default.match.value"));
				}

				if (fad.getMsPri().equals(MatchStrategyType.EXACT.getType())) {
					matchValue = 100;
				}

				MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_PRI, fad.getMsPri(), matchValue);
				listMatchInput.add(matchInput);

				// FIX it for secondary
			}
		}
		return listMatchInput;
	}
	
	/**
	 * Gets the match output.
	 *
	 * @param listMatchInput the list match input
	 * @param demoDTO the demo DTO
	 * @param demoEntity the demo entity
	 * @return the match output
	 */
	public List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInput,DemoDTO demoDTO,DemoEntity demoEntity){
		
		return demoMatcher.matchDemoData(demoDTO, demoEntity, listMatchInput);
		
	}
	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.service.DemoAuthService#getDemoStatus(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	public boolean getDemoStatus(AuthRequestDTO authRequestDTO) {
		boolean demoMatched=false;
		List<MatchInput> listMatchInput=constructMatchInput(authRequestDTO);
	    List<MatchOutput> listMatchOutput=getMatchOutput(listMatchInput,authRequestDTO.getPersonalDataDTO().getDemoDTO(),getDemoEntity(authRequestDTO.getId()));
	    demoMatched=listMatchOutput.stream().allMatch(MatchOutput::isMatched);
	    return demoMatched;
	   
		
		
	}
	
	/**
	 * Gets the demo entity.
	 *
	 * @param uniqueId the unique id
	 * @return the demo entity
	 */
	public DemoEntity getDemoEntity(String uniqueId) {
		return new DemoEntity();
	}
	
}