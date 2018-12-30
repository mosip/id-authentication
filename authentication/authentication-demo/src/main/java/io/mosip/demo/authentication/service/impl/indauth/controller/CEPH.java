package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import io.mosip.kernel.core.util.CryptoUtil;

@RestController
public class CEPH {
	
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	    multipartResolver.setMaxUploadSize(100000);
	    return multipartResolver;
	}

	@PostMapping(value = "/encodeFile", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String encodeFile(@RequestPart MultipartFile file) throws IOException {
		return CryptoUtil.encodeBase64(file.getBytes());
	}

	@PostMapping(path = "/decodeFile", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> decodeToFile(@RequestBody String stringToDecode,
			@RequestParam String fileName) {
		byte[] decodedFileData = CryptoUtil.decodeBase64(stringToDecode);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Content-Disposition", "attachment; filename=" + fileName);
		headers.add("Expires", "0");
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(decodedFileData));
		return ResponseEntity.ok().headers(headers).contentLength(decodedFileData.length)
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}

	// @GetMapping("/ceph")
	// public String result() throws IOException,
	// io.mosip.kernel.core.exception.IOException {
	// PathMatchingResourcePatternResolver resolver = new
	// PathMatchingResourcePatternResolver(
	// getClass().getClassLoader());
	// Resource resources = resolver.getResource("classpath:testfile");
	// System.err.println(IOUtils.toString(resources.getInputStream(), "UTF-8"));
	// System.err.println(util.getConnection().doesBucketExistV2("1234567890"));
	// System.err.println(
	// util.getConnection().putObject("1234567890", "doc/testfile",
	// resources.getInputStream(), null));
	// System.err.println(ceph.checkFileExistence("1234567890", "doc/testfile"));
	// System.err.println(util.getConnection().listObjectsV2("524013657059").getObjectSummaries().get(0).getKey());
	// return ceph.checkFileExistence("1234567890", "doc/testfile").toString();
	// }
}
