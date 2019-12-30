package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.constant.MasterDataConstant;
import io.mosip.kernel.masterdata.constant.OrderEnum;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.ValidDocumentTypeResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.DocumentTypeExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.CodeResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocumentTypePostResponseDto;
import io.mosip.kernel.masterdata.dto.postresponse.DocumentTypePutResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.DocumentTypeService;
import io.mosip.kernel.masterdata.utils.AuditUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Document type controller with api to get list of valid document types based
 * on document category code type and language code and with api to
 * create,update and delete document types.
 * 
 * 
 * @author Uday Kumar
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@RestController
@Api(tags = { "DocumentType" })
public class DocumentTypeController {
	@Autowired
	DocumentTypeService documentTypeService;
	
	@Autowired
	AuditUtil auditUtil;

	/**
	 * 
	 * @param langCode
	 *            input from user
	 * @param documentCategoryCode
	 *            input from user
	 * @return {@link ValidDocumentTypeResponseDto}}
	 */

	@ResponseFilter
	@ApiOperation(value = "Fetch all the  valid doucment type avialbale for specific document category code ")
	@GetMapping("/documenttypes/{documentcategorycode}/{langcode}")
	public ResponseWrapper<ValidDocumentTypeResponseDto> getDoucmentTypesForDocumentCategoryAndLangCode(
			@PathVariable("langcode") String langCode,
			@PathVariable("documentcategorycode") String documentCategoryCode) {
		List<DocumentTypeDto> validDocumentTypes = documentTypeService.getAllValidDocumentType(documentCategoryCode,
				langCode);

		ResponseWrapper<ValidDocumentTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(new ValidDocumentTypeResponseDto(validDocumentTypes));
		return responseWrapper;

	}

