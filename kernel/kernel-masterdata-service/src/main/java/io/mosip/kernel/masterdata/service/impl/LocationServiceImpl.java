package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.LocationErrorCode;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.MasterdataSearchErrorCode;
import io.mosip.kernel.masterdata.constant.RequestErrorCode;
import io.mosip.kernel.masterdata.constant.ValidationErrorCode;
import io.mosip.kernel.masterdata.dto.LocationCreateDto;
import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.StatusResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.LocationExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.PostLocationCodeResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.Pagination;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.dto.response.ColumnValue;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.LocationSearchDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.exception.ValidationException;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.service.LocationService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.LanguageUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MasterDataFilterHelper;
import io.mosip.kernel.masterdata.utils.MasterdataCreationUtil;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;
import io.mosip.kernel.masterdata.utils.Node;
import io.mosip.kernel.masterdata.utils.PageUtils;
import io.mosip.kernel.masterdata.utils.UBtree;
import io.mosip.kernel.masterdata.validator.FilterColumnEnum;
import io.mosip.kernel.masterdata.validator.FilterColumnValidator;
import io.mosip.kernel.masterdata.validator.FilterTypeEnum;

/**
 * Class will fetch Location details based on various parameters this class is
 * implemented from {@link LocationService}}
 * 
 * @author Srinivasan
 * @author Tapaswini
 * @author Sidhant Agarwal
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class LocationServiceImpl implements LocationService {

	/**
	 * creates an instance of repository class {@link LocationRepository}}
	 */
	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	FilterColumnValidator filterColumnValidator;

	@Autowired
	MasterDataFilterHelper masterDataFilterHelper;

	@Autowired
	private UBtree<Location> locationTree;

	@Autowired
	private PageUtils pageUtils;

	@Autowired
	private LanguageUtils languageUtils;

	@Autowired
	private LocalValidatorFactoryBean validator;

	private List<Location> childHierarchyList = null;
	private List<Location> hierarchyChildList = null;
	private List<Location> parentHierarchyList = null;
	private List<String> childList = null;
	
	@Autowired
	private MasterdataCreationUtil masterdataCreationUtil;

	/**
	 * This method will all location details from the Database. Refers to
	 * {@link LocationRepository} for fetching location hierarchy
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.LocationService#getLocationDetails(java.
	 * lang.String)
	 */
	@Override
	public LocationHierarchyResponseDto getLocationDetails(String langCode) {
		List<LocationHierarchyDto> responseList = null;
		LocationHierarchyResponseDto locationHierarchyResponseDto = new LocationHierarchyResponseDto();
		List<Object[]> locations = null;
		try {

			locations = locationRepository.findDistinctLocationHierarchyByIsDeletedFalse(langCode);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}
		if (!locations.isEmpty()) {

			responseList = MapperUtils.objectToDtoConverter(locations);

		} else {
			throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		locationHierarchyResponseDto.setLocations(responseList);
		return locationHierarchyResponseDto;
	}

	/**
	 * This method will fetch location hierarchy based on location code and language
	 * code Refers to {@link LocationRepository} for fetching location hierarchy
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return LocationHierarchyResponseDto-
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.LocationService#
	 * getLocationHierarchyByLangCode(java.lang.String, java.lang.String)
	 */
	@Override
	public LocationResponseDto getLocationHierarchyByLangCode(String locCode, String langCode) {
		List<Location> childList = null;
		List<Location> parentList = null;
		childHierarchyList = new ArrayList<>();
		parentHierarchyList = new ArrayList<>();
		LocationResponseDto locationHierarchyResponseDto = new LocationResponseDto();
		try {

			List<Location> locHierList = getLocationHierarchyList(locCode, langCode);
			if (locHierList != null && !locHierList.isEmpty()) {
				for (Location locationHierarchy : locHierList) {
					String currentParentLocCode = locationHierarchy.getParentLocCode();
					childList = getChildList(locCode, langCode);
					parentList = getParentList(currentParentLocCode, langCode);

				}
				locHierList.addAll(childList);
				locHierList.addAll(parentList);

				List<LocationDto> locationHierarchies = MapperUtils.mapAll(locHierList, LocationDto.class);
				locationHierarchyResponseDto.setLocations(locationHierarchies);

			} else {
				throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		}

		catch (DataAccessException | DataAccessLayerException e) {

			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));

		}
		return locationHierarchyResponseDto;
	}

	@Override
	@Transactional
	public ResponseWrapper<Location> createLocation(LocationCreateDto dto) {
		List<ServiceError> errors = new ArrayList<>();
		Location locationEntity = null;
		//List<LocationDto> locations = new ArrayList<>();
		//List<Location> savedEntities = null;

		// request validation
		//requestValidation(dto, errors);

		dataValidation(dto);

		//Optional<LocationDto> optional = locations.stream().filter(dto -> dto.getIsActive().equals(true)).findAny();
		// if data present in all the configured languages then setting isActive as
		// true, otherwise false
//		if (optional.isPresent()) {
//			List<String> languages = new ArrayList<>();
//			locations.forEach(dto -> languages.add(dto.getLangCode()));
//			if (languages.size() == languageUtils.getConfiguredLanguages().size()
//					&& languageUtils.getConfiguredLanguages().containsAll(languages)) {
//				locations.forEach(i -> i.setIsActive(true));
//			} else {
//				throw new RequestException(LocationErrorCode.UNABLE_TO_ACTIVATE.getErrorCode(),
//						LocationErrorCode.UNABLE_TO_ACTIVATE.getErrorMessage());
//			}
//		}
		try {
			dto = masterdataCreationUtil.createMasterData(Location.class, dto);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException| SecurityException e) {
			// TODO Auto-generated catch block
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_INSERT_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_INSERT_EXCEPTION.getErrorMessage());
		}
		// Validation name already exists
		if(dto!=null)
		{
			List<Location> list = locationRepository.findByNameAndLevelLangCode(dto.getName(), dto.getHierarchyLevel(),dto.getLangCode());
			if (list != null && !list.isEmpty()) {
				throw new RequestException(LocationErrorCode.LOCATION_ALREDAY_EXIST_UNDER_HIERARCHY.getErrorCode(),
						String.format(LocationErrorCode.LOCATION_ALREDAY_EXIST_UNDER_HIERARCHY.getErrorMessage(),
								dto.getName()));
			}
		}
			
		// setting metadata
		//List<Location> entities = new ArrayList<>();
		/*for (LocationDto dto : locations) {
			entities.add(MetaDataUtils.setCreateMetaData(dto, Location.class));
		}*/
		if(dto!=null)
		{
			locationEntity = MetaDataUtils.setCreateMetaData(dto, Location.class);
		}
			
