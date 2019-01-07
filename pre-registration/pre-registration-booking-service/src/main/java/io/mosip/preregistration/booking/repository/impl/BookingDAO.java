package io.mosip.preregistration.booking.repository.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;

//@Component
public class BookingDAO implements BookingAvailabilityRepository{

	@Override
	public AvailibityEntity create(AvailibityEntity arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AvailibityEntity> createNamedQuerySelect(String arg0, Class<AvailibityEntity> arg1,
			Map<String, Object> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int createNamedQueryUpdateOrDelete(String arg0, Class<AvailibityEntity> arg1, Map<String, Object> arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<AvailibityEntity> createQuerySelect(String arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AvailibityEntity> createQuerySelect(String arg0, Map<String, Object> arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int createQueryUpdateOrDelete(String arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String delete(Class<AvailibityEntity> arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AvailibityEntity> findAll(Class<AvailibityEntity> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AvailibityEntity findById(Class<AvailibityEntity> arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AvailibityEntity update(AvailibityEntity arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AvailibityEntity> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AvailibityEntity> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AvailibityEntity> findAllById(Iterable<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AvailibityEntity> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends AvailibityEntity> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteInBatch(Iterable<AvailibityEntity> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAllInBatch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AvailibityEntity getOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AvailibityEntity> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AvailibityEntity> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AvailibityEntity> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AvailibityEntity> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AvailibityEntity> findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteById(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(AvailibityEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends AvailibityEntity> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends AvailibityEntity> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AvailibityEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AvailibityEntity> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends AvailibityEntity> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<AvailibityEntity> findByRegcntrIdAndRegDateOrderByFromTimeAsc(String regcntrId, LocalDate regDate) {

		List<AvailibityEntity> availibilityList = new ArrayList<>();
		try {

			availibilityList = this.findByRegcntrIdAndRegDateOrderByFromTimeAsc(regcntrId, regDate);

		} catch (DataAccessLayerException e) {
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		}
		return availibilityList;
	}

	@Override
	public List<LocalDate> findDate(String regcntrId, LocalDate fromDate, LocalDate toDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AvailibityEntity findByFromTimeAndToTimeAndRegDateAndRegcntrId(LocalTime slotFromTime, LocalTime slotToTime,
			LocalDate regDate, String regcntrd) {
		// TODO Auto-generated method stub
		return null;
	}

}
