package eux.nav.rinasak.datasync.persistence

import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import eux.nav.rinasak.datasync.model.SyncStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NavRinasakRepository : JpaRepository<NavRinasak, UUID> {
    fun countBySyncStatus(syncStatus: SyncStatus): Long
    fun findAllBySyncStatus(syncStatus: SyncStatus): List<NavRinasak>
    fun findByOverstyrtEnhetsnummerSyncStatus(syncStatus: SyncStatus): List<NavRinasak>
    fun findByOverstyrtEnhetsnummerSyncStatusIsNullAndOverstyrtEnhetsnummerIsNotNull(): List<NavRinasak>
    fun countByOverstyrtEnhetsnummerSyncStatusIsNull(): Long
    fun countByOverstyrtEnhetsnummerSyncStatus(syncStatus: SyncStatus): Long
}

@Repository
interface DokumentRepository : JpaRepository<Dokument, UUID> {
    fun countBySyncStatus(syncStatus: SyncStatus): Long
    fun findAllBySyncStatus(syncStatus: SyncStatus): List<Dokument>
}

@Repository
interface InitiellFagsakRepository : JpaRepository<InitiellFagsak, UUID> {
    fun countBySyncStatus(syncStatus: SyncStatus): Long
    fun findAllBySyncStatus(syncStatus: SyncStatus): List<InitiellFagsak>
}
