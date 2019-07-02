package io.mosip.kernel.masterdata.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.masterdata.constant.ValidationErrorCode;
import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.exception.ValidationException;

/**
 * Validator class for validating the search filter against the
 * {@link FilterType} annotation.
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Component
public class FilterTypeValidator {

	/**
	 * Method to validate that the filter supports the search type
	 * 
	 * @param target
	 *            the class to be with filter to be validated
	 * @param filters
	 *            list of {@link SearchFilter}
	 * @return true if successful false otherwise
	 */
	public <T> boolean validate(Class<T> target, List<SearchFilter> filters) {
		List<ServiceError> errors = new ArrayList<>();
		if (filters != null && !filters.isEmpty()) {
			for (SearchFilter filter : filters) {
				validateFilter(target, errors, filter);
			}
		}
		if (!errors.isEmpty())
			throw new ValidationException(errors);

		return true;
	}

	private <T> void validateFilter(Class<T> target, List<ServiceError> errors, SearchFilter filter) {
		try {
			if (validateColumnAndTypes(filter.getColumnName(), filter.getType())) {
				Field field = target.getDeclaredField(filter.getColumnName());
				if (!containsFilter(field, filter.getType())) {
					errors.add(new ServiceError(ValidationErrorCode.FILTER_NOT_SUPPORTED.getErrorCode(),
							String.format(ValidationErrorCode.FILTER_NOT_SUPPORTED.getErrorMessage(),
									filter.getColumnName(), filter.getType())));
				}
			} else {
				errors.add(new ServiceError(ValidationErrorCode.NO_FILTER_FOUND.getErrorCode(),
						String.format(ValidationErrorCode.NO_FILTER_FOUND.getErrorMessage(), filter.getColumnName())));
			}
		} catch (NoSuchFieldException | SecurityException e) {
			errors.add(new ServiceError(ValidationErrorCode.COLUMN_DOESNT_EXIST.getErrorCode(),
					String.format(ValidationErrorCode.COLUMN_DOESNT_EXIST.getErrorMessage(), filter.getColumnName())));
		}
	}

	/**
	 * Check the filter is supported.
	 * 
	 * @param field
	 *            column field
	 * @param filterType
	 *            type of the filter
	 * @return true if supported else otherwise
	 */
	public boolean containsFilter(Field field, String filterType) {
		if (field.isAnnotationPresent(FilterType.class)) {
			FilterType annotation = field.getAnnotation(FilterType.class);
			FilterTypeEnum[] types = annotation.types();
			return Arrays.stream(types).map(FilterTypeEnum::toString).map(String::toLowerCase)
					.collect(Collectors.toList()).contains(filterType.toLowerCase());
		}
		return false;
	}

	/**
	 * Validate the input column and types is not either null or emptry
	 * 
	 * @param column
	 *            column name
	 * @param filterType
	 *            search type
	 * @return
	 */
	private boolean validateColumnAndTypes(String column, String filterType) {
		return column != null && !column.isEmpty() && filterType != null && !filterType.isEmpty();
	}
}
