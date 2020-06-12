package io.mosip.authentication.common.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.kernel.biometrics.constant.BiometricFunction;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biosdk.provider.factory.BioAPIFactory;
import io.mosip.kernel.biosdk.provider.spi.iBioProviderApi;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIR.BIRBuilder;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The Class BioMatcherUtil is the utility class to match biometrics, that uses the Bio SDK provider.
 * 
 * @author Loganathan Sekar
 */
@Component
public class BioMatcherUtil {
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(BioMatcherUtil.class);
	
	/** The id info fetcher. */
	@Autowired
	private IdInfoFetcher idInfoFetcher;
	
	/** The bio api factory. */
	@Autowired
	private BioAPIFactory bioApiFactory;

	/**
	 * Match function.
	 *
	 * @param probe the probe
	 * @param gallery the gallery
	 * @param properties the properties
	 * @return the double
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public double match(Map<String, String> probe, Map<String, String> gallery,
			Map<String, Object> properties) throws IdAuthenticationBusinessException {
		
		IdMapping[] idMappings = (IdMapping[]) properties.get(IdMapping.class.getSimpleName()); 
		BIR[][] objArrays = getBirValues(probe, gallery, idMappings);
		BIR[] reqInfoObj = objArrays[0];
		BIR[] entityBIR = objArrays[1];
		
		Map<BiometricType, List<BIR>> reqBirByType =  getBirByType(reqInfoObj);
		Map<BiometricType, List<BIR>> entityBirByType = getBirByType(entityBIR);
		
		boolean res = !reqBirByType.isEmpty();
		for (BiometricType modality : Arrays.asList(BiometricType.FINGER, BiometricType.IRIS, BiometricType.FACE)) {
			if(reqBirByType.containsKey(modality)) {
				try {
					iBioProviderApi bioProvider = bioApiFactory.getBioProvider(modality,
							BiometricFunction.MATCH);
					List<BIR> sample = reqBirByType.get(modality);
					List<BIR> record = entityBirByType.get(modality);
					if(sample != null) {
						if(record != null) {
							res =  bioProvider.verify(sample, record, modality, null);
							if(!res) {
								break;
							}
						} else {
							throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(), 
									String.format(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorMessage(), modality));
						}
					}
				} catch (BiometricException e) {
					logger.error(IdAuthCommonConstants.SESSION_ID, "IDA", "matchFunction",
							e.getClass().getSimpleName() + ": " + e.getMessage());
				}
			}
		}
	
		return res ? (double)  100 : (double) 0;
	}

	/**
	 * Gets the bir by type.
	 *
	 * @param reqInfoObj the req info obj
	 * @return the bir by type
	 * @throws IdAuthenticationBusinessException 
	 */
	private Map<BiometricType, List<BIR>> getBirByType(BIR[] reqInfoObj) throws IdAuthenticationBusinessException {
		 return	Stream.of(reqInfoObj)
				.filter(bir -> 
						Optional.ofNullable(bir.getBdbInfo())
								.map(BDBInfo::getType)
								.filter(list -> !list.isEmpty() && list.get(0) != null)
								.isPresent()
						)
				 .collect(Collectors.groupingBy(bir -> 
					BiometricType.fromValue(
							bir.getBdbInfo()
								.getType()
								.get(0)
								.value())));
	}

