package com.jay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication  //@SpringBootApplication = (默认属性)@Configuration + @EnableAutoConfiguration + @ComponentScan。
public class ToutiaoApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ToutiaoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ToutiaoApplication.class, args);
	}
}
