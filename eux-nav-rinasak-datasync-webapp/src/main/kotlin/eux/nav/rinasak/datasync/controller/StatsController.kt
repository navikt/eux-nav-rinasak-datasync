package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.StatsService
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Unprotected
@Controller
class StatsController(
    val statsService: StatsService
) {
    val log: Logger = getLogger(StatsController::class.java)

    @GetMapping("/stats")
    fun caseStoreRecords(
        model: Model,
    ) = try {
        model.caseStoreRecords()
        "stats"
    } catch (e: Exception) {
        log.error("feilet mot case store", e)
        print(e)
        "stats"
    }

    fun Model.caseStoreRecords() =
        addAttribute(
            "stats",
            statsService.stats()
        )
}
