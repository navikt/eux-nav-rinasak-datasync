package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.enhet.OverstyrtEnhetsnummerService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Unprotected
@Controller
class SyncOverstyrtEnhetsnummerController(
    val overstyrtEnhetsnummerService: OverstyrtEnhetsnummerService,
) {

    @GetMapping("/sync-overstyrt-enhetsnummer")
    fun populateAllCaseStoreRecords(
        model: Model,
    ): String {
        model.addAttribute("number", 0)
        return "sync-overstyrt-enhetsnummer"
    }

    @PostMapping("/sync-overstyrt-enhetsnummer")
    fun populateALlCaseStoreRecords(
        @RequestParam("inputNumber") inputNumber: Int?,
        model: Model
    ): String {
        overstyrtEnhetsnummerService.sync()
        return "sync-overstyrt-enhetsnummer"
    }
}