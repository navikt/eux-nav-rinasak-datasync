package eux.nav.rinasak.datasync.model

import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*
import java.util.UUID.randomUUID

data class CaseStoreRecord(
    val caseStoreRecordUuid: UUID = randomUUID(),
    val rinasakId: Int? = 0,
    val fagsakNr: String? = "0",
    val fagsakTema: String? = "ukjent",
    val overstyrtEnhetsnummer: String? = "0",
    val journalpostId: String? = "0",
    val bucId: UUID? = null,
    val opprettetBruker: String? = "ukjent",
    val opprettetDato: LocalDateTime? = now()
)