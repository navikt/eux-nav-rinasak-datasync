package eux.nav.rinasak.datasync.persistence

import eux.nav.rinasak.datasync.model.Dokument
import eux.nav.rinasak.datasync.model.InitiellFagsak
import eux.nav.rinasak.datasync.model.NavRinasak
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface NavRinasakRepository : JpaRepository<NavRinasak, UUID> {

}

interface DokumentRepository : JpaRepository<Dokument, UUID> {

}

interface InitiellFagsakRepository : JpaRepository<InitiellFagsak, UUID>