	/**
	 * Gets the bir values.
	 *
	 * @param reqInfo the req info
	 * @param entityInfo the entity info
	 * @param idMappings the id mappings
	 * @return the bir values
	 * @throws IdAuthenticationBusinessException 
	 */
	private BIR[][] getBirValues(Map<String, String> reqInfo, Map<String, String> entityInfo, IdMapping[] idMappings) throws IdAuthenticationBusinessException {
		BIR[] reqInfoObj;
		BIR[] entityInfoObj;
	
		int index = 0;
		if (reqInfo.keySet().stream().noneMatch(key -> key.startsWith(IdAuthCommonConstants.UNKNOWN_BIO))) {
			reqInfoObj = new BIR[reqInfo.size()];
			entityInfoObj = new BIR[reqInfo.size()];
	
			for (Map.Entry<String, String> e : reqInfo.entrySet()) {
				String key = e.getKey();
				
				reqInfoObj[index] = getBir(e.getValue(), getType(key, idMappings));
				entityInfoObj[index] = getBir(entityInfo.get(key), getType(key, idMappings));
				index++;
			}
		} else {
			List<IdAuthenticationBusinessException> reqMapexceptions = new ArrayList<>();
			Function<? super Entry<String, String>, ? extends BIR> birMapper = e -> {
				try {
					return getBir(e.getValue(), getType(e.getKey(), idMappings));
				} catch (IdAuthenticationBusinessException e1) {
					reqMapexceptions.add(e1);
					return null;
				}
			};
			reqInfoObj = reqInfo.entrySet().stream()
							.map(birMapper)
							.toArray(s -> new BIR[s]);
			if(!reqMapexceptions.isEmpty()) {
				throw reqMapexceptions.get(0);
			}
			
			List<IdAuthenticationBusinessException> entityMapexceptions = new ArrayList<>();

			entityInfoObj = entityInfo.entrySet()
								.stream()
								.map(birMapper)
								.toArray(s -> new BIR[s]);
			
			if(!entityMapexceptions.isEmpty()) {
				throw entityMapexceptions.get(0);
			}
		}
	
		return new BIR[][] { reqInfoObj, entityInfoObj };
	}

	/**
	 * Gets the type.
	 *
	 * @param idName the id name
	 * @param idMappings the id mappings
	 * @return the type
	 * @throws IdAuthenticationBusinessException 
	 */
	private BioInfo getType(String idName, IdMapping[] idMappings) throws IdAuthenticationBusinessException {
		//Note: Finger minutiea type not handled based on the requirement
		String typeForIdName = idInfoFetcher.getTypeForIdName(idName, idMappings).orElse("");
		long type = 0L;
		SingleType singleType = null;
		if(typeForIdName.equalsIgnoreCase(SingleType.FINGER.value())) {
			type = CbeffConstant.FORMAT_TYPE_FINGER;
			singleType = SingleType.FINGER;
		} else if(typeForIdName.equalsIgnoreCase(SingleType.IRIS.value())) {
			type = CbeffConstant.FORMAT_TYPE_IRIS;
			singleType = SingleType.IRIS;
		} else if(typeForIdName.equalsIgnoreCase(SingleType.FACE.value())) {
			type = CbeffConstant.FORMAT_TYPE_FACE;
			singleType = SingleType.FACE;
		} else {
			 logger.error(IdAuthCommonConstants.SESSION_ID, "IDA", "getType",
						"Found invalid type: " + typeForIdName);
			 throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
		}
		String[] subTypes = Arrays.stream(idName.split(" "))
				.filter(str -> !str.isEmpty())
				.toArray(s -> new String[s]);
		return new BioInfo(String.valueOf(type), singleType, subTypes);
	}

	/**
	 * To create BIRType based on requested input.
	 *
	 * @param info            the info
	 * @param type the type
	 * @return the bir
	 */
	private BIR getBir(Object info, BioInfo type) {
		BIRBuilder birBuilder = new BIRBuilder();
		if (info instanceof String) {
			RegistryIDType format = new RegistryIDType();
			format.setOrganization(String.valueOf(CbeffConstant.FORMAT_OWNER));
			format.setType(type.getType());
			BDBInfo bdbInfo = new BDBInfo.BDBInfoBuilder()
					.withType(Collections.singletonList(type.getSingleType()))
					.withSubtype(Arrays.asList(type.getSubTypes()))
					.withFormat(format).build();
			String reqInfoStr = (String) info;
			byte[] decodedrefInfo = decodeValue(reqInfoStr);
			birBuilder.withBdb(decodedrefInfo);
			birBuilder.withBdbInfo(bdbInfo);
		}
		return birBuilder.build();
	}

	/**
	 * Decode value.
	 *
	 * @param value
	 *            the value
	 * @return the byte[]
	 */
	private static byte[] decodeValue(String value) {
		return CryptoUtil.decodeBase64(value);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Data
	
	/**
	 * Instantiates a new bio info.
	 *
	 * @param type the type
	 * @param singleType the single type
	 * @param subTypes the sub types
	 */
	@AllArgsConstructor
	private static class BioInfo {
		
		/** The type. */
		private String type;
		
		/** The single type. */
		private SingleType singleType;
		
		/** The sub types. */
		private String[] subTypes;
	}

}
