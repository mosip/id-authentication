package io.mosip.kernel.masterdata.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import io.mosip.kernel.masterdata.entity.Location;

@Component
public class LocationUtils {
	ThreadLocal<List<Location>> local;
	ThreadLocal<List<Location>> list;

	public List<Location> getDescedants(List<Location> locations, Location location) {
		Objects.requireNonNull(locations);
		initialize(locations);
		list.get().add(location);
		getImmdChild(locations, location);
		return list.get();
	}

	private void initialize(List<Location> locations) {
		local = new ThreadLocal<>();
		local.set(locations);
		list = new ThreadLocal<>();
		list.set(new ArrayList<>());
	}

	private void getImmdChild(List<Location> locations, Location location) {
		locations.stream().filter(child -> isChild(child, location)).forEach(i -> {
			list.get().add(i);
			getImmdChild(local.get(), i);
		});
	}

	private void getImmdParent(List<Location> locations, Location location) {
		locations.stream().filter(parent -> isParent(parent, location)).forEach(i -> {
			list.get().add(i);
			getImmdParent(local.get(), i);
		});
	}

	public List<Location> getAncestors(List<Location> locations, Location location) {
		initialize(locations);
		list.get().add(location);
		getImmdParent(locations, location);
		return list.get();
	}

	private boolean isChild(Location child, Location parent) {
		if (child.getParentLocCode() != null)
			return child.getParentLocCode().equals(parent.getCode())
					&& child.getLangCode().equals(parent.getLangCode());
		else
			return false;
	}

	private boolean isParent(Location parent, Location child) {
		if (parent.getCode() != null)
			return parent.getCode().equals(child.getParentLocCode())
					&& child.getLangCode().equals(parent.getLangCode());
		else
			return false;
	}
}
