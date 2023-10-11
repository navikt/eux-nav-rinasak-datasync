package eux.nav.rinasak.datasync.controller

import eux.nav.rinasak.datasync.service.util.SandboxService
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Unprotected
@Controller
class SandboxController(
    val sandboxService: SandboxService
) {
    val log: Logger = getLogger(SandboxController::class.java)

    @GetMapping("/sandbox")
    fun sandbox(
        model: Model,
    ) = try {
        model.sandbox()
        "sandbox"
    } catch (e: Exception) {
        "sandbox"
    }

    fun Model.sandbox() =
        addAttribute(
            "sandbox",
            sandboxService.sandbox()
        )
}
