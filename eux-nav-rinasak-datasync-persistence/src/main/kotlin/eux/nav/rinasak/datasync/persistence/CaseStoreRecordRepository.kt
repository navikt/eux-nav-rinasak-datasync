package eux.nav.rinasak.datasync.persistence

import eux.nav.rinasak.datasync.model.CaseStoreRecord
import eux.nav.rinasak.datasync.model.SyncStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CaseStoreRecordRepository : JpaRepository<CaseStoreRecord, UUID> {
    fun countBySyncStatus(syncStatus: SyncStatus): Long
    fun findAllBySyncStatus(syncStatus: SyncStatus): List<CaseStoreRecord>
}
