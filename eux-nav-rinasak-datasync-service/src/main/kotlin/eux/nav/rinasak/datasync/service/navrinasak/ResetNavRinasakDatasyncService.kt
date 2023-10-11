package eux.nav.rinasak.datasync.service.navrinasak

import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import org.springframework.stereotype.Service

@Service
class ResetNavRinasakDatasyncService(
    val navRinasakRepository: NavRinasakRepository,
    val dokumentRepository: DokumentRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
) {
    fun reset() {
        dokumentRepository.deleteAll()
        initiellFagsakRepository.deleteAll()
        navRinasakRepository.deleteAll()
    }
}