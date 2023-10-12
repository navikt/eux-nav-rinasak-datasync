package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.navrinasak.FagsakService
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Unprotected
@Controller
class InitielleFagsakerPendingController(
    val fagsakService: FagsakService
) {
    val log: Logger = getLogger(InitielleFagsakerPendingController::class.java)

    @GetMapping("/fagsaker/pending")
    fun caseStoreRecords(
        model: Model,
    ) = try {
        model.caseStoreRecords()
        "fagsaker"
    } catch (e: Exception) {
        "fagsaker"
    }

    fun Model.caseStoreRecords() =
        addAttribute(
            "fagsaker",
            fagsakService.fagsakerPending()
        )
}
