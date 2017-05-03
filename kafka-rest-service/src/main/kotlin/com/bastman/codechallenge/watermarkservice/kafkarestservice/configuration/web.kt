package com.bastman.codechallenge.watermarkservice.kafkarestservice.configuration

import io.undertow.Undertow
import io.undertow.UndertowOptions
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.BasicAuth
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
open class RouterConfiguration {
    @Bean
    open fun forwardIndexPagesToSwaggerDoc(): WebMvcConfigurerAdapter {
        return object : WebMvcConfigurerAdapter() {

            override fun configurePathMatch(configurer: PathMatchConfigurer) {
                configurer.isUseSuffixPatternMatch = false
            }

            override fun addViewControllers(registry: ViewControllerRegistry) {
                registry.addViewController("/").setViewName("redirect:${SpringConstants.API_ROUTE_SWAGGER_UI}")
                registry.addViewController("/index.html").setViewName("redirect:${SpringConstants.API_ROUTE_SWAGGER_UI}")
            }
        }
    }
}

@Configuration
@EnableSwagger2()
open class SwaggerConfiguration {

    private val apiInfo by lazy {
        ApiInfoBuilder()
                .title("Code Challenge: Watermark Service (Kafka)")
                .description("Showcase an async (queued) processing pipeline. Distributed via Kafka :)")
                .build()
    }

    @Bean
    open fun watermarkServiceDocket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .groupName("watermark-service-kafka")
                .apiInfo(apiInfo)
                .select()
                .apis(
                        RequestHandlerSelectors.basePackage(
                                "${SpringConstants.BASE_PACKAGE}.api"
                        )
                ).build()
                .securitySchemes(
                        arrayListOf(
                                BasicAuth("basic-auth-realm")
                        )
                )
    }
}

@Configuration
open class HttpCacheConfiguration {
    @Bean
    open fun shallowEtagHeaderFilter(): ShallowEtagHeaderFilter = ShallowEtagHeaderFilter()
}

@Configuration
open class UndertowServerConfiguration {

    class BuilderCustomizer : UndertowBuilderCustomizer {
        override fun customize(builder: Undertow.Builder) {
            builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
        }
    }

    @Bean
    open fun embeddedServletContainerFactory(): UndertowEmbeddedServletContainerFactory =
            UndertowEmbeddedServletContainerFactory().apply { addBuilderCustomizers(BuilderCustomizer()) }

}

