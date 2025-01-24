package com.schooldevops

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@EnableAutoConfiguration(
//    exclude = [
//        DataSourceAutoConfiguration::class,
//        SecurityAutoConfiguration::class,
//        UserDetailsServiceAutoConfiguration::class,
//        MailSenderAutoConfiguration::class,
//        FlywayAutoConfiguration::class,
//    ]
)
@ComponentScan(
    basePackages = ["com"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ["com\\.schooldevops\\.spring_batch\\..*"]
        )
    ]
)
@ConfigurationPropertiesScan("common","com")
@SpringBootConfiguration
class BatchApplication

fun main(args: Array<String>) {
    // Spring Boot Application 실행
    runApplication<BatchApplication>(*args)
}