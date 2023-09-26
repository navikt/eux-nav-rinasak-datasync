package eux.nav.rinasak.datasync.integration.casestore

import java.util.*

data class EuxCaseStoreCase (
    val id: Long? = null,
    val rinaId: String? = null,
    val navId: String? = null,
    val bucId: UUID? = null,
    val theme: String? = null,
    val caseFileId: String? = null,
    val enhetId: String? = null,
    val syncStatus: String? = null,
)
