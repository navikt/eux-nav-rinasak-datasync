package eux.nav.rinasak.datasync.integration.navrinasak

data class DokumentCreateType(
    val sedId: String,
    val sedVersjon: Int,
    val sedType: String,
    val dokumentInfoId: String? = null
)
