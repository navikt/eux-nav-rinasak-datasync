package eux.nav.rinasak.datasync.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime
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
    val opprettetBruker: String,
    val opprettetDato: LocalDateTime,
)
