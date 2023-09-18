package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.CaseStoreRecordsService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Unprotected
@Controller
class CaseStoreRecordsController(
    val caseStoreRecordsService: CaseStoreRecordsService
) {

    @GetMapping("/case-store-records")
    fun stations(
        model: Model,
    ) = try {
        model.caseStoreRecords()
        "case-store-records"
    } catch (e: Exception) {
        "case-store-records-error"
    }


    fun Model.caseStoreRecords() =
        addAttribute(
            "caseStoreRecords",
            caseStoreRecordsService.cases()
        )

}
