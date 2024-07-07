package org.webproject.sso.util.authentication

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/*
@Configuration
class FilterConfig {

    @Bean
    fun cachingRequestBodyFilter(): FilterRegistrationBean<CachingRequestBodyFilter> {
        val registrationBean = FilterRegistrationBean<CachingRequestBodyFilter>()
        registrationBean.filter = CachingRequestBodyFilter()
        registrationBean.addUrlPatterns("/*")
        return registrationBean
    }
}*/