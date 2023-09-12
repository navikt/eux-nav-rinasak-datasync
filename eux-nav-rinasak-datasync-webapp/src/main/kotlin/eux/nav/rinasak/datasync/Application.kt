package eux.nav.rinasak.datasync

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableJwtTokenValidation(
    ignore = [
        "org.springframework",
        "org.springdoc"
    ]
)
@SpringBootApplication
@EnableConfigurationProperties(DataSourceProperties::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
