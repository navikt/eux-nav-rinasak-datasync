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
data class NavRinasak(
    @Id
    val navRinasakUuid: UUID = randomUUID(),
    val rinasakId: Int,
    val opprettetBruker: String = "datasync",
    val opprettetDato: LocalDateTime = now(),
    @Enumerated(EnumType.STRING)
    val syncStatus: SyncStatus = PENDING
) {

}
