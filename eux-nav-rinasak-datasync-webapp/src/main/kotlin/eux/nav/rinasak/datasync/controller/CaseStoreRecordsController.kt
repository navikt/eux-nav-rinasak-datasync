package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.casestore.CaseStoreRecordsService
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Unprotected
@Controller
class CaseStoreRecordsController(
    val caseStoreRecordsService: CaseStoreRecordsService
) {
    val log: Logger = getLogger(CaseStoreRecordsController::class.java)

    @GetMapping("/case-store-records")
    fun caseStoreRecords(
        model: Model,
    ) = try {
        model.caseStoreRecords()
        "case-store-records"
    } catch (e: Exception) {
        "case-store-records"
    }

    fun Model.caseStoreRecords() =
        addAttribute(
            "caseStoreRecords",
            caseStoreRecordsService.cases()
        )
}
