package eux.nav.rinasak.datasync.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@Entity
data class Dokument(
    @Id
    val dokumentUuid: UUID,
    val navRinasakUuid: UUID,
    val dokumentInfoId: String?,
    val sedId: String,
    val sedType: String?,
    val opprettetBruker: String,
    val opprettetDato: LocalDateTime,
    @Enumerated(EnumType.STRING)
    val syncStatus: SyncStatus
)
