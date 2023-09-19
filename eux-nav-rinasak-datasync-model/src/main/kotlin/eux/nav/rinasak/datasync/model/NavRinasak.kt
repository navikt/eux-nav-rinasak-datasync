package eux.nav.rinasak.datasync.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@Entity
data class NavRinasak(
    @Id
    val navRinasakUuid: UUID,
    val rinasakId: Int,
    val opprettetBruker: String,
    val opprettetDato: LocalDateTime
)
