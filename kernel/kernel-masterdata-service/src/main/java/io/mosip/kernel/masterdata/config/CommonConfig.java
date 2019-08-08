package io.mosip.kernel.masterdata.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.httpfilter.ReqResFilter;
import io.mosip.kernel.masterdata.utils.DefaultSort;
import io.mosip.kernel.masterdata.utils.Node;
import io.mosip.kernel.masterdata.utils.UBtree;

/**
 * Config class with beans for modelmapper and request logging
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class CommonConfig {

	/**
	 * Produce Request Logging bean
	 * 
	 * @return Request logging bean
	 */
	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(false);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}

	@Bean
	public FilterRegistrationBean<Filter> registerReqResFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(getReqResFilter());
		filterRegistrationBean.setOrder(2);
		return filterRegistrationBean;
	}

	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}

	@Bean(name = "zoneTree")
	public UBtree<Zone> zoneTree() {
		return zone -> new Node<>(zone.getCode(), zone, zone.getParentZoneCode());
	}

	@Bean(name = "locationTree")
	public UBtree<Location> locationTree() {
		return location -> new Node<>(location.getCode(), location, location.getParentLocCode());
	}

	@Bean
	public DefaultSort defaultSort() {
		return new DefaultSort();
	}
}
