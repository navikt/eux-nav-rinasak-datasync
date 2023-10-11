package eux.nav.rinasak.datasync.service.navrinasak

import eux.nav.rinasak.datasync.integration.navrinasak.*
import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SendToNavRinasakIntegrationService(
    val navRinasakRepository: NavRinasakRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
    val dokumentRepository: DokumentRepository,
    val navRinasakClient: NavRinasakClient,
) {
    val log: Logger = LoggerFactory.getLogger(SendToNavRinasakIntegrationService::class.java)

    @Transactional
    fun sync(
        navRinasak: NavRinasak,
        initielleFagsakerPending: Map<UUID, InitiellFagsak>,
        dokumenterPending: Map<UUID, List<Dokument>>
    ) {
        navRinasakClient
            .finnNavRinasakOrNull(navRinasak.rinasakId)
            ?.oppdaterNavRinasak(navRinasak, initielleFagsakerPending, dokumenterPending)
            ?: lagNavRinasak(navRinasak, initielleFagsakerPending, dokumenterPending)
    }

    fun lagNavRinasak(
        navRinasak: NavRinasak,
        initielleFagsakerPending: Map<UUID, InitiellFagsak>,
        dokumenterPending: Map<UUID, List<Dokument>>
    ) {
        log.info("Oppretter ny NAV Rinasak ${navRinasak.rinasakId} ...")
        val navRinasakInitiellFagsakCreateType = initielleFagsakerPending[navRinasak.navRinasakUuid]
            ?.toNavRinasakInitiellFagsakCreateType()
        val navRinasakDokumentCreateTypes = dokumenterPending[navRinasak.navRinasakUuid]
            ?.toNavRinasakDokumentCreateTypes()
        val navRinasakCreateType = navRinasak.toNavRinasakCreateType(
            navRinasakDokumentCreateTypes = navRinasakDokumentCreateTypes,
            navRinasakInitiellFagsakCreateType = navRinasakInitiellFagsakCreateType
        )
        navRinasakRepository.save(navRinasak.copy(syncStatus = SyncStatus.SYNCED))
        initielleFagsakerPending[navRinasak.navRinasakUuid]
            ?.let { initiellFagsakRepository.save(it) }
        dokumenterPending[navRinasak.navRinasakUuid]
            ?.forEach { dokumentRepository.save(it) }
        navRinasakClient.opprettNavRinasak(navRinasakCreateType)
        log.info("NAV Rinasak opprettet: ${navRinasak.rinasakId}")
    }
}


fun NavRinasakType.oppdaterNavRinasak(
    navRinasak: NavRinasak,
    initielleFagsakerPending: Map<UUID, InitiellFagsak>,
    dokumenterPending: Map<UUID, List<Dokument>>
) {
}

fun NavRinasak.toNavRinasakCreateType(
    navRinasakInitiellFagsakCreateType: NavRinasakInitiellFagsakCreateType?,
    navRinasakDokumentCreateTypes: List<NavRinasakDokumentCreateType>?
) =
    NavRinasakCreateType(
        rinasakId = rinasakId,
        initiellFagsak = navRinasakInitiellFagsakCreateType,
        dokumenter = navRinasakDokumentCreateTypes
    )

fun InitiellFagsak.toNavRinasakInitiellFagsakCreateType() =
    NavRinasakInitiellFagsakCreateType(
        id = id,
        tema = tema,
        system = system,
        nr = nr,
        type = type,
        arkiv = arkiv,
        aktoerId = fnr
    )

fun List<Dokument>.toNavRinasakDokumentCreateTypes() =
    map { it.toNavRinasakDokumentCreateType() }

fun Dokument.toNavRinasakDokumentCreateType() =
    NavRinasakDokumentCreateType(
        sedId = sedId,
        sedType = sedType,
        dokumentInfoId = dokumentInfoId,
    )
