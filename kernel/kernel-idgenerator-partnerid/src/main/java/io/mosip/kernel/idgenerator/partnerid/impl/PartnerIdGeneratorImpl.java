package io.mosip.kernel.idgenerator.partnerid.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.PartnerIdGenerator;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.idgenerator.partnerid.constant.PartnerIdExceptionConstant;
import io.mosip.kernel.idgenerator.partnerid.constant.PartnerIdPropertyConstant;
import io.mosip.kernel.idgenerator.partnerid.entity.Partner;
import io.mosip.kernel.idgenerator.partnerid.excepion.PartnerIdException;
import io.mosip.kernel.idgenerator.partnerid.repository.PartnerRepository;

/**
 * This service class contains methods for generating PartnerId.
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Component
public class PartnerIdGeneratorImpl implements PartnerIdGenerator<String> {

	/**
	 * Length of PartnerId.
	 */
	@Value("${mosip.kernel.partnerid.length}")
	private int partnerIdLength;
	/**
	 * The reference to PartnerRepository.
	 */
	@Autowired
	PartnerRepository partnerRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idgenerator.spi.TspIdGenerator#generateId()
	 */
	@Override
	public String generateId() {

		int generatedId = 0;

		final int initialValue = MathUtils.getPow(Integer.parseInt(PartnerIdPropertyConstant.ID_BASE.getProperty()),
				partnerIdLength - 1);

		Partner entity = null;

		try {

			entity = partnerRepository.findLastTspId();

		} catch (DataAccessLayerException e) {
			throw new PartnerIdException(PartnerIdExceptionConstant.PARTNERID_FETCH_EXCEPTION.getErrorCode(),
					PartnerIdExceptionConstant.PARTNERID_FETCH_EXCEPTION.getErrorMessage(), e);
		}

		try {
			if (entity != null) {
				generatedId = entity.getTspId() + 1;
				Partner partner = new Partner();
				partner.setTspId(generatedId);
				partner.setCreatedBy("SYSTEM");
				partner.setUpdatedBy("SYSTEM");
				partner.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				partner.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				partnerRepository.create(partner);

			} else {
				entity = new Partner();
				entity.setTspId(initialValue);
				entity.setCreatedBy("SYSTEM");
				entity.setUpdatedBy("SYSTEM");
				LocalDateTime createdTime = LocalDateTime.now(ZoneId.of("UTC"));
				entity.setCreatedDateTime(createdTime);
				entity.setUpdatedDateTime(null);
				generatedId = initialValue;
				partnerRepository.create(entity);
			}

		} catch (DataAccessLayerException e) {
			if (e.getCause().getClass() == EntityExistsException.class) {
				generateId();
			} else {
				throw new PartnerIdException(PartnerIdExceptionConstant.PARTNERID_INSERTION_EXCEPTION.getErrorCode(),
						PartnerIdExceptionConstant.PARTNERID_INSERTION_EXCEPTION.getErrorMessage(), e);
			}
		}

		return String.valueOf(generatedId);

	}

}
