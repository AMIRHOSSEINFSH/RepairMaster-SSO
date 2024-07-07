package org.webproject.sso.util.authentication


import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CachingRequestBodyFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cachedBodyHttpServletRequest =
            CachedBodyHttpServletRequest(request)
        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }


}
