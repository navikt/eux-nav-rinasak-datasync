package eux.nav.rinasak.datasync.service.navrinasak

import eux.nav.rinasak.datasync.integration.navrinasak.*
import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus.SYNCED
import eux.nav.rinasak.datasync.persistence.DokumentRepository
import eux.nav.rinasak.datasync.persistence.InitiellFagsakRepository
import eux.nav.rinasak.datasync.persistence.NavRinasakRepository
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SendToNavRinasakIntegrationService(
    val navRinasakRepository: NavRinasakRepository,
    val initiellFagsakRepository: InitiellFagsakRepository,
    val dokumentRepository: DokumentRepository,
    val navRinasakClient: NavRinasakClient,
    val navRinasakDokumentClient: NavRinasakDokumentClient
) {
    val log = logger {}

    @Transactional
    fun sync(
        navRinasak: NavRinasak,
        initielleFagsakerPending: Map<UUID, InitiellFagsak>,
        dokumenterPending: Map<UUID, List<Dokument>>
    ) {
        navRinasakClient
            .finnNavRinasakOrNull(navRinasak.rinasakId)
            ?.oppdaterNavRinasak(navRinasak, dokumenterPending)
            ?: lagNavRinasak(navRinasak, initielleFagsakerPending, dokumenterPending)
    }

    fun lagNavRinasak(
        navRinasak: NavRinasak,
        initielleFagsakerPending: Map<UUID, InitiellFagsak>,
        dokumenterPending: Map<UUID, List<Dokument>>
    ) {
        log.info { "Oppretter ny NAV Rinasak ${navRinasak.rinasakId} ..." }
        val navRinasakInitiellFagsakCreateType = initielleFagsakerPending[navRinasak.navRinasakUuid]
            ?.toNavRinasakInitiellFagsakCreateType()
        val navRinasakDokumentCreateTypes = dokumenterPending[navRinasak.navRinasakUuid]
            ?.toNavRinasakDokumentCreateTypes()
        val navRinasakCreateType = navRinasak.toNavRinasakCreateType(
            navRinasakDokumentCreateTypes = navRinasakDokumentCreateTypes,
            navRinasakInitiellFagsakCreateType = navRinasakInitiellFagsakCreateType
        )
        navRinasakRepository.save(navRinasak.copy(syncStatus = SYNCED))
        initielleFagsakerPending[navRinasak.navRinasakUuid]
            ?.let { initiellFagsakRepository.save(it.copy(syncStatus = SYNCED)) }
        dokumenterPending[navRinasak.navRinasakUuid]
            ?.forEach { dokumentRepository.save(it.copy(syncStatus = SYNCED)) }
        navRinasakClient.opprettNavRinasak(navRinasakCreateType)
        log.info { "NAV Rinasak opprettet: ${navRinasak.rinasakId}" }
    }

    fun NavRinasakType.oppdaterNavRinasak(
        navRinasak: NavRinasak,
        dokumenterPending: Map<UUID, List<Dokument>>
    ) {
        log.info { "Oppdaterer NAV Rinasak ${navRinasak.rinasakId} ..." }
        val dokumenterPendingList = dokumenterPending[navRinasak.navRinasakUuid] ?: emptyList()
        dokumenterPendingList
            .filterNot { harDokument(it, dokumenter ?: emptyList()) }
            .distinctBy { it.sedId to it.sedVersjon }
            .forEach {
                log.info { "Legger til dokument: ${it.sedId} : ${it.sedVersjon} i sak: ${navRinasak.rinasakId}" }
                navRinasakDokumentClient.opprettNavRinasakDokument(
                    navRinasak.rinasakId, DokumentCreateType(
                        sedId = it.sedId,
                        sedVersjon = it.sedVersjon,
                        sedType = it.sedType,
                        dokumentInfoId = it.dokumentInfoId
                    )
                )
            }
        navRinasakRepository.save(navRinasak.copy(syncStatus = SYNCED))
        dokumenterPending[navRinasak.navRinasakUuid]
            ?.forEach { dokumentRepository.save(it.copy(syncStatus = SYNCED)) }
    }

    fun harDokument(dokumentType: Dokument, dokumentTypeList: List<DokumentType>) =
        dokumentTypeList.any { it.sedId == dokumentType.sedId && it.sedVersjon == dokumentType.sedVersjon }
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
        fnr = fnr
    )

fun List<Dokument>.toNavRinasakDokumentCreateTypes() =
    map { it.toNavRinasakDokumentCreateType() }

fun Dokument.toNavRinasakDokumentCreateType() =
    NavRinasakDokumentCreateType(
        sedId = sedId,
        sedVersjon = sedVersjon,
        sedType = sedType,
        dokumentInfoId = dokumentInfoId,
    )
