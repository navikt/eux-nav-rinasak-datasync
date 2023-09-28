package eux.nav.rinasak.datasync.persistence

import eux.nav.rinasak.datasync.model.CaseStoreRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CaseStoreRecordRepository  : JpaRepository<CaseStoreRecord, UUID>