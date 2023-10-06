package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.CaseStoreRecordsService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Unprotected
@Controller
class SendToNavRinasakController(
    val caseStoreRecordsService: CaseStoreRecordsService
) {

    @GetMapping("/send-to-nav-rinasak")
    fun sendToNavRinasak(
        model: Model,
    ): String {
        model.addAttribute("number", 0)
        return "send-to-nav-rinasak"
    }

    @PostMapping("/send-to-nav-rinasak")
    fun sendToNavRinasak(
        @RequestParam("inputNumber") inputNumber: Int?,
        model: Model
    ): String {
        val numberOfFetchedRecords = caseStoreRecordsService.populateAll()
        model.addAttribute("numberOfFetchedRecords", numberOfFetchedRecords)
        return "send-to-nav-rinasak"
    }
}