package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.model.CaseStoreRecord
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Unprotected
@Controller
class PopulateCaseStoreRecordsController {

    @GetMapping("/populate-case-store-records")
    fun stations(
        model: Model,
    ): String {
        model.addAttribute("number", 0)
        return "populate-case-store-records"
    }

    @PostMapping("/populate-case-store-records")
    fun updateNumber(
        @RequestParam("inputNumber") inputNumber: Int?,
        model: Model
    ): String {
        val number = inputNumber ?: 0
        model.addAttribute("number", number)
        return "populate-case-store-records"
    }
}