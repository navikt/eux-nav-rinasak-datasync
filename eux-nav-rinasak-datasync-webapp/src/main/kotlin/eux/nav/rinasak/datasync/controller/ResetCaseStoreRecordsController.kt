package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.casestore.CaseStoreRecordsService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Unprotected
@Controller
class ResetCaseStoreRecordsController(
    val service: CaseStoreRecordsService
) {

    @GetMapping("/reset-case-store-records")
    fun resetCaseStoreRecords(
        model: Model,
    ): String {
        return "reset-case-store-records"
    }

    @PostMapping("/reset-case-store-records")
    fun reset(
        model: Model
    ): String {
        service.resetRecords()
        return "reset-case-store-records"
    }
}