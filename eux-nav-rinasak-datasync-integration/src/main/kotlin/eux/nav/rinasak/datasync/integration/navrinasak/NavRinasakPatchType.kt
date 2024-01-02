package eux.nav.rinasak.datasync.integration.navrinasak

data class NavRinasakPatchType(
    val rinasakId: Int,
    val overstyrtEnhetsnummer: String? = null
)