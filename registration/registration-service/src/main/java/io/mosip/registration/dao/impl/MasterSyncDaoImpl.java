package io.mosip.registration.dao.impl;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.mapper.CustomObjectMapper.MAPPER_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.MACHINE_MAPPING_LOGGER_TITLE;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.MasterSyncDao;
import io.mosip.registration.dto.mastersync.BiometricAttributeResponseDto;
import io.mosip.registration.dto.mastersync.BiometricTypeResponseDto;
import io.mosip.registration.dto.mastersync.BlacklistedWordsDto;
import io.mosip.registration.dto.mastersync.DocumentCategoryDto;
import io.mosip.registration.dto.mastersync.DocumentTypeDto;
import io.mosip.registration.dto.mastersync.GenderTypeResponseDto;
import io.mosip.registration.dto.mastersync.IdTypeDto;
import io.mosip.registration.dto.mastersync.LanguageResponseDto;
import io.mosip.registration.dto.mastersync.LocationDto;
import io.mosip.registration.dto.mastersync.MasterSyncDto;
import io.mosip.registration.dto.mastersync.ReasonCategoryDto;
import io.mosip.registration.dto.mastersync.ReasonListDto;
import io.mosip.registration.dto.mastersync.TitleResponseDto;
import io.mosip.registration.entity.SyncControl;
import io.mosip.registration.entity.mastersync.BiometricAttribute;
import io.mosip.registration.entity.mastersync.BiometricType;
import io.mosip.registration.entity.mastersync.BlacklistedWords;
import io.mosip.registration.entity.mastersync.DocumentCategory;
import io.mosip.registration.entity.mastersync.DocumentType;
import io.mosip.registration.entity.mastersync.GenderType;
import io.mosip.registration.entity.mastersync.IdType;
import io.mosip.registration.entity.mastersync.Language;
import io.mosip.registration.entity.mastersync.Location;
import io.mosip.registration.entity.mastersync.ReasonCategory;
import io.mosip.registration.entity.mastersync.ReasonList;
import io.mosip.registration.entity.mastersync.Title;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.repositories.SyncJobRepository;
import io.mosip.registration.repositories.mastersync.BiometricAttributeReposiotry;
import io.mosip.registration.repositories.mastersync.BiometricTypeRepository;
import io.mosip.registration.repositories.mastersync.BlacklistedWordsRepository;
import io.mosip.registration.repositories.mastersync.DocumentCategoryRepo;
import io.mosip.registration.repositories.mastersync.DocumnetTypesRepository;
import io.mosip.registration.repositories.mastersync.GenderRepostry;
import io.mosip.registration.repositories.mastersync.IdTypeReposiotry;
import io.mosip.registration.repositories.mastersync.LanguageRepository;
import io.mosip.registration.repositories.mastersync.LocationMasterRepository;
import io.mosip.registration.repositories.mastersync.ReasonCatogryReposiotry;
import io.mosip.registration.repositories.mastersync.TitleRepository;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class MasterSyncDaoImpl implements MasterSyncDao {

	/** The master sync repository. */
	@Autowired
	private BlacklistedWordsRepository blackListedWordsRepository;

	/** The master sync repository. */
	@Autowired
	private DocumnetTypesRepository documnetTypesRepository;

	/** The master sync repository. */
	@Autowired
	private BiometricAttributeReposiotry biometricAttributeReposiotry;

	/** The master sync repository. */
	@Autowired
	private BiometricTypeRepository biometricTypeRepository;

	/** The master sync repository. */
	@Autowired
	private DocumentCategoryRepo documentCategoryrepository;

	/** The master sync repository. */
	@Autowired
	private IdTypeReposiotry idTypeRepository;

	/** The master sync repository. */
	@Autowired
	private GenderRepostry genderRepository;

	/** The master sync repository. */
	@Autowired
	private LanguageRepository languageRepository;

	/** The master sync repository. */
	@Autowired
	private ReasonCatogryReposiotry reasonCatogryReposiotry;

	/** The master sync repository. */
	@Autowired
	private LocationMasterRepository locationRepository;

	/** Object for Sync Status Repository. */
	@Autowired
	private SyncJobRepository syncStatusRepository;

	/** Object for Sync Status Repository. */
	@Autowired
	private TitleRepository titleRepository;

	/**
	 * logger for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(MasterSyncDaoImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.dao.MasterSyncDao#getMasterSyncStatus()
	 */
	@Override
	public SyncControl getMasterSyncStatus(String synccontrol) {

		SyncControl syncControlResonse = null;

		try {
			// find the user
			syncControlResonse = syncStatusRepository.findBySyncJobId(synccontrol);

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.MACHINE_MAPPING_RUN_TIME_EXCEPTION,
					runtimeException.getMessage());
		}
		LOGGER.debug(MACHINE_MAPPING_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID, "DAO findByID method ended");

		return syncControlResonse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.dao.MasterSyncDao#save(io.mosip.registration.mastersync
	 * .dto.MasterSyncDto)
	 */
	@Override
	public void insertMasterSyncData(MasterSyncDto masterSyncDto) throws RegBaseCheckedException {

		List<BiometricTypeResponseDto> biometricTypeResponseDtos = masterSyncDto.getBiometrictypes();
		List<BiometricAttributeResponseDto> biometricAttribute = masterSyncDto.getBiometricattributes();
		List<LanguageResponseDto> languageType = masterSyncDto.getLanguages();
		List<BlacklistedWordsDto> blacklistedWords = masterSyncDto.getBlacklistedwords();
		List<GenderTypeResponseDto> genderType = masterSyncDto.getGenders();
		List<IdTypeDto> idType = masterSyncDto.getIdtypes();
		List<TitleResponseDto> titlesList = masterSyncDto.getTitles();
		List<DocumentCategoryDto> documnetCatogry = masterSyncDto.getDocumentcategories();
		List<DocumentTypeDto> documentsType = masterSyncDto.getDocumenttypes();
		List<LocationDto> locationsList = masterSyncDto.getLocations();
		ReasonCategoryDto reasonCatogryType = masterSyncDto.getReasonCategory();

		try {

			List<Language> languagesList = MAPPER_FACADE.mapAsList(languageType, Language.class);

			languageRepository.saveAll(languagesList);

			List<BiometricType> biometricTypeList = MAPPER_FACADE.mapAsList(biometricTypeResponseDtos,
					BiometricType.class);

			biometricTypeRepository.saveAll(biometricTypeList);

			List<BiometricAttribute> biometricAttributeList = MAPPER_FACADE.mapAsList(biometricAttribute,
					BiometricAttribute.class);

			biometricAttributeReposiotry.saveAll(biometricAttributeList);

			List<BlacklistedWords> blacklistedWordsList = MAPPER_FACADE.mapAsList(blacklistedWords,
					BlacklistedWords.class);

			blackListedWordsRepository.saveAll(blacklistedWordsList);

			List<GenderType> genderList = MAPPER_FACADE.mapAsList(genderType, GenderType.class);

			genderRepository.saveAll(genderList);

			List<IdType> idTypeList = MAPPER_FACADE.mapAsList(idType, IdType.class);

			idTypeRepository.saveAll(idTypeList);

			List<DocumentCategory> documnetCatogryList = MAPPER_FACADE.mapAsList(documnetCatogry,
					DocumentCategory.class);

			documentCategoryrepository.saveAll(documnetCatogryList);

			List<DocumentType> documnetsList = MAPPER_FACADE.mapAsList(documentsType, DocumentType.class);

			documnetTypesRepository.saveAll(documnetsList);

			List<Title> titleLists = MAPPER_FACADE.mapAsList(titlesList, Title.class);

			titleRepository.saveAll(titleLists);

			List<Location> locationLists = MAPPER_FACADE.mapAsList(locationsList, Location.class);
			locationRepository.saveAll(locationLists);

			ReasonCategory reasonCatogryy = new ReasonCategory();

			reasonCatogryy.setCode(reasonCatogryType.getCode());
			reasonCatogryy.setDescription(reasonCatogryType.getDescription());
			reasonCatogryy.setLangCode(reasonCatogryType.getLangCode());
			reasonCatogryy.setName(reasonCatogryType.getName());
			reasonCatogryy.setIsActive(true);
			reasonCatogryy.setCrBy(SessionContext.getInstance().getUserContext().getName());
			reasonCatogryy.setCrDtime(new Timestamp(System.currentTimeMillis()));

			Set<ReasonListDto> reasonListtoSet = reasonCatogryType.getReasonLists().stream()
					.collect(Collectors.toSet());

			Set<ReasonList> setReasonList = new HashSet<>();

			for (ReasonListDto result : reasonListtoSet) {

				ReasonList reasonList = new ReasonList();
				reasonList.setName(result.getName());
				reasonList.setCode(result.getCode());
				reasonList.setLangCode(result.getLangCode());
				reasonList.setDescription(result.getDescription());
				reasonList.setName(result.getName());
				reasonList.setCrBy(SessionContext.getInstance().getUserContext().getName());
				reasonList.setCrDtime(new Timestamp(System.currentTimeMillis()));
				reasonList.setIsActive(true);
				reasonList.setReasonCategoryCode(reasonCatogryy);
				setReasonList.add(reasonList);
			}

			reasonCatogryy.setReasons(setReasonList);

			reasonCatogryReposiotry.save(reasonCatogryy);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