//			if(dto.getLangCode().equals(primaryLang))
//			{
//				locationCode = getCodeFromName(dto.getName());
//				locationEntity.setCode(locationCode);
//			}
			
		try {
			locationEntity = locationRepository.create(locationEntity);
		} catch (DataAccessLayerException | DataAccessException ex) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_INSERT_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_INSERT_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(ex));
		}
//		if(locationEntity!=null)
//		{
//			responseDto = MapperUtils.map(locationEntity, PostLocationCodeResponseDto.class);
//		}
		
		//List<PostLocationCodeResponseDto> dtos = MapperUtils.mapAll(savedEntities, PostLocationCodeResponseDto.class);

		ResponseWrapper<Location> response = new ResponseWrapper<>();
		response.setErrors(errors);
		response.setResponse(locationEntity);
		return response;
	}

	/**
	 * Method to perform the basic request validation
	 * 
	 * @param request
	 *            request to be validated
	 * @param errors
	 *            list of service errors
	 * @param locations
	 *            adding validation location to this list
	 */
	private void requestValidation(LocationDto dto, List<ServiceError> errors) {
			if (dto.getLangCode() != null && dto.getLangCode().equals(languageUtils.getPrimaryLanguage())) {
				Set<ConstraintViolation<LocationDto>> validations = validator.validate(dto);
				if (!validations.isEmpty()) {
					validations.forEach(
							i -> errors.add(new ServiceError(RequestErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
									dto.getLangCode() + "." + i.getPropertyPath() + ":" + i.getMessage())));
					throw new ValidationException(errors);
				} 
			} else if (dto.getLangCode() != null && languageUtils.getSecondaryLanguages().contains(dto.getLangCode())) {
				Set<ConstraintViolation<LocationDto>> validations = validator.validate(dto);
				if (!validations.isEmpty()) {
					validations.forEach(
							i -> errors.add(new ServiceError(RequestErrorCode.REQUEST_DATA_NOT_VALID.getErrorCode(),
									dto.getLangCode() + "." + i.getPropertyPath() + ":" + i.getMessage())));
				} 
			} else {
				errors.add(new ServiceError(LocationErrorCode.INVALID_LANG_CODE.getErrorCode(),
						String.format(LocationErrorCode.INVALID_LANG_CODE.getErrorMessage(), dto.getLangCode())));
			}
	}

	/**
	 * Method to perform the business validation for the location
	 * 
	 * @param request
	 *            list of location to be validated
	 */
	private void dataValidation(LocationCreateDto request) {
//		Set<String> ids = request.stream().map(LocationDto::getCode).collect(Collectors.toSet());
//		if (ids.size() > 1) {
//			throw new RequestException(LocationErrorCode.DIFFERENT_LOC_CODE.getErrorCode(),
//					LocationErrorCode.DIFFERENT_LOC_CODE.getErrorMessage());
//		}

		if(!request.getCode().equals(languageUtils.getPrimaryLanguage()))
		{
			throw new RequestException(LocationErrorCode.DATA_IN_PRIMARY_LANG_MISSING.getErrorCode(),
					String.format(LocationErrorCode.DATA_IN_PRIMARY_LANG_MISSING.getErrorMessage(),
							languageUtils.getPrimaryLanguage()));
		}
//		Set<Short> levels = request.stream().map(LocationDto::getHierarchyLevel).collect(Collectors.toSet());
//		if (levels.size() > 1) {
//			throw new RequestException(LocationErrorCode.INVALID_DIFF_HIERARCY_LEVEL.getErrorCode(),
//					LocationErrorCode.INVALID_DIFF_HIERARCY_LEVEL.getErrorMessage());
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.LocationService#updateLocationDetails(io.
	 * mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	@Transactional
	public PostLocationCodeResponseDto updateLocationDetails(LocationDto locationDto) {
		PostLocationCodeResponseDto postLocationCodeResponseDto = new PostLocationCodeResponseDto();
		CodeAndLanguageCodeID locationId = new CodeAndLanguageCodeID();
		locationId.setCode(locationDto.getCode());
		locationId.setLangCode(locationDto.getLangCode());
		try {
			Location location = locationRepository.findById(Location.class, locationId);

			if (location == null) {
				throw new RequestException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
			if (!locationDto.getIsActive() && findIsActiveInHierarchy(location)) {
				throw new RequestException(LocationErrorCode.LOCATION_CHILD_STATUS_EXCEPTION.getErrorCode(),
						LocationErrorCode.LOCATION_CHILD_STATUS_EXCEPTION.getErrorMessage());
			}
			location = MetaDataUtils.setUpdateMetaData(locationDto, location, true);
			locationRepository.update(location);
			MapperUtils.map(location, postLocationCodeResponseDto);

		} catch (DataAccessException | DataAccessLayerException ex) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_UPDATE_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_UPDATE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(ex));
		}

		return postLocationCodeResponseDto;
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.LocationService#deleteLocationDetials(java
	 * .lang.String)
	 */
	@Override
	@Transactional
	public CodeResponseDto deleteLocationDetials(String locationCode) {
		List<Location> locations = null;
		CodeResponseDto codeResponseDto = new CodeResponseDto();
		try {
			locations = locationRepository.findByCode(locationCode);
			if (!locations.isEmpty()) {

				locations.stream().map(MetaDataUtils::setDeleteMetaData)
						.forEach(location -> locationRepository.update(location));

			} else {
				throw new RequestException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

		} catch (DataAccessException | DataAccessLayerException ex) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_UPDATE_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_UPDATE_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(ex));
		}
		codeResponseDto.setCode(locationCode);
		return codeResponseDto;
	}

	/**
	 * Method creates location hierarchy data into the table based on the request
	 * parameter sent {@inheritDoc}
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.LocationService#
	 * getLocationDataByHierarchyName(java.lang.String)
	 */
	@Override
	public LocationResponseDto getLocationDataByHierarchyName(String hierarchyName) {
		List<Location> locationlist = null;
		LocationResponseDto locationHierarchyResponseDto = new LocationResponseDto();
		try {
			locationlist = locationRepository.findAllByHierarchyNameIgnoreCase(hierarchyName);

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}

		if (!(locationlist.isEmpty())) {
			List<LocationDto> hierarchyList = MapperUtils.mapAll(locationlist, LocationDto.class);
			locationHierarchyResponseDto.setLocations(hierarchyList);

		} else {

			throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		return locationHierarchyResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.LocationService#
	 * getImmediateChildrenByLocCodeAndLangCode(java.lang.String, java.lang.String)
	 */
	@Override
	public LocationResponseDto getImmediateChildrenByLocCodeAndLangCode(String locCode, String langCode) {
		List<Location> locationlist = null;
		LocationResponseDto locationHierarchyResponseDto = new LocationResponseDto();
		try {
			locationlist = locationRepository.findLocationHierarchyByParentLocCodeAndLanguageCode(locCode, langCode);

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage() + ExceptionUtils.parseException(e));
		}

		if (locationlist.isEmpty()) {
			throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
		List<LocationDto> locationDtoList = MapperUtils.mapAll(locationlist, LocationDto.class);
		locationHierarchyResponseDto.setLocations(locationDtoList);
		return locationHierarchyResponseDto;
	}

	/**
	 * fetches location hierarchy details from database based on location code and
	 * language code
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return List<LocationHierarchy>
	 */
	private List<Location> getLocationHierarchyList(String locCode, String langCode) {

		return locationRepository.findLocationHierarchyByCodeAndLanguageCode(locCode, langCode);
	}

	/**
	 * fetches location hierarchy details from database based on parent location
	 * code and language code
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return List<LocationHierarchy>
	 */
	private List<Location> getLocationChildHierarchyList(String locCode, String langCode) {

		return locationRepository.findLocationHierarchyByParentLocCodeAndLanguageCode(locCode, langCode);

	}

	/**
	 * This method fetches child hierarchy details of the location based on location
	 * code
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return List<Location>
	 */
	private List<Location> getChildList(String locCode, String langCode) {

		if (locCode != null && !locCode.isEmpty()) {
			List<Location> childLocHierList = getLocationChildHierarchyList(locCode, langCode);
			childHierarchyList.addAll(childLocHierList);
			childLocHierList.parallelStream().filter(entity -> entity.getCode() != null && !entity.getCode().isEmpty())
					.map(entity -> getChildList(entity.getCode(), langCode)).collect(Collectors.toList());
		}

		return childHierarchyList;
	}

	/**
	 * This method fetches parent hierarchy details of the location based on parent
	 * Location code
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return List<LocationHierarcy>
	 */
	private List<Location> getParentList(String locCode, String langCode) {

		if (locCode != null && !locCode.isEmpty()) {
			List<Location> parentLocHierList = getLocationHierarchyList(locCode, langCode);
			parentHierarchyList.addAll(parentLocHierList);

			parentLocHierList.parallelStream()
					.filter(entity -> entity.getParentLocCode() != null && !entity.getParentLocCode().isEmpty())
					.map(entity -> getParentList(entity.getParentLocCode(), langCode)).collect(Collectors.toList());
		}

		return parentHierarchyList;
	}

	@Override
	public Map<Short, List<Location>> getLocationByLangCodeAndHierarchyLevel(String langCode, Short hierarchyLevel) {
		Map<Short, List<Location>> map = new TreeMap<>();
		List<Location> locations = locationRepository.getAllLocationsByLangCodeAndLevel(langCode, hierarchyLevel);
		if (!EmptyCheckUtils.isNullEmpty(locations)) {
			for (Location location : locations) {
				if (map.containsKey(location.getHierarchyLevel())) {
					map.get(location.getHierarchyLevel()).add(location);
				} else {
					List<Location> list = new ArrayList<>();
					list.add(location);
					map.put(location.getHierarchyLevel(), list);
				}
			}
			return map;
		} else {
			throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.LocationService#validateLocationName(java.
	 * lang.String)
	 */
	@Override
	public StatusResponseDto validateLocationName(String locationName) {
		StatusResponseDto statusResponseDto = null;
		boolean isPresent = false;
		try {
			statusResponseDto = new StatusResponseDto();
			statusResponseDto.setStatus(MasterDataConstant.INVALID);
			isPresent = locationRepository.isLocationNamePresent(locationName);
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage());
		}
		if (isPresent) {
			statusResponseDto.setStatus(MasterDataConstant.VALID);
		}
		return statusResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.LocationService#getLocations(int,
	 * int, java.lang.String, java.lang.String)
	 */
	@Override
	public PageDto<LocationExtnDto> getLocations(int pageNumber, int pageSize, String sortBy, String orderBy) {
		List<LocationExtnDto> locations = null;
		PageDto<LocationExtnDto> pageDto = null;
		try {
			Page<Location> pageData = locationRepository
					.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Direction.fromString(orderBy), sortBy)));
			if (pageData != null && pageData.getContent() != null && !pageData.getContent().isEmpty()) {
				locations = MapperUtils.mapAll(pageData.getContent(), LocationExtnDto.class);
				pageDto = new PageDto<>(pageData.getNumber(), pageData.getTotalPages(), pageData.getTotalElements(),
						locations);
			} else {
				throw new DataNotFoundException(LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorCode(),
						LocationErrorCode.LOCATION_NOT_FOUND_EXCEPTION.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage());
		}
		return pageDto;
	}

	/**
	 * This method to find, is there any child of given Location isActive is true
	 * then return true and break the loop. Otherwise if all children of the given
	 * location are false the return false.
	 * 
	 * @param location
	 * @return boolean return true or false
	 */
	public boolean findIsActiveInHierarchy(Location location) {
		boolean flag = false;
		String locCode = location.getCode();
		String langCode = location.getLangCode();

		List<Location> childList = new ArrayList<>();
		hierarchyChildList = new ArrayList<>();
		childList = getIsActiveChildList(locCode, langCode);
		if (!childList.isEmpty())
			return true;
		return flag;
	}

	/**
	 * This method fetches child hierarchy details of the location based on location
	 * code, here child isActive can true or false
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return List<Location>
	 */
	private List<Location> getIsActiveChildList(String locCode, String langCode) {

		if (locCode != null && !locCode.isEmpty()) {
			List<Location> childLocHierList = getIsActiveLocationChildHierarchyList(locCode, langCode);
			hierarchyChildList.addAll(childLocHierList);
			childLocHierList.parallelStream().filter(entity -> entity.getCode() != null && !entity.getCode().isEmpty())
					.map(entity -> getIsActiveChildList(entity.getCode(), langCode)).collect(Collectors.toList());
		}
		return hierarchyChildList;

	}

	/**
	 * fetches location hierarchy details from database based on parent location
	 * code and language code, children's isActive is either true or false
	 * 
	 * @param locCode
	 *            - location code
	 * @param langCode
	 *            - language code
	 * @return List<LocationHierarchy>
	 */
	private List<Location> getIsActiveLocationChildHierarchyList(String locCode, String langCode) {

		return locationRepository.findDistinctByparentLocCode(locCode, langCode);

	}

	/**
	 * This method fetches child hierarchy details of the location based on location
	 * code, here child isActive can true or false
	 * 
	 * @param locCode
	 *            - location code
	 * @return List<Location>
	 */
	@Override
	public List<String> getChildList(String locCode) {
		childList = new ArrayList<>();
		List<String> resultList = new ArrayList<>();
		resultList = getChildByLocCode(locCode);
		if (!resultList.isEmpty())
			return resultList;
		return Arrays.asList(locCode);
	}

	private List<String> getChildByLocCode(String locCode) {
		if (locCode != null && !locCode.isEmpty()) {
			List<String> childLocHierList = getLocationChildHierarchyList(locCode);
			childList.addAll(childLocHierList);
			childLocHierList.parallelStream().filter(Objects::nonNull).map(this::getChildByLocCode)
					.collect(Collectors.toList());
		}
		return childList;
	}

	/**
	 * fetches location hierarchy details from database based on parent location
	 * code and language code, children's isActive is either true or false
	 * 
	 * @param locCode
	 *            - location code
	 * @return List<LocationHierarchy>
	 */
	private List<String> getLocationChildHierarchyList(String locCode) {

		return locationRepository.findDistinctByparentLocCode(locCode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.LocationService#searchLocation(io.mosip.
	 * kernel.masterdata.dto.request.SearchDto)
	 */
	@Override
	public PageResponseDto<LocationSearchDto> searchLocation(SearchDto dto) {
		PageResponseDto<LocationSearchDto> pageDto = null;
		String active = null;
		boolean isActive=true;
		List<LocationSearchDto> responseDto = new ArrayList<>();
		if(!CollectionUtils.isEmpty(dto.getFilters()))
		{
			Optional<SearchFilter> isActiveFilter = dto.getFilters().stream().filter(a->a.getColumnName().equals(MasterDataConstant.IS_ACTIVE)).findFirst();
			if(isActiveFilter.isPresent())
			{
				active = isActiveFilter.get().getValue();
				isActive = Boolean.valueOf(active);
				dto.getFilters().remove(isActiveFilter.get());
			}
		}
		List<Location> locationList = locationRepository.findAllByLangCode(dto.getLanguageCode(),isActive);
		locationList=locationList.stream().filter(location -> location.getHierarchyLevel()!=0).collect(Collectors.toList());
		List<Node<Location>> tree = locationTree.createTree(locationList);
		
		if (dto.getFilters().isEmpty()) {
			responseDto = emptyFilterLocationSearch(tree);
		}else if(dto.getFilters().size()==1 && !StringUtils.isEmpty(active) && !tree.isEmpty())
		{
			responseDto = emptyFilterLocationSearch(tree,isActive);
		}
		else {
			for (SearchFilter filter : dto.getFilters()) {
				validateFilters(filter);
				String type = filter.getType();
				if (type.equalsIgnoreCase(FilterTypeEnum.EQUALS.toString())) {
					responseDto = getEqualsLocationSearch(filter, dto, tree,isActive);
				} else {
					if (type.equalsIgnoreCase(FilterTypeEnum.CONTAINS.toString())) {
						responseDto = getContainsLocationSearch(filter, dto, tree,isActive);
					} else {
						if (type.equalsIgnoreCase(FilterTypeEnum.STARTSWITH.toString())) {
							responseDto = getStartsWithLocationSearch(filter, dto, tree,isActive);
						} else {
							throw new RequestException(ValidationErrorCode.FILTER_NOT_SUPPORTED.getErrorCode(),
									String.format(ValidationErrorCode.FILTER_NOT_SUPPORTED.getErrorMessage(),
											filter.getColumnName(), filter.getType()));
						}

					}

				}

			}
		}
		Pagination pagination = dto.getPagination();
		List<SearchSort> sort = dto.getSort();
		pageDto = pageUtils.sortPage(responseDto, sort, pagination);
		return pageDto;
	}
	
	
	private boolean validateFilters(SearchFilter filter) {
		if (filter != null) {
			if (filter.getColumnName() != null && !filter.getColumnName().trim().isEmpty()) {
				if (filter.getType() != null && !filter.getType().trim().isEmpty()) {
					return true;
				} else {
					throw new RequestException(MasterdataSearchErrorCode.FILTER_TYPE_NOT_AVAILABLE.getErrorCode(),
							String.format(MasterdataSearchErrorCode.FILTER_TYPE_NOT_AVAILABLE.getErrorMessage(),
									filter.getColumnName()));
				}
			} else {
				throw new RequestException(MasterdataSearchErrorCode.MISSING_FILTER_COLUMN.getErrorCode(),
						MasterdataSearchErrorCode.MISSING_FILTER_COLUMN.getErrorMessage());
			}
		}
		return false;
	}

	/**
	 * Method to search Location with no filter mentioned.
	 * 
	 * @param tree
	 *            the Location Tree.
	 * @return list of {@link LocationSearchDto}.
	 */
	private List<LocationSearchDto> emptyFilterLocationSearch(List<Node<Location>> tree) {
		List<LocationSearchDto> responseDto = new ArrayList<>();
		Node<Location> root = locationTree.findRootNode(tree.get(0));
		List<Node<Location>> leafNodes = locationTree.findLeafs(root);

		leafNodes.forEach(leafNode -> {
			List<Location> leafParents = locationTree.getParentHierarchy(leafNode);

			LocationSearchDto locationSearchDto = new LocationSearchDto();
			leafParents.forEach(p -> {

				if (p.getHierarchyLevel() == 1) {
					locationSearchDto.setRegion(p.getName());
				}
				if (p.getHierarchyLevel() == 2) {
					locationSearchDto.setProvince(p.getName());
				}
				if (p.getHierarchyLevel() == 3) {
					locationSearchDto.setCity(p.getName());
				}
				if (p.getHierarchyLevel() == 4) {
					locationSearchDto.setZone(p.getName());
				}
				if (p.getHierarchyLevel() == 5) {
					locationSearchDto.setPostalCode(p.getName());
				}
				locationSearchDto.setCreatedBy(p.getCreatedBy());
				locationSearchDto.setCreatedDateTime(p.getCreatedDateTime());
				locationSearchDto.setDeletedDateTime(p.getDeletedDateTime());
				locationSearchDto.setIsActive(p.getIsActive());
				locationSearchDto.setIsDeleted(p.getIsDeleted());
				locationSearchDto.setUpdatedBy(p.getUpdatedBy());
				locationSearchDto.setUpdatedDateTime(p.getUpdatedDateTime());
			});
			responseDto.add(locationSearchDto);
		});

		return responseDto;
	}
	
	
	/**
	 * Method to search Location with no filter mentioned.
	 * 
	 * @param tree
	 *            the Location Tree.
	 * @return list of {@link LocationSearchDto}.
	 */
	private List<LocationSearchDto> emptyFilterLocationSearch(List<Node<Location>> tree,boolean isActive) {
		List<LocationSearchDto> responseDto = new ArrayList<>();
		Node<Location> root = locationTree.findRootNode(tree.get(0));
		List<Node<Location>> leafNodes = locationTree.findLeafs(root);

		leafNodes.forEach(leafNode -> {
			List<Location> leafParents = locationTree.getParentHierarchy(leafNode);

			LocationSearchDto locationSearchDto = new LocationSearchDto();
			leafParents.forEach(p -> {
				if(p.getIsActive()==isActive)
				{
					if (p.getHierarchyLevel() == 1) {
						locationSearchDto.setRegion(p.getName());
					}
					if (p.getHierarchyLevel() == 2) {
						locationSearchDto.setProvince(p.getName());
					}
					if (p.getHierarchyLevel() == 3) {
						locationSearchDto.setCity(p.getName());
					}
					if (p.getHierarchyLevel() == 4) {
						locationSearchDto.setZone(p.getName());
					}
					if (p.getHierarchyLevel() == 5) {
						locationSearchDto.setPostalCode(p.getName());
					}
					locationSearchDto.setCreatedBy(p.getCreatedBy());
					locationSearchDto.setCreatedDateTime(p.getCreatedDateTime());
					locationSearchDto.setDeletedDateTime(p.getDeletedDateTime());
					locationSearchDto.setIsActive(p.getIsActive());
					locationSearchDto.setIsDeleted(p.getIsDeleted());
					locationSearchDto.setUpdatedBy(p.getUpdatedBy());
					locationSearchDto.setUpdatedDateTime(p.getUpdatedDateTime());
				}
				
			});
			responseDto.add(locationSearchDto);
		});

		return responseDto;
	}

	/**
	 * Method to find Location for equal data.
	 * 
	 * @param filter
	 *            the search filters provided
	 * @param dto
	 *            the search DTO provided.
	 * @param tree
	 *            the unbalanced tree of Location.
	 * @param isActive 
	 * @return the list of {@link LocationSearchDto}.
	 */
	private List<LocationSearchDto> getEqualsLocationSearch(SearchFilter filter, SearchDto dto,
			List<Node<Location>> tree, boolean isActive) {
		List<LocationSearchDto> locationSearch = new ArrayList<>();
		short locLevel = Short.parseShort(getHierarchyLevel(filter.getColumnName()));
		Location location = locationRepository.findLocationByHierarchyLevel(locLevel, filter.getValue(),
				dto.getLanguageCode(),isActive);
		if (location != null) {
			locationSearch = getListOfLocationNodes(tree, location,locationSearch);
		}
		return locationSearch;
	}

	private List<LocationSearchDto> getListOfLocationNodes(List<Node<Location>> tree, Location location,List<LocationSearchDto> responseDto) {	
		Node<Location> node = locationTree.findNode(tree, location.getCode());
		List<Node<Location>> leafNodes = locationTree.findLeafs(node);
		leafNodes.forEach(leafNode -> {
			List<Location> leafParents = locationTree.getParentHierarchy(leafNode);

			LocationSearchDto locationSearchDto = new LocationSearchDto();
			leafParents.forEach(p -> {
				if (p.getHierarchyLevel() == 1) {
					locationSearchDto.setRegion(p.getName());
				}
				if (p.getHierarchyLevel() == 2) {
					locationSearchDto.setProvince(p.getName());
				}
				if (p.getHierarchyLevel() == 3) {
					locationSearchDto.setCity(p.getName());
				}
				if (p.getHierarchyLevel() == 4) {
					locationSearchDto.setZone(p.getName());
				}
				if (p.getHierarchyLevel() == 5) {
					locationSearchDto.setPostalCode(p.getName());
				}
				locationSearchDto.setCreatedBy(p.getCreatedBy());
				locationSearchDto.setCreatedDateTime(p.getCreatedDateTime());
				locationSearchDto.setDeletedDateTime(p.getDeletedDateTime());
				locationSearchDto.setIsActive(p.getIsActive());
				locationSearchDto.setIsDeleted(p.getIsDeleted());
				locationSearchDto.setUpdatedBy(p.getUpdatedBy());
				locationSearchDto.setUpdatedDateTime(p.getUpdatedDateTime());
			});
			responseDto.add(locationSearchDto);
		});

		return responseDto;
	}

	/**
	 * Method to find Location for contains data mentioned.
	 * 
	 * @param filter
	 *            the search filters provided
	 * @param dto
	 *            the search DTO provided.
	 * @param tree
	 *            the unbalanced tree of Location.
	 * @param isActive 
	 * @return the list of {@link LocationSearchDto}.
	 */
	private List<LocationSearchDto> getContainsLocationSearch(SearchFilter filter, SearchDto dto,
			List<Node<Location>> tree, boolean isActive) {
		List<LocationSearchDto> locationSearch = new ArrayList<>();
		short locLevel = Short.parseShort(getHierarchyLevel(filter.getColumnName()));
		List<Location> locationList = locationRepository.findLocationByHierarchyLevelContains(locLevel,
				"%" + filter.getValue().toLowerCase() + "%", dto.getLanguageCode(),isActive);
		for (Location loc : locationList) {
			locationSearch = getListOfLocationNodes(tree, loc,locationSearch);
		}
		return locationSearch;
	}

	/**
	 * Method to find Location thats starts with provided data.
	 * 
	 * @param filter
	 *            the search filters provided
	 * @param dto
	 *            the search DTO provided.
	 * @param tree
	 *            the unbalanced tree of Location.
	 * @param isActive 
	 * @retun the list of {@link LocationSearchDto}.
	 */
	private List<LocationSearchDto> getStartsWithLocationSearch(SearchFilter filter, SearchDto dto,
			List<Node<Location>> tree, boolean isActive) {
		List<LocationSearchDto> locationSearch = new ArrayList<>();
		short hierarchyLevel = Short.parseShort(getHierarchyLevel(filter.getColumnName()));
		List<Location> locationList = locationRepository.findLocationByHierarchyLevelStartsWith(hierarchyLevel,
				filter.getValue().toLowerCase() + "%", dto.getLanguageCode(),isActive);
		for (Location loc : locationList) {
			locationSearch = getListOfLocationNodes(tree, loc,locationSearch);

		}

		return locationSearch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.LocationService#locationFilterValues(io.
	 * mosip.kernel.masterdata.dto.request.FilterValueDto)
	 */
	@Override
	public FilterResponseDto locationFilterValues(FilterValueDto filterValueDto) {
		FilterResponseDto filterResponseDto = new FilterResponseDto();
		List<ColumnValue> columnValueList = new ArrayList<>();
		List<String> hierarchyNames = null;
		try {
			hierarchyNames = locationRepository.findLocationAllHierarchyNames();
			String langCode = filterValueDto.getLanguageCode();
			for (FilterDto filter : filterValueDto.getFilters()) {
				String columnName = filter.getColumnName();
				String type = filter.getType();
				if (!type.equals(FilterColumnEnum.UNIQUE.toString()) && !type.equals(FilterColumnEnum.ALL.toString())) {
					throw new RequestException(ValidationErrorCode.FILTER_COLUMN_NOT_SUPPORTED.getErrorCode(),
							ValidationErrorCode.FILTER_COLUMN_NOT_SUPPORTED.getErrorMessage());
				}
				if (!hierarchyNames.contains(columnName)&&!columnName.equals(MasterDataConstant.IS_ACTIVE)) {
					throw new RequestException(ValidationErrorCode.INVALID_COLUMN_NAME.getErrorCode(),
							ValidationErrorCode.INVALID_COLUMN_NAME.getErrorMessage());
				}
				if (filter.getType().equals(FilterColumnEnum.UNIQUE.toString())) {
					if(filter.getColumnName().equals(MasterDataConstant.IS_ACTIVE))
					{
						List<String> filterValues = masterDataFilterHelper.filterValues(Location.class, filter, filterValueDto);
						filterValues.forEach(filterValue -> {
							ColumnValue columnValue = new ColumnValue();
							columnValue.setFieldID(MasterDataConstant.IS_ACTIVE);
							columnValue.setFieldValue(filterValue);
							columnValueList.add(columnValue);
						});
					}
					if (filter.getText() == null || filter.getText().isEmpty()) {
						List<String> locationNames = locationRepository
								.findDistinctHierarchyNameAndNameValueForEmptyTextFilter(filter.getColumnName(),
										langCode);
						locationNames.forEach(locationName -> {
							ColumnValue columnValue = new ColumnValue();
							columnValue.setFieldID(filter.getColumnName());
							columnValue.setFieldValue(locationName);
							columnValueList.add(columnValue);
						});

					} else {
						List<String> locationNames = locationRepository
								.findDistinctHierarchyNameAndNameValueForTextFilter(filter.getColumnName(),
										"%" + filter.getText().toLowerCase() + "%", langCode);
						locationNames.forEach(locationName -> {
							ColumnValue columnValue = new ColumnValue();
							columnValue.setFieldID(filter.getColumnName());
							columnValue.setFieldValue(locationName);
							columnValueList.add(columnValue);
						});
					}

				} else {
					if (filter.getText().isEmpty() || filter.getText() == null) {
						List<Location> locations = locationRepository
								.findAllHierarchyNameAndNameValueForEmptyTextFilter(filter.getColumnName(), langCode);
						locations.forEach(loc -> {
							ColumnValue columnValue = new ColumnValue();
							columnValue.setFieldID(filter.getColumnName());
							columnValue.setFieldValue(loc.getName());
							columnValueList.add(columnValue);
						});

					} else {
						List<Location> locations = locationRepository.findAllHierarchyNameAndNameValueForTextFilter(
								filter.getColumnName(), "%" + filter.getText().toLowerCase() + "%", langCode);
						locations.forEach(loc -> {
							ColumnValue columnValue = new ColumnValue();
							columnValue.setFieldID(filter.getColumnName());
							columnValue.setFieldValue(loc.getName());
							columnValueList.add(columnValue);
						});
					}

				}

			}
			filterResponseDto.setFilters(columnValueList);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorCode(),
					LocationErrorCode.LOCATION_FETCH_EXCEPTION.getErrorMessage());
		}

		return filterResponseDto;
	}

	/**
	 * Method to find out the hierrachy level from the column name
	 * 
	 * @param columnName
	 *            input column name
	 * @return hierarchy level
	 */
	public String getHierarchyLevel(String columnName) {
		if (columnName != null) {
			switch (columnName) {
			case MasterDataConstant.POSTAL_CODE:
				return "5";
			case MasterDataConstant.ZONE:
			case "Zone":
				return "4";
			case MasterDataConstant.CITY:
				return "3";
			case MasterDataConstant.PROVINCE:
				return "2";
			case MasterDataConstant.REGION:
				return "1";

			default:
				return "0";
			}
		}
		return "0";
	}

}
