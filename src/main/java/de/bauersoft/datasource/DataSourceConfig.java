package de.bauersoft.datasource;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	@Bean
	@ConfigurationProperties("spring.datasource.hikari")
	HikariDataSource dataSource()
	{
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}
}