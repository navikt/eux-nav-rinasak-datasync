package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.CaseStoreRecordsService
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Unprotected
@Controller
class CaseStoreRecordsController(
    val caseStoreRecordsService: CaseStoreRecordsService
) {
    private val log: Logger = LoggerFactory.getLogger(CaseStoreRecordsController::class.java)

    @GetMapping("/case-store-records")
    fun stations(
        model: Model,
    ) = try {
        model.caseStoreRecords()
        "case-store-records"
    } catch (e: Exception) {
        log.error("feilet mot case store", e)
        print(e)
        "case-store-records-error"
    }

    fun Model.caseStoreRecords() =
        addAttribute(
            "caseStoreRecords",
            caseStoreRecordsService.cases()
        )

}
