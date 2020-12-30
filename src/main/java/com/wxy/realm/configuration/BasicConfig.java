package com.wxy.realm.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    @Resource
    private BasicInterceptor basicInterceptor;

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
     * JPA事务管理器
     */
    @Bean("jpaTransactionManager")
    public JpaTransactionManager jpaTransactionManager() {
        JpaTransactionManager jpa = new JpaTransactionManager();
        jpa.setDataSource(dataSource);
        jpa.setRollbackOnCommitFailure(true);
        return jpa;
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> patterns = new ArrayList<>();
        //swagger配置过滤
        patterns.add("/doc.html");
        patterns.add("/backstage/**");
        patterns.add("/webjars/**");
        patterns.add("/swagger-resources/**");
        patterns.add("/favicon.ico");
        patterns.add("/error");
        registry.addInterceptor(basicInterceptor).addPathPatterns("/**").excludePathPatterns(patterns);
    }

    /**
     * 消息转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //调用父类的配置
        WebMvcConfigurer.super.configureMessageConverters(converters);
        //必须保证FastJson的消息转换器先于Jackson的消息转换器注册
        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        //创建FastJson的消息转换器
        FastJsonHttpMessageConverter convert = new FastJsonHttpMessageConverter();
        //创建FastJson的配置对象
        FastJsonConfig config = new FastJsonConfig();
        //对Json数据进行格式化
        config.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteMapNullValue,
                //禁止循环引用
                SerializerFeature.DisableCircularReferenceDetect);
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        config.setCharset(StandardCharsets.UTF_8);
        convert.setFastJsonConfig(config);
        convert.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        converters.add(convert);
    }

}
