package io.mosip.registration.test.util;

import org.junit.Assert;
import org.junit.Test;

import io.mosip.registration.builder.Builder;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;

public class BuilderTest {

	@Test
	public void buildDocumentDTOTest() {
		DocumentDetailsDTO detailsDTO = Builder.build(DocumentDetailsDTO.class)
				.with(document -> document.setType("category")).with(document -> document.setFormat("pdf"))
				.with(document -> document.setOwner("self")).get();

		Assert.assertNotNull(detailsDTO);
		Assert.assertEquals("category", detailsDTO.getType());
		Assert.assertEquals("pdf", detailsDTO.getFormat());
		Assert.assertEquals("self", detailsDTO.getOwner());
	}

	@Test
	public void buildException() throws Exception {
		Class<?> clazz = Class.class;
		new Builder<>(clazz);
	}

}
