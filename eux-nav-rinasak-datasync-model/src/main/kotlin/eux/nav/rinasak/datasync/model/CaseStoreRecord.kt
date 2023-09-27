package eux.nav.rinasak.datasync.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

@Entity
data class CaseStoreRecord(
    @Id
    val caseStoreRecordUuid: UUID,
    val caseStoreId: Long?,
    val rinasakId: Int,
    val fagsakId: String?,
    val fagsakTema: String?,
    val overstyrtEnhetsnummer: String?,
    val journalpostId: String?,
    val bucId: UUID?,
    val opprettetBruker: String? = "ukjent",
    val opprettetDato: LocalDateTime? = now(),
    @Enumerated(EnumType.STRING)
    val syncStatus: SyncStatus
)
