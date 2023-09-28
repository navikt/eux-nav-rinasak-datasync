package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.ResetNavRinasakDatasyncService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Unprotected
@Controller
class ResetNavRinasakController(
    val service: ResetNavRinasakDatasyncService
) {

    @GetMapping("/reset-nav-rinasak")
    fun resetCaseStoreSyncStatus(
        model: Model,
    ): String {
        return "reset-nav-rinasak"
    }

    @PostMapping("/reset-nav-rinasak")
    fun reset(
        model: Model
    ): String {
        service.reset()
        return "reset-nav-rinasak"
    }
}