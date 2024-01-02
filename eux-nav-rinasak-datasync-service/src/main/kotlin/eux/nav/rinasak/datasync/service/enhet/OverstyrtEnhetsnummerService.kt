package eux.nav.rinasak.datasync.service.enhet

import eux.nav.rinasak.datasync.integration.navrinasak.NavRinasakClient
import eux.nav.rinasak.datasync.integration.navrinasak.NavRinasakPatchType
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus.FAILED
import eux.nav.rinasak.datasync.model.SyncStatus.SYNCED
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.stereotype.Service

@Service
class OverstyrtEnhetsnummerService(
    val navRinasakRepository: NavRinasakRepository,
    val navRinasakClient: NavRinasakClient
) {
    val log = logger {}

    fun sync() {
        navRinasakRepository
            .findByOverstyrtEnhetsnummerSyncStatusIsNull()
            .also { log.info { "Pending overstyrt enhetsnummer by null: ${it.size}" } }
            .filterNot { it.harRiktigOverstyrtEnhetsnummer() }
            .also { log.info { "Har ikke riktig enhetsnummer: ${it.size}" } }
            .forEach { it.oppdaterEnhetsnummer() }
    }

    fun NavRinasak.harRiktigOverstyrtEnhetsnummer(): Boolean {
        val eksisterende = navRinasakClient.finnNavRinasakOrNull(rinasakId)
        return eksisterende?.overstyrtEnhetsnummer == overstyrtEnhetsnummer
    }

    fun NavRinasak.oppdaterEnhetsnummer() {
        try {
            navRinasakClient.oppdater(
                NavRinasakPatchType(
                    rinasakId = rinasakId,
                    overstyrtEnhetsnummer = overstyrtEnhetsnummer
                )
            )
            navRinasakRepository.save(copy(overstyrtEnhetsnummerSyncStatus = SYNCED))
        } catch (e: RuntimeException) {
            log.error(e) { "Kunne ikke oppdatere overstyrt enhetsnummer for: $rinasakId" }
            navRinasakRepository.save(copy(overstyrtEnhetsnummerSyncStatus = FAILED))
        }
    }
}