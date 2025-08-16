package com.example.basespringbootapikotlin.controller

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.OracleContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = [OracleTestContainerConfig.Companion.Initializer::class])
@org.springframework.test.context.ActiveProfiles("test")
abstract class OracleTestContainerConfig {
    companion object {
        @Container
        val oracle: OracleContainer = OracleContainer("gvenzl/oracle-xe:21-slim")
            .withUsername("timeforing_user")
            .withPassword("TimeTrack123")
            // No .withExposedPorts needed; Testcontainers will randomize

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(context: ConfigurableApplicationContext) {
                if (!oracle.isRunning) {
                    oracle.start()
                }
                TestPropertyValues.of(
                    "spring.datasource.url=${oracle.jdbcUrl}",
                    "spring.datasource.username=${oracle.username}",
                    "spring.datasource.password=${oracle.password}",
                    "spring.datasource.driver-class-name=oracle.jdbc.OracleDriver"
                ).applyTo(context.environment)
            }
        }
    }
}
