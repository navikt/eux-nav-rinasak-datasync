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
class PopulateCaseStoreRecordsController(
    val caseStoreRecordsService: CaseStoreRecordsService
) {

    @GetMapping("/populate-case-store-records")
    fun stations(
        model: Model,
    ): String {
        model.addAttribute("number", 0)
        return "populate-case-store-records"
    }

    @PostMapping("/populate-case-store-records")
    fun populateCaseStoreRecords(
        @RequestParam("inputNumber") inputNumber: Int?,
        model: Model
    ): String {
        val numberOfFetchedRecords = caseStoreRecordsService.populateNext()
        model.addAttribute("numberOfFetchedRecords", numberOfFetchedRecords)
        return "populate-case-store-records"
    }
}