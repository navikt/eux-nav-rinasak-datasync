package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.enhet.OverstyrtEnhetsnummerService
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Unprotected
@Controller
class JournalController(
    val overstyrtEnhetsnummerService: OverstyrtEnhetsnummerService,
) {

    val log = KotlinLogging.logger {}

    @GetMapping("/journal")
    fun journal(
        model: Model,
    ): String {
        model.addAttribute("number", 0)
        return "journal"
    }

    @PostMapping("/journal")
    fun journal(
        @RequestParam journalposter: String?,
        model: Model
    ): String {
        log.info { "number: $journalposter" }
        return "journal"
    }
}