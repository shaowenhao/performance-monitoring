package com.siemens.devops.monitoring.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration // 标明是配置类
@EnableSwagger2WebMvc
public class SwaggerConfig {
	@Value("${swagger.enabled}")
	private Boolean enabled;

	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2) // DocumentationType.SWAGGER_2 固定的，代表swagger2
//                .groupName("分布式任务系统") // 如果配置多个文档的时候，那么需要配置groupName来分组标识
				.enable(enabled).apiInfo(apiInfo()) // 用于生成API信息
				.pathMapping("/").select() // select()函数返回一个ApiSelectorBuilder实例,用来控制接口被swagger做成文档
				.apis(RequestHandlerSelectors.basePackage("com.siemens.devops.monitoring.controller")) // 用于指定扫描哪个包下的接口
				.paths(PathSelectors.any())// 选择所有的API,如果你想只为部分API生成文档，可以配置这里
				.build();
	}

	/**
	 * 用于定义API主界面的信息，比如可以声明所有的API的总标题、描述、版本
	 * 
	 * @return
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Semantic Monitoring API Document") // 可以用来自定义API的主标题
				.description("Semantic Monitoring API") // 可以用来描述整体的API
				.termsOfServiceUrl("") // 用于定义服务的域名
				.version("1.0") // 可以用来定义版本。
				.build(); //
	}

//	@Bean
//	public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
//			ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
//			EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
//			WebEndpointProperties webEndpointProperties, Environment environment) {
//		List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
//		Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
//		allEndpoints.addAll(webEndpoints);
//		allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
//		allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
//		String basePath = webEndpointProperties.getBasePath();
//		EndpointMapping endpointMapping = new EndpointMapping(basePath);
//		boolean shouldRegisterLinksMapping = webEndpointProperties.getDiscovery().isEnabled()
//				&& (org.springframework.util.StringUtils.hasText(basePath)
//						|| ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
//		return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
//				corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
//				shouldRegisterLinksMapping, null);
//	}

	@Bean
	public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
		return new BeanPostProcessor() {

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof WebMvcRequestHandlerProvider) {
					customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
				}
				return bean;
			}

			private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(
					List<T> mappings) {
				List<T> copy = mappings.stream().filter(mapping -> mapping.getPatternParser() == null)
						.collect(Collectors.toList());
				mappings.clear();
				mappings.addAll(copy);
			}

			@SuppressWarnings("unchecked")
			private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
				try {
					Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
					field.setAccessible(true);
					return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			}
		};
	}
}
