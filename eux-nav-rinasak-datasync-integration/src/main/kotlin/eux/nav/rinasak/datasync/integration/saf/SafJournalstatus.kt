package eux.nav.rinasak.datasync.integration.saf

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class SafJournalstatus(
    val erJournalfoert: Boolean
) {
    JOURNALFOERT(true),
    FERDIGSTILT(true),
    EKSPEDERT(true),
    AVBRUTT(true),
    UTGAAR(true),
    UKJENT_BRUKER(true),

    @JsonEnumDefaultValue
    UKJENT(false);
}

fun safJournalstatusOf(name: String) = SafJournalstatus.values()
    .firstOrNull { safJournalstatus: SafJournalstatus -> safJournalstatus.name == name }
    ?: SafJournalstatus.UKJENT
