package eux.nav.rinasak.datasync.integration.saf

data class SafJournalpostRoot(val data: SafJournalpostData)
data class SafJournalpostData(val journalpost: SafJournalpost)
data class SafSakerRoot(val data: SafSakerData)
data class SafSakerData(val saker: List<SafSak>)
