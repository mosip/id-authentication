package io.mosip.kernel.ldap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("*").permitAll();
//        http
//                .httpBasic().and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.GET, "/moderate").hasAnyAuthority("moderator", "admin")
//                .antMatchers(HttpMethod.POST, "/moderate").hasAnyAuthority("moderator", "admin")
//                .antMatchers(HttpMethod.POST, "/admin").hasAuthority("admin");
    }

}
