package io.mosip.admin.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.masterdata.dto.MasterDataColumnDto;
import io.mosip.admin.masterdata.service.MasterDataColumnService;
import io.mosip.kernel.core.http.ResponseWrapper;

@RestController
public class MasterDataColumnController {
	
	@Autowired
	MasterDataColumnService masterdataService;
	
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','REGISTRATION_ADMIN')")
	@GetMapping(value = "/mastercolumns/{resource}")
	public ResponseWrapper<MasterDataColumnDto> getMasterDataColumns(@PathVariable("resource")String resource){
		return masterdataService.getMasterDataColumns(resource);
	}

}