	/**
	 * Api to create document type.
	 * 
	 * @param types
	 *            the DTO of document type.
	 * 
	 * @return {@link CodeAndLanguageCodeID }
	 */
	@PreAuthorize("hasAnyRole('GLOBAL_ADMIN')")
	@ResponseFilter
	@PostMapping("/documenttypes")
	@ApiOperation(value = "Service to create document type")
	public ResponseWrapper<DocumentTypePostResponseDto> createDocumentType(
			@Valid @RequestBody RequestWrapper<DocumentTypeDto> types) {
auditUtil.auditRequest(MasterDataConstant.CREATE_API_IS_CALLED + DocumentTypeDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.CREATE_API_IS_CALLED + DocumentTypeDto.class.getCanonicalName(), "ADM-679");
		ResponseWrapper<DocumentTypePostResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentTypeService.createDocumentType(types.getRequest()));
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_CREATE, DocumentTypeDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_CREATE_DESC, DocumentTypeDto.class.getCanonicalName()), "ADM-680");
		return responseWrapper;
	}

	/**
	 * Api to update document type. .
	 * 
	 * @param types
	 *            the DTO of document type.
	 * @return {@link CodeAndLanguageCodeID}.
	 */
	@PreAuthorize("hasAnyRole('GLOBAL_ADMIN')")
	@ResponseFilter
	@PutMapping("/documenttypes")
	@ApiOperation(value = "Service to update document type")
	public ResponseWrapper<DocumentTypePutResponseDto> updateDocumentType(
			@ApiParam("Document Type DTO to update") @Valid @RequestBody RequestWrapper<DocumentTypeDto> types) {
auditUtil.auditRequest(
				MasterDataConstant.UPDATE_API_IS_CALLED + DocumentTypeDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.UPDATE_API_IS_CALLED + DocumentTypeDto.class.getCanonicalName(), "ADM-681");
		ResponseWrapper<DocumentTypePutResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentTypeService.updateDocumentType(types.getRequest()));
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_UPDATE, DocumentTypeDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM, String.format(MasterDataConstant.SUCCESSFUL_UPDATE_DESC,
						DocumentTypeDto.class.getCanonicalName()), "ADM-682");
		return responseWrapper;
	}

	/**
	 * Api to delete document type.
	 * 
	 * @param code
	 *            the document type code.
	 * @return the code.
	 */
	@ResponseFilter
	@DeleteMapping("/documenttypes/{code}")
	@ApiOperation(value = "Service to delete document type")
	public ResponseWrapper<CodeResponseDto> deleteDocumentType(@PathVariable("code") String code) {
		ResponseWrapper<CodeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentTypeService.deleteDocumentType(code));
		return responseWrapper;
	}

	/**
	 * This controller method provides with all document types.
	 * 
	 * @param pageNumber
	 *            the page number
	 * @param size
	 *            the size of each page
	 * @param sortBy
	 *            the attributes by which it should be ordered
	 * @param orderBy
	 *            the order to be used
	 * 
	 * @return the response i.e. pages containing the document types.
	 */
	@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN')")
	@ResponseFilter
	@GetMapping("/documenttypes/all")
	@ApiOperation(value = "Retrieve all the document types with additional metadata", notes = "Retrieve all the document types with additional metadata")
	@ApiResponses({ @ApiResponse(code = 200, message = "list of document types"),
			@ApiResponse(code = 500, message = "Error occured while retrieving document types") })
	public ResponseWrapper<PageDto<DocumentTypeExtnDto>> getAllDocumentTypes(
			@RequestParam(name = "pageNumber", defaultValue = "0") @ApiParam(value = "page no for the requested data", defaultValue = "0") int pageNumber,
			@RequestParam(name = "pageSize", defaultValue = "10") @ApiParam(value = "page size for the requested data", defaultValue = "10") int pageSize,
			@RequestParam(name = "sortBy", defaultValue = "createdDateTime") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String sortBy,
			@RequestParam(name = "orderBy", defaultValue = "desc") @ApiParam(value = "order the requested data based on param", defaultValue = "desc") OrderEnum orderBy) {
		ResponseWrapper<PageDto<DocumentTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper
				.setResponse(documentTypeService.getAllDocumentTypes(pageNumber, pageSize, sortBy, orderBy.name()));
		return responseWrapper;
	}

	/**
	 * API that returns the values required for the column filter columns.
	 * 
	 * @param request
	 *            the request DTO {@link FilterResponseDto} wrapper in
	 *            {@link RequestWrapper}.
	 * @return the response i.e. the list of values for the specific filter column
	 *         name and type.
	 */
	@ResponseFilter
	@PostMapping("/documenttypes/filtervalues")
	@PreAuthorize("hasAnyRole('GLOBAL_ADMIN')")
	public ResponseWrapper<FilterResponseDto> documentTypeFilterValues(
			@RequestBody @Valid RequestWrapper<FilterValueDto> request) {
			auditUtil.auditRequest(MasterDataConstant.FILTER_API_IS_CALLED + DocumentTypeDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.FILTER_API_IS_CALLED + DocumentTypeDto.class.getCanonicalName(),"ADM-683");
		ResponseWrapper<FilterResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentTypeService.documentTypeFilterValues(request.getRequest()));
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_FILTER, DocumentTypeDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_FILTER_DESC, DocumentTypeDto.class.getCanonicalName()), "ADM-684");
		return responseWrapper;
	}

	/**
	 * Function to fetch all document types.
	 * 
	 * @return {@link DocumentTypeExtnDto} DocumentTypeResponseDto
	 */
	@ResponseFilter
	@PostMapping("/documenttypes/search")
	@PreAuthorize("hasAnyRole('GLOBAL_ADMIN')")
	public ResponseWrapper<PageResponseDto<DocumentTypeExtnDto>> searchDocumentType(
			@RequestBody @Valid RequestWrapper<SearchDto> request) {
		auditUtil.auditRequest(MasterDataConstant.SEARCH_API_IS_CALLED + DocumentTypeDto.class.getCanonicalName(),
				MasterDataConstant.AUDIT_SYSTEM,
				MasterDataConstant.SEARCH_API_IS_CALLED + DocumentTypeDto.class.getCanonicalName(), "ADM-685");
		ResponseWrapper<PageResponseDto<DocumentTypeExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentTypeService.searchDocumentTypes(request.getRequest()));
		auditUtil.auditRequest(
				String.format(MasterDataConstant.SUCCESSFUL_SEARCH, DocumentTypeDto.class.getCanonicalName()),
				MasterDataConstant.AUDIT_SYSTEM,
				String.format(MasterDataConstant.SUCCESSFUL_SEARCH_DESC, DocumentTypeDto.class.getCanonicalName(), "ADM-686"));
		return responseWrapper;
	}
	
	/**
	 * API to fetch all Document type  details based on language code
	 * 
	 * @param langCode
	 *            the language code
	 * 
	 * @return {@link DocumentTypeResponseDto}
	 */
	@ResponseFilter
	@GetMapping("/documenttypes/{langcode}")
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	public ResponseWrapper<DocumentTypeResponseDto> getAllDocumentTypeByLaguageCode(
			@PathVariable("langcode") String langCode) {
		ResponseWrapper<DocumentTypeResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(documentTypeService.getAllDocumentTypeByLaguageCode(langCode));
		return responseWrapper;
	}

}
