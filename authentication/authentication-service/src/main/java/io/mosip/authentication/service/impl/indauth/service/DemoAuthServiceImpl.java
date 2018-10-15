package io.mosip.authentication.service.impl.indauth.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.idauth.demo.PersonalAddressDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalFullAddressDTO;
import io.mosip.authentication.core.spi.idauth.demo.PersonalIdentityDTO;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.builder.AuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatcher;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchInput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchOutput;
import io.mosip.authentication.service.impl.indauth.service.demo.MatchingStrategyType;
import io.mosip.authentication.service.repository.DemoRepository;

/**
 * The implementation of Demographic Authentication service.
 * 
 * @author Arun Bose
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
	
	@Autowired
	private DemoRepository demoRepository;

	/**
	 * Construct match input.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @return the list
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {

		List<MatchInput> listMatchInputs = new ArrayList<>();
		constructFadMatchInput(authRequestDTO, listMatchInputs);
		constructAdMatchInput(authRequestDTO, listMatchInputs);
		constructPIDMatchInput(authRequestDTO, listMatchInputs);
		return listMatchInputs;
	}

	/**
	 * Construct PID match input.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param listMatchInput
	 *            the list match input
	 * @return the list
	 */
	private List<MatchInput> constructPIDMatchInput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInputs) {
		PersonalIdentityDTO pid = authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalIdentityDTO();
		if (null != pid) {
			if (null != pid.getNamePri()) {
				Integer matchValue = DEFAULT_EXACT_MATCH_VALUE;

				if (pid.getMsPri() != null && pid.getMsPri().equals(MatchingStrategyType.PARTIAL.getType())) {
					matchValue = pid.getMtPri();
					if (null == matchValue) {
						matchValue = Integer.parseInt(environment.getProperty("demo.default.match.value"));
					}
				}

				MatchInput matchInput = new MatchInput(DemoMatchType.NAME_PRI, pid.getMsPri(), matchValue);
				listMatchInputs.add(matchInput);
			}

			if (null != pid.getAge()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.AGE, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}

			if (null != pid.getDob()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.DOB, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}

			if (null != pid.getEmail()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.EMAIL, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}

			if (null != pid.getPhone()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.MOBILE, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}

			if (null != pid.getGender()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.GENDER, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}

		}
		return listMatchInputs;
	}

	/**
	 * Construct ad match input.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param listMatchInput
	 *            the list match input
	 * @return the list
	 */
	private List<MatchInput> constructAdMatchInput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInputs) {
		PersonalAddressDTO ad = authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalAddressDTO();
		if (null != ad) {
			if (null != ad.getAddrLine1Pri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_LINE1_PRI, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}

			if (null != ad.getAddrLine2Pri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_LINE2_PRI, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);

			}

			if (null != ad.getAddrLine3Pri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_LINE3_PRI, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}
			if (null != ad.getCountryPri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.COUNTRY_PRI, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}

			if (null != ad.getPinCodePri()) {
				MatchInput matchInput = new MatchInput(DemoMatchType.PINCODE_PRI, MatchingStrategyType.EXACT.getType(),
						DEFAULT_EXACT_MATCH_VALUE);
				listMatchInputs.add(matchInput);
			}

		}
		return listMatchInputs;
	}

	/**
	 * Construct fad match input.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param listMatchInput
	 *            the list match input
	 * @return the list
	 */
	private List<MatchInput> constructFadMatchInput(AuthRequestDTO authRequestDTO, List<MatchInput> listMatchInputs) {
		Integer matchValue = DEFAULT_EXACT_MATCH_VALUE;
		PersonalFullAddressDTO fad = authRequestDTO.getPersonalDataDTO().getDemoDTO().getPersonalFullAddressDTO();
		if (null != fad) {
			if (null != fad.getAddrPri()) {
				if (fad.getMsPri() != null && fad.getMsPri().equals(MatchingStrategyType.PARTIAL.getType())) {
					matchValue = fad.getMtPri();
					if (null == matchValue) {
						matchValue = Integer.parseInt(environment.getProperty("demo.default.match.value"));
					}

					// TODO add it for secondary language
				}
				MatchInput matchInput = new MatchInput(DemoMatchType.ADDR_PRI, fad.getMsPri(), matchValue);
				listMatchInputs.add(matchInput);
			}
		}
		return listMatchInputs;
	}

	/**
	 * Gets the match output.
	 *
	 * @param listMatchInput
	 *            the list match input
	 * @param demoDTO
	 *            the demo DTO
	 * @param demoEntity
	 *            the demo entity
	 * @return the match output
	 */
	public List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, DemoDTO demoDTO, DemoEntity demoEntity) {

		return demoMatcher.matchDemoData(demoDTO, demoEntity, listMatchInputs);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.service.DemoAuthService#
	 * getDemoStatus(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	public AuthStatusInfo getDemoStatus(AuthRequestDTO authRequestDTO, String refId) throws IdAuthenticationBusinessException {
		boolean demoMatched = false;
		List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
		DemoEntity demoEntity = getDemoEntity(refId, authRequestDTO.getPersonalDataDTO()
																.getDemoDTO()
																.getLangPri());
		AuthStatusInfoBuilder statusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		if(demoEntity != null) {
			List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs,
			        authRequestDTO.getPersonalDataDTO().getDemoDTO(), 
			        demoEntity);
			demoMatched = listMatchOutputs.stream().allMatch(MatchOutput::isMatched);
			
			statusInfoBuilder.setStatus(demoMatched);
			
			listMatchInputs.stream()
				.forEach(matchInput -> {
							if(AuthType.getAuthTypeForMatchType(matchInput.getDemoMatchType())
									.map(AuthType::getType).isPresent()) {
								statusInfoBuilder.addMessageInfo(
										AuthType.getAuthTypeForMatchType(matchInput.getDemoMatchType())
												.map(AuthType::getType).orElse(""),
										matchInput.getMatchStrategyType(), 
										matchInput.getMatchValue());
							}
							
							statusInfoBuilder
							.addAuthUsageDataBits(matchInput.getDemoMatchType().getUsedBit());
						});
			
			
			
			listMatchOutputs.forEach(matchOutput -> {
							if(matchOutput.isMatched()) {
								statusInfoBuilder.addAuthUsageDataBits(
										matchOutput.getDemoMatchType()
										.getMatchedBit());
							}
						});
		} else {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST);//TODO check constant
		}
			
			
		return statusInfoBuilder.build();
		

	}

	/**
	 * Gets the demo entity.
	 *
	 * @param uniqueId
	 *            the unique id
	 * @return the demo entity
	 */
	public DemoEntity getDemoEntity(String refId,String langCode) {
		return demoRepository.findByUinRefIdAndLangCode(refId, langCode);
	}
	
	public static void main(String[] args) {
		DemoAuthServiceImpl demoService=new DemoAuthServiceImpl();
		DemoEntity demoEntity=demoService.getDemoEntity("12345", "EN");
		System.out.println(demoEntity.getFirstName());
	}

}