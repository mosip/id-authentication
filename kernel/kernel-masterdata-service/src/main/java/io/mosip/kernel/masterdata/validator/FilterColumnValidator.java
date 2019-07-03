package io.mosip.kernel.masterdata.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.masterdata.constant.ValidationErrorCode;
import io.mosip.kernel.masterdata.dto.request.FilterDto;
import io.mosip.kernel.masterdata.exception.ValidationException;

/**
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
public class FilterColumnValidator {
	private static final String TYPE_FIELD = "type";

	public <T> boolean validate(Class<T> target, List<FilterDto> filters) {
		List<ServiceError> errors = new ArrayList<>();
		if (filters != null && !filters.isEmpty()) {
			for (FilterDto filter : filters) {
				validateFilterColumn(target, errors, filter);
			}
		}
		if (!errors.isEmpty())
			throw new ValidationException(errors);

		return true;
	}

	private <T> void validateFilterColumn(Class<T> target, List<ServiceError> errors, FilterDto filter) {
		try {
			if (validateFilterColumnType(filter.getType(), filter.getType())) {
				Field field = target.getDeclaredField(TYPE_FIELD);
				if (!containsFilterColumn(field, filter.getType())) {
					errors.add(new ServiceError(ValidationErrorCode.FILTER_COLUMN_NOT_SUPPORTED.getErrorCode(),
							String.format(ValidationErrorCode.FILTER_COLUMN_NOT_SUPPORTED.getErrorMessage(),
									filter.getType())));
				}
			} else {
				errors.add(new ServiceError(ValidationErrorCode.NO_FILTER_COLUMN_FOUND.getErrorCode(),
						String.format(ValidationErrorCode.NO_FILTER_COLUMN_FOUND.getErrorMessage(), filter.getType())));
			}
		} catch (NoSuchFieldException | SecurityException e) {
			errors.add(new ServiceError(ValidationErrorCode.FILTER_COLUMN_DOESNT_EXIST.getErrorCode(),
					String.format(ValidationErrorCode.FILTER_COLUMN_DOESNT_EXIST.getErrorMessage(), filter.getType())));
		}
	}

	public boolean containsFilterColumn(Field field, String filterColumn) {
		if (field.isAnnotationPresent(FilterColumn.class)) {
			FilterColumn annotation = field.getAnnotation(FilterColumn.class);
			FilterColumnEnum[] columns = annotation.columns();
			return Arrays.stream(columns).map(FilterColumnEnum::toString).map(String::toLowerCase)
					.collect(Collectors.toList()).contains(filterColumn.toLowerCase());
		}
		return false;
	}

	private boolean validateFilterColumnType(String column, String filterType) {
		return column != null && !column.isEmpty() && filterType != null && !filterType.isEmpty();
	}
}
