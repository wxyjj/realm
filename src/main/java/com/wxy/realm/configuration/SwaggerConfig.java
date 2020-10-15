package com.wxy.realm.configuration;

import cn.hutool.core.net.NetUtil;
import com.wxy.realm.properties.SwaggerProp;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import java.util.*;

/**
 * swagger配置
 *
 * @Author wxy
 * @Date 2020/9/21 17:04
 * @Version 1.0
 */
@Configuration
@EnableOpenApi
@Slf4j
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;
    @Resource
    private SwaggerProp swaggerProp;

    @Bean
    public Docket createRestApi() {
        log.info("项目启动成功！接口文档地址: http://" + NetUtil.getLocalhost().getHostAddress() + ":" + serverPort + "/doc.html");
        return new Docket(DocumentationType.OAS_30).pathMapping("/")
                // 定义是否开启swagger，false为关闭，可以通过变量控制
                .enable(swaggerProp.getEnable())
                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                // 接口调试地址
                .host(swaggerProp.getTryHost())
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))
                // 授权信息设置，必要的header token等认证信息
                .securitySchemes(securitySchemes())
                // 授权信息全局应用
                .securityContexts(securityContexts());
    }

    /**
     * API 页面上半部分展示信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProp.getApplicationName() + " Api Doc")
                .description(swaggerProp.getApplicationDescription())
                .contact(new Contact("wxy", "https://github.com/wxyjj", "1083762642@qq.com"))
                .version(
                        "Application Version: " + swaggerProp.getApplicationVersion()
                                + ", Spring Boot Version: " + SpringBootVersion.getVersion()
                )
                .build();
    }

    /**
     * 设置授权信息
     */
    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("token", "token", In.HEADER.toValue());
        return Collections.singletonList(apiKey);
    }

    /**
     * 授权信息全局应用
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(
                                Collections.singletonList(
                                        new SecurityReference(
                                                "token",
                                                new AuthorizationScope[]{
                                                        new AuthorizationScope("global", "")
                                                }
                                        )
                                )
                        )
                        .build()
        );
    }

    @SafeVarargs
    private final <T> Set<T> newHashSet(T... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }


}
