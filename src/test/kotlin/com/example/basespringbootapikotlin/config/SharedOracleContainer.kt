package com.example.basespringbootapikotlin.config

import org.testcontainers.containers.OracleContainer

object SharedOracleContainer {
    val oracle: OracleContainer by lazy {
        OracleContainer("gvenzl/oracle-xe:21-slim")
            .withUsername("timeforing_user")
            .withPassword("TimeTrack123")
    }
}
