package eux.nav.rinasak.datasync.model

import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*
import java.util.UUID.randomUUID

@Entity
data class Dokument(
    @Id
    val dokumentUuid: UUID = randomUUID(),
    val navRinasakUuid: UUID,
    val dokumentInfoId: String,
    val sedId: String,
    val sedType: String,
    val opprettetBruker: String = "datasync",
    val opprettetDato: LocalDateTime = now(),
    @Enumerated(EnumType.STRING)
    val syncStatus: SyncStatus = PENDING
)
