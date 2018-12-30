package io.mosip.kernel.masterdata.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.TemplateFileFormatErrorCode;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.entity.Template;
import io.mosip.kernel.masterdata.entity.TemplateFileFormat;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.masterdata.repository.TemplateRepository;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@Service
public class TemplateFileFormatServiceImpl implements TemplateFileFormatService {

	@Autowired
	private TemplateFileFormatRepository templateFileFormatRepository;

	@Autowired
	private TemplateRepository templateRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.masterdata.service.TemplateFileFormatService#
	 * createTemplateFileFormat(io.mosip.kernel.masterdata.dto.RequestDto)
	 */
	@Override
	public CodeAndLanguageCodeID createTemplateFileFormat(
			RequestDto<TemplateFileFormatDto> templateFileFormatRequestDto) {
		TemplateFileFormat entity = MetaDataUtils.setCreateMetaData(templateFileFormatRequestDto.getRequest(),
				TemplateFileFormat.class);
		TemplateFileFormat templateFileFormat;
		try {
			templateFileFormat = templateFileFormatRepository.create(entity);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new MasterDataServiceException(
					TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_INSERT_EXCEPTION.getErrorCode(),
					TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_INSERT_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		MapperUtils.map(templateFileFormat, codeLangCodeId);
		return codeLangCodeId;
	}

	@Override
	public CodeAndLanguageCodeID updateDevice(RequestDto<TemplateFileFormatDto> templateFileFormatRequestDto) {

		TemplateFileFormatDto templateFileFormatDto = templateFileFormatRequestDto.getRequest();

		CodeAndLanguageCodeID templateFileFormatId = new CodeAndLanguageCodeID();

		MapperUtils.mapFieldValues(templateFileFormatDto, templateFileFormatId);

		try {
			TemplateFileFormat templateFileFormat = templateFileFormatRepository
					.findByCodeAndLangCodeAndIsDeletedFalseOrIsDeletedIsNull(
							templateFileFormatRequestDto.getRequest().getCode(),
							templateFileFormatRequestDto.getRequest().getLangCode());

			if (templateFileFormat != null) {
				MetaDataUtils.setUpdateMetaData(templateFileFormatDto, templateFileFormat, false);
				templateFileFormatRepository.update(templateFileFormat);
			} else {
				throw new RequestException(TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_NOT_FOUND.getErrorCode(),
						TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_NOT_FOUND.getErrorMessage());
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(
					TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_UPDATE_EXCEPTION.getErrorCode(),
					TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_UPDATE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		return templateFileFormatId;
	}

	@Override
	public CodeResponseDto deleteTemplateFileFormat(String code) {
		List<Template> templates;
		try {
			templates = templateRepository.findAllByFileFormatCodeAndIsDeletedFalseOrIsDeletedIsNull(code);
			if (!templates.isEmpty()) {
				throw new MasterDataServiceException(
						TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_DELETE_DEPENDENCY_EXCEPTION.getErrorCode(),
						TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_DELETE_DEPENDENCY_EXCEPTION.getErrorMessage());
			}
			int updatedRows = templateFileFormatRepository.deleteTemplateFileFormat(MetaDataUtils.getContextUser(),
					LocalDateTime.now(ZoneId.of("UTC")), code);
			if (updatedRows < 1) {
				throw new RequestException(TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_NOT_FOUND.getErrorCode(),
						TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessLayerException | DataAccessException e) {
			System.out.println(e.getMessage());
			throw new MasterDataServiceException(
					TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_DELETE_EXCEPTION.getErrorCode(),
					TemplateFileFormatErrorCode.TEMPLATE_FILE_FORMAT_DELETE_EXCEPTION.getErrorMessage() + " "
							+ ExceptionUtils.parseException(e));
		}
		CodeResponseDto responseDto = new CodeResponseDto();
		responseDto.setCode(code);
		return responseDto;
	}
}
