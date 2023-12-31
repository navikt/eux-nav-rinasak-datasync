package eux.nav.rinasak.datasync.model

import eux.nav.rinasak.datasync.model.SyncStatus.PENDING
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*

@Entity
data class InitiellFagsak(
    @Id
    val navRinasakUuid: UUID,
    val id: String?,
    val tema: String?,
    val system: String?,
    val nr: String?,
    val type: String?,
    val arkiv: String?,
    val fnr: String,
    val opprettetBruker: String = "datasync",
    val opprettetDato: LocalDateTime = now(),
    @Enumerated(EnumType.STRING)
    val syncStatus: SyncStatus = PENDING
)
