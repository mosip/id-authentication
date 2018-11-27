package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.TitleErrorCode;
import io.mosip.kernel.masterdata.dto.TitleDto;
import io.mosip.kernel.masterdata.dto.TitleResponseDto;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.TitleRepository;
import io.mosip.kernel.masterdata.service.TitleService;
import io.mosip.kernel.masterdata.utils.MapperUtils;

/**
 * Implementing service class for fetching titles from master db
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class TitleServiceImpl implements TitleService {

	@Autowired
	private TitleRepository titleRepository;
	
	@Autowired
	private MapperUtils objectMapperUtil;

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TitleService#getAllTitles()
	 */
	@Override
	public TitleResponseDto getAllTitles() {
		TitleResponseDto titleResponseDto = null;
		List<TitleDto> titleDto = null;
		List<Title> titles = null;
		try {
			titles = titleRepository.findAll(Title.class);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (titles != null && !titles.isEmpty()) {
			titleDto = objectMapperUtil.mapAll(titles, TitleDto.class);
		} else {
			throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
					TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
		}
		titleResponseDto = new TitleResponseDto();
		titleResponseDto.setTitleList(titleDto);
		return titleResponseDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.TitleService#getByLanguageCode(java.lang.
	 * String)
	 */
	@Override
	public TitleResponseDto getByLanguageCode(String languageCode) {
		TitleResponseDto titleResponseDto = null;
		List<TitleDto> titleDto = null;
		List<Title> title = new ArrayList<>();

		try {
			title = titleRepository.getThroughLanguageCode(languageCode);
		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorCode(),
					TitleErrorCode.TITLE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (title.isEmpty()) {
			throw new DataNotFoundException(TitleErrorCode.TITLE_NOT_FOUND.getErrorCode(),
					TitleErrorCode.TITLE_NOT_FOUND.getErrorMessage());
		}
		titleDto = objectMapperUtil.mapAll(title, TitleDto.class);

		titleResponseDto = new TitleResponseDto();
		titleResponseDto.setTitleList(titleDto);

		return titleResponseDto;
	}

}
