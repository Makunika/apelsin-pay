package ru.pshiblo.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/WEB-INF/view/react/build/static/");
        registry.addResourceHandler("/favicon/**")
                .addResourceLocations("classpath:/WEB-INF/view/react/build/favicon/");
        registry.addResourceHandler("/*.js")
                .addResourceLocations("classpath:/WEB-INF/view/react/build/");
        registry.addResourceHandler("/*.json")
                .addResourceLocations("classpath:/WEB-INF/view/react/build/");
        registry.addResourceHandler("/*.ico")
                .addResourceLocations("classpath:/WEB-INF/view/react/build/");
        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:/WEB-INF/view/react/build/index.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedOrigins("*")
                .maxAge(36000L)
                .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name());
    }
}
