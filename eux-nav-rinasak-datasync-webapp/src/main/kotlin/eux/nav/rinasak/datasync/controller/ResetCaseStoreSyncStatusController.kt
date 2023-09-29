package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.CaseStoreSyncStatusResetService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Unprotected
@Controller
class ResetCaseStoreSyncStatusController(
    val service: CaseStoreSyncStatusResetService
) {

    @GetMapping("/reset-case-store-sync-status")
    fun resetCaseStoreSyncStatus(
        model: Model,
    ): String {
        return "reset-case-store-sync-status"
    }

    @PostMapping("/reset-case-store-sync-status")
    fun reset(
        model: Model
    ): String {
        service.reset()
        return "reset-case-store-sync-status"
    }
}