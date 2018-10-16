package io.mosip.registration.service.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.entity.DocumentEntity;
import io.mosip.registration.repositary.DocumentRepository;
import io.mosip.registration.repositary.RegistrationRepositary;
import io.mosip.registration.service.DocumentUploadService;
import io.mosip.registration.service.impl.DocumentUploaderServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource({ "classpath:registration-application.properties" })
public class DocumentUploadServiceTest {

	@InjectMocks
	private DocumentUploadService documentUploaderServiceImpl = new DocumentUploaderServiceImpl();

	@Mock
	private DocumentRepository documentRepository;

	@Mock
	private RegistrationRepositary registrationRepositary;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private MockMultipartFile mockMultipartFile;

	DocumentDto documentDto = new DocumentDto("98745632155997", "12345678996325", "address", "POA", ".pdf", "SAVE",
			"ENG", "Kishan", "Kishan", true);
	DocumentDto documentDto2 = new DocumentDto("85697463215698", "98745632159753", "address", "POI", ".pdf", "SAVE",
			"ENG", "Rupika", "Rupika", false);
	private DocumentEntity entity;

	@Before
	public void setUp() {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sample.pdf").getFile());
		byte[] bFile = null;
		try {
			bFile = Files.readAllBytes(file.toPath());
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		entity = new DocumentEntity(1, "98745632155997", "sample.pdf", "address", "POA", ".pdf", bFile, "SAVE", "ENG",
				"Kishan", new Timestamp(System.currentTimeMillis()), "Kishan",
				new Timestamp(System.currentTimeMillis()));

		try {
			mockMultipartFile = new MockMultipartFile("sample.pdf", "sample.pdf", "mixed/multipart",
					new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}

	@Test
	public void successUpload() {
		List<DocumentEntity> list = new ArrayList<DocumentEntity>();
		list.add(entity);
		List<String> list2 = new ArrayList<String>();
		list2.add("98745632155997");
		list2.add("78996741258596");
		Mockito.when(documentRepository.save(entity)).thenReturn(entity);
		Mockito.when(registrationRepositary.findBygroupIds(ArgumentMatchers.any())).thenReturn(list2);
		Mockito.when(documentRepository.findBypreregId(ArgumentMatchers.any())).thenReturn(list);
		boolean success = documentUploaderServiceImpl.uploadDoucment(mockMultipartFile, documentDto);

		assertEquals(true, success);
	}

	@Test
	public void documentCopy() {
		DocumentEntity documentEntity = entity;
		List<DocumentEntity> list = new ArrayList<DocumentEntity>();
		list.add(entity);
		List<String> list2 = new ArrayList<String>();
		list2.add("98745632155997");
		Mockito.when(documentRepository.save(entity)).thenReturn(documentEntity);
		Mockito.when(registrationRepositary.findBygroupIds(ArgumentMatchers.any())).thenReturn(list2);
		Mockito.when(documentRepository.findBypreregId(ArgumentMatchers.any())).thenReturn(list);
		boolean success = documentUploaderServiceImpl.uploadDoucment(mockMultipartFile, documentDto2);

		assertEquals(true, success);

	}
}
