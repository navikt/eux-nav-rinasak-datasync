package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.casestore.CaseStoreRecordsService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Unprotected
@Controller
class PopulateAllCaseStoreRecordsController(
    val caseStoreRecordsService: CaseStoreRecordsService
) {

    @GetMapping("/populate-all-case-store-records")
    fun populateAllCaseStoreRecords(
        model: Model,
    ): String {
        model.addAttribute("number", 0)
        return "populate-all-case-store-records"
    }

    @PostMapping("/populate-all-case-store-records")
    fun populateALlCaseStoreRecords(
        @RequestParam("inputNumber") inputNumber: Int?,
        model: Model
    ): String {
        val numberOfFetchedRecords = caseStoreRecordsService.populateAll()
        model.addAttribute("numberOfFetchedRecords", numberOfFetchedRecords)
        return "populate-all-case-store-records"
    }
}