package io.mosip.kernel.masterdata.service;

import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineTypeExtnDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

/**
 * This interface provides methods to do CRUD operations on MachineType.
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface MachineTypeService {

	/**
	 * Abstract method to save Machine Type Details to the Database
	 * 
	 * @param machineType
	 *            machineType DTO
	 * 
	 * @return CodeAndLanguageCodeID returning code and language code
	 *         {@link CodeAndLanguageCodeID}
	 * 
	 * @throws MasterDataServiceException
	 *             if any error occurred while saving Machine Type
	 */
	public CodeAndLanguageCodeID createMachineType(MachineTypeDto machineType);

	/**
	 * Method to get all machine types
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param pageSize
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the machine types
	 */
	public PageDto<MachineTypeExtnDto> getAllMachineTypes(int pageNumber, int pageSize, String sortBy, String orderBy);

	/**
	 * Method to search Machine Type.
	 * 
	 * @param dto
	 *            the search DTO.
	 * @return the {@link MachineTypeExtnDto}.
	 */
	public PageResponseDto<MachineTypeExtnDto> searchMachineType(SearchDto dto);

	/**
	 * Method to filter Machine Types based on column and type provided.
	 * 
	 * @param filterValueDto
	 *            the filter DTO.
	 * @return the {@link FilterResponseDto}.
	 */
	public FilterResponseDto machineTypesFilterValues(FilterValueDto filterValueDto);
}
