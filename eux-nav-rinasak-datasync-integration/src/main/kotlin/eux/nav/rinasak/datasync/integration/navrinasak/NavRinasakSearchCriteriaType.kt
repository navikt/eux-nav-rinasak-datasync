package eux.nav.rinasak.datasync.integration.navrinasak

import java.time.OffsetDateTime

data class NavRinasakSearchCriteriaType(
    val rinasakId: Int? = null,
    val fraOpprettetDato: OffsetDateTime? = null,
    val tilOpprettetDato: OffsetDateTime? = null
)
