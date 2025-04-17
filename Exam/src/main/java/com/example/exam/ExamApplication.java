package com.example.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@MapperScan("com.example.exam.mapper")
public class ExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<Filter> contentFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter((request, response, chain) -> {
            // 修复可能被修改的Authorization头
            String authHeader = ((HttpServletRequest) request).getHeader("Authorization");
            if (authHeader != null) {
                ((HttpServletRequest) request).setAttribute(
                        "ORIG_AUTH_HEADER",
                        authHeader.replaceAll("\\s+", " ").trim()
                );
            }
            chain.doFilter(request, response);
        });
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}