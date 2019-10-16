package io.mosip.kernel.masterdata.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.mosip.kernel.masterdata.constant.MasterdataSearchErrorCode;
import io.mosip.kernel.masterdata.dto.request.SearchSort;
import io.mosip.kernel.masterdata.entity.BaseEntity;
import io.mosip.kernel.masterdata.exception.RequestException;

/**
 * {@link SortUtils} use to sort the list based on the sort criteria this class
 * support multiple field sorting
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public class SortUtils {

	/**
	 * Method to sort the list based on the sorting parameter support mutliple sort
	 * criteria
	 * 
	 * @param <T>
	 *            list generic type
	 * @param list
	 *            input to be sorted
	 * @param sortCriteria
	 *            sorting criteria
	 * @return sorted list
	 */
	public <T> List<T> sort(List<T> list, List<SearchSort> sortCriteria) {
		List<Field> fields = null;
		if (toBeSorted(list, sortCriteria)) {
			T data = list.get(0);
			fields = extractField(data);
			FieldComparator<T> comparator = null;
			for (int i = 0; i < sortCriteria.size(); i++) {
				SearchSort sort = sortCriteria.get(i);
				if (i == 0) {
					comparator = new FieldComparator<>(findField(fields, sort.getSortField()), sort);
				} else {
					comparator.thenComparing(new FieldComparator<>(findField(fields, sort.getSortField()), sort));
				}
			}
			return list.parallelStream().sorted(comparator).collect(Collectors.toList());
		}
		return list;
	}

	/**
	 * Method to verify sorting criteria and the list to be sorted are present
	 * 
	 * @param <T>
	 *            generic list type
	 * @param list
	 *            input list
	 * @param sortCriteria
	 *            sort criteria input
	 * @return true if need sorting, false otherwise
	 */
	private <T> boolean toBeSorted(List<T> list, List<SearchSort> sortCriteria) {
		return (list != null && !list.isEmpty() && sortCriteria != null && !sortCriteria.isEmpty());
	}

	/**
	 * Method to extract the fields
	 * 
	 * @param <T>
	 *            generic type
	 * @param clazz
	 *            input class
	 * @return {@link List} of {@link Field} for the input along with super class
	 *         {@link Field}
	 */
	private <T> List<Field> extractField(T clazz) {
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(clazz.getClass().getDeclaredFields()));
		if (clazz.getClass().getSuperclass() != Object.class) {
			fields.addAll(Arrays.asList(clazz.getClass().getSuperclass().getDeclaredFields()));
		}
		return fields;
	}

	private <T extends BaseEntity> List<Field> extractEntityFields(Class<T> clazz) {
		List<Field> fields = new ArrayList<>();
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
		return fields;

	}

	private Field findField(List<Field> fields, String name) {
		Optional<Field> field = fields.stream().filter(f -> f.getName().equalsIgnoreCase(name)).findFirst();
		if (field.isPresent()) {
			return field.get();
		} else {
			throw new RequestException(MasterdataSearchErrorCode.INVALID_SORT_FIELD.getErrorCode(),
					String.format(MasterdataSearchErrorCode.INVALID_SORT_FIELD.getErrorMessage(), name));

		}
	}

	/**
	 * @param clazz
	 *            - generic class
	 * @param searchSorts
	 *            - {@link SearchSort}
	 */
	public <T extends BaseEntity> void validateSortField(Class<T> clazz, List<SearchSort> searchSorts) {
		List<Field> fields = extractEntityFields(clazz);
		for (SearchSort searchSort : searchSorts) {
			findField(fields, searchSort.getSortField());
		}

	}

}
