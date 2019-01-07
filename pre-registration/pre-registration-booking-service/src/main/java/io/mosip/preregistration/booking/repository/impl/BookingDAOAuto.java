package io.mosip.preregistration.booking.repository.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;

public class BookingDAOAuto {
	
	@Autowired
	private BookingAvailabilityRepository availabilityRepository;
	
	public List<AvailibityEntity> availability(String regcntrId, LocalDate regDate){
		
		List<AvailibityEntity> availabilityList=new ArrayList<>();
		try {
			availabilityList=availabilityRepository.
								findByRegcntrIdAndRegDateOrderByFromTimeAsc(regcntrId, regDate);
		} catch (DataAccessLayerException e) {
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		}
		
		
		return availabilityList;
		
	}

}
