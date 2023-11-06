package eux.nav.rinasak.datasync.model

enum class SyncStatus {
    PENDING,
    SYNCED,
    RINASAK_NOT_FOUND,
    FAGSAK_NOT_FOUND,
    INVALID_FNR,
}
