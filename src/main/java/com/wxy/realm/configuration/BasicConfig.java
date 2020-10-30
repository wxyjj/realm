package com.wxy.realm.configuration;

import com.wxy.realm.support.WorkThreadFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 基本配置
 *
 * @Author wxy
 * @Date 2020/9/22 11:07
 * @Version 1.0
 */
@EnableJpaRepositories(value = {"com.wxy.realm.jpa"}, transactionManagerRef = "jpaTransactionManager")
@Configuration
public class BasicConfig implements WebMvcConfigurer {

    @Resource
    private HikariDataSource dataSource;

    /**
     * 允许跨域调用的过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //允许所有域名进行跨域调用
        config.addAllowedOrigin("*");
        //允许跨越发送cookie
        config.setAllowCredentials(true);
        //放行全部原始头信息
        config.addAllowedHeader("*");
        //允许所有请求方法跨域调用
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器BasicInterceptor
        InterceptorRegistration registration = registry.addInterceptor(new BasicInterceptor());
        //排除swagger资源
        registration.excludePathPatterns("/swagger**/**", "/webjars/**", "/doc.html", "/favicon.ico", "/error");
    }

    /**
     * 异步线程池
     */
    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
                5,
                150,
                1500,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new WorkThreadFactory("RX")
        );
    }

    /**
     * JPA事务管理器
     */
    @Bean("jpaTransactionManager")
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpa = new JpaTransactionManager();
        jpa.setDataSource(dataSource);
        jpa.setRollbackOnCommitFailure(true);
        return jpa;
    }
}
