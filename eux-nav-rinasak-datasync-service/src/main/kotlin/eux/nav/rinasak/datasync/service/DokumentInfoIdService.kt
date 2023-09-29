package eux.nav.rinasak.datasync.service

import eux.nav.rinasak.datasync.model.Dokument
import org.springframework.stereotype.Service
import java.util.*

@Service
class DokumentInfoIdService {

    fun dokument(
        journalpostId: String,
        navRinasakUuid: UUID
    ) = Dokument(
        navRinasakUuid = navRinasakUuid,
        dokumentInfoId = "tbd",
        sedId = "tbd",
        sedType = "tbd",
    )
}