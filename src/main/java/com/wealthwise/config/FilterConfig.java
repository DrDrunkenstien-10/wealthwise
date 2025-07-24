package com.wealthwise.config;

import com.wealthwise.logging.TraceIdFilter;
import com.wealthwise.user.service.CurrentUserProvider;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> traceIdFilter(CurrentUserProvider currentUserProvider) {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter(currentUserProvider));
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}
