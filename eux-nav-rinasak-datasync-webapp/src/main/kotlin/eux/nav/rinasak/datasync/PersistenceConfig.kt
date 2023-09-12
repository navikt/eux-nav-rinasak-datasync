package eux.nav.rinasak.datasync

import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PersistenceConfig(
    val dataSourceProperties: DataSourceProperties
) {

    @Bean
    fun hikariDataSource() = HikariDataSource(dataSourceProperties.hikari)
}
