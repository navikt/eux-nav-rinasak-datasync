package eux.nav.rinasak.datasync.integration.saf

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class SafClient(
    @Value("\${endpoint.eux.saf}")
    val safUrl: String,
    val safRestTemplate: RestTemplate
) {
    val log: Logger = LoggerFactory.getLogger(SafClient::class.java)

    fun safSakerRoot(fnr: String): SafSakerRoot? {
        val graphQlQuery = sakerQuery(fnr)
        val request = RequestEntity
            .post(
                UriComponentsBuilder
                    .fromHttpUrl(safUrl)
                    .path("/graphql").build().toUri()
            )
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body<GraphQlQuery>(graphQlQuery)
        log.info("Henter SAF saker for fnr: $fnr")
        val response = safRestTemplate
            .exchange(request, SafSakerRoot::class.java)
        return if (response.statusCode.is2xxSuccessful) {
            response.body
        } else {
            log.info("Feilet mot SAF (${response.statusCode.value()}), body: ${response.body}")
            throw RuntimeException("feilet mot saf med ${response.statusCode.value()}")
        }
    }

    fun safSaker(fnr: String): List<SafSak> =
        try {
            safSakerRoot(fnr)!!
                .data
                .saker
        } catch (e: RuntimeException) {
            log.error("Kunne ikke hente saker fra SAF for fnr $fnr", e)
            emptyList()
        }

    fun safSakOrNull(fnr: String, fagsakId: String): SafSak? {
        val saker = safSaker(fnr)
        val sakMedFagsakId = saker.firstOrNull { it.fagsakId == fagsakId }
        return if (saker.isEmpty()) {
            log.info("Tom liste fra SAF for fnr=$fnr og fagsakId=$fagsakId")
            null
        } else if (sakMedFagsakId != null) {
            sakMedFagsakId
        } else {
            log.info("Treff mot SAF saker for $fnr (${saker.size}, men uten fagsakId $fagsakId, bruker første i lista")
            saker.first()
        }
    }

    fun safJournalpost(journalpostId: String): SafJournalpost {
        val graphQlQuery = journalpostQuery(journalpostId)
        val request = RequestEntity
            .post(
                UriComponentsBuilder
                    .fromHttpUrl(safUrl)
                    .path("/graphql").build().toUri()
            )
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body<GraphQlQuery>(graphQlQuery)
        val response = safRestTemplate
            .exchange(request, SafJournalpostRoot::class.java)
        return if (response.statusCode.is2xxSuccessful) {
            response
                .body
                ?.data
                ?.journalpost ?: throw RuntimeException("feilet mot saf, men med 200")
        } else {
            log.info("Feilet mot SAF (${response.statusCode.value()}), body: ${response.body}")
            throw RuntimeException("feilet mot saf med ${response.statusCode.value()}")
        }
    }

}

fun journalpostQuery(journalpostId: String) = GraphQlQuery(
    """query {
          journalpost(journalpostId: "$journalpostId") {
              journalpostId
              tittel
              journalposttype
              journalstatus
              eksternReferanseId
              tema
              dokumenter {
                dokumentInfoId
                tittel
              }
          }
        }""".trimIndent()
)

fun sakerQuery(fnr: String) = GraphQlQuery(
    """query {
          saker(brukerId: { id: "$fnr", type:FNR } ) {
            tema
            fagsakId
            fagsaksystem
            arkivsaksnummer
            arkivsaksystem
            sakstype
          }
        }""".trimIndent()
)

data class GraphQlQuery(
    val query: String
)
