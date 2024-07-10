package org.webproject.sso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.webproject.sso.util.authentication.GlobalHandlerInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final GlobalHandlerInterceptor globalHandlerInterceptor;

    @Autowired
    public WebConfig(GlobalHandlerInterceptor globalHandlerInterceptor) {
        this.globalHandlerInterceptor = globalHandlerInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:63344")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalHandlerInterceptor);
    }
}

