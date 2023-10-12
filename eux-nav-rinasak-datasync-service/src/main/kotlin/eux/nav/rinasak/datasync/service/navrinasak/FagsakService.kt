package eux.nav.rinasak.datasync.service.navrinasak

import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import org.springframework.stereotype.Service

@Service
class FagsakService(
    val fagsakRepository: InitiellFagsakRepository
) {

    fun fagsaker(): List<InitiellFagsak> = fagsakRepository.findAll()

    fun fagsakerPending(): List<InitiellFagsak> = fagsakRepository.findAllBySyncStatus(PENDING)

}
