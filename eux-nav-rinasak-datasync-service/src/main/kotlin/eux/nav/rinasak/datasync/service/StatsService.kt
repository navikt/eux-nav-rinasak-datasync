package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.model.Stats
import eux.nav.rinasak.datasync.persistence.CaseStoreRecordRepository
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import org.springframework.stereotype.Service

@Service
class StatsService(
    val navRinasakRepository: NavRinasakRepository,
    val dokumentRepository: DokumentRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
    val caseStoreRecordRepository: CaseStoreRecordRepository,
) {
    fun stats() =
        Stats(
            navRinasakCount = navRinasakRepository.count(),
            dokumenterCount = dokumentRepository.count(),
            initiellFagsakCount = initiellFagsakRepository.count(),
            caseStoreRecordCount = caseStoreRecordRepository.count(),
        )
}
