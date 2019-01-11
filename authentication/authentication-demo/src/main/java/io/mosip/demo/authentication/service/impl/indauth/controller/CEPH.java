package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;

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

	@PostMapping(path = "/jsonCompress", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String jsonCompress(@RequestBody ObjectNode json) {
		ImmutableMap<CharSequence, CharSequence> map = ImmutableMap.<CharSequence, CharSequence>of("\"", "\\\"", "\\",
				"\\\\");
		AggregateTranslator escaper = new AggregateTranslator(new LookupTranslator(map),
				new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE));
		return escaper.translate(json.toString());
	}

	@PostMapping(path = "/jsonDecompress", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String jsonDecompress(@RequestBody String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
				mapper.readValue(StringEscapeUtils.unescapeJava(json).getBytes(Charset.forName("UTF-16")), ObjectNode.class));
	}

}
