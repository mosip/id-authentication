package io.mosip.registration.processor.qc.users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.qc.users.dao.QCUserInfoDao;
import io.mosip.registration.processor.qc.users.status.codes.QCUsersStatusCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v0.1/registration-processor/qc-users")
@Api(tags = "QC Users")
public class QCUsersController {
	@Autowired
	QCUserInfoDao qcUserInfoDao;
	
	@GetMapping(path = "/qcUsersList")
	@ApiOperation(value = "Get the QCUserIds", response = QCUsersStatusCode.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "QC UserIds list successfully received") })
	public ResponseEntity<List<String>> getAllQcuserIds() {
		List<String> ids = qcUserInfoDao.getAllQcuserIds();
		
		return ResponseEntity.status(HttpStatus.OK).body(ids);
	}
}
