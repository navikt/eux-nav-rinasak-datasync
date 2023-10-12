package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.navrinasak.DokumentService
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Unprotected
@Controller
class DokumenterPendingController(
    val dokumentService: DokumentService
) {
    val log: Logger = getLogger(DokumenterPendingController::class.java)

    @GetMapping("/dokumenter/pending")
    fun caseStoreRecords(
        model: Model,
    ) = try {
        model.caseStoreRecords()
        "dokumenter"
    } catch (e: Exception) {
        "dokumenter"
    }

    fun Model.caseStoreRecords() =
        addAttribute(
            "dokumenter",
            dokumentService.dokumenterPending()
        )
}
