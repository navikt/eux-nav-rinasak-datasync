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
class PopulateNavRinasakController(
    val caseStoreRecordsService: CaseStoreRecordsService
) {

    @GetMapping("/populate-nav-rinasak")
    fun populateNavRinasak(
        model: Model,
    ): String {
        model.addAttribute("number", 0)
        return "populate-nav-rinasak"
    }

    @PostMapping("/populate-nav-rinasak")
    fun populateCaseStoreRecords(
        @RequestParam("inputNumber") inputNumber: Int?,
        model: Model
    ): String {
        val numberOfFetchedRecords = caseStoreRecordsService.populateNavRinasakStaging()
        model.addAttribute("numberOfFetchedRecords", numberOfFetchedRecords)
        return "populate-nav-rinasak"
    }
}