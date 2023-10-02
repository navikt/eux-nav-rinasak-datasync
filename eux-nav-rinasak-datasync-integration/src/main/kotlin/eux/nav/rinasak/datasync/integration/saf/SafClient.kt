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

    fun safSak(fagsakId: String) =
        SafSak(
            fagsakId = fagsakId,
        )

    fun safJournalpost(journalpostId: String): SafJournalpost {
        val graphQlQuery = query(journalpostId)
        val request = RequestEntity
            .post(
                UriComponentsBuilder
                    .fromHttpUrl(safUrl)
                    .path("/graphql").build().toUri()
            )
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body<GraphQlQuery>(graphQlQuery)
        log.info("Henter SAF journalpost: $journalpostId")
        val responseString = safRestTemplate
            .exchange(request, String::class.java)
        log.info("SAF String: $responseString")
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

    fun query(journalpostId: String) = GraphQlQuery(
        """query {
              journalpost(journalpostId: "$journalpostId") {
                journalposter {
                  journalpostId
                  tittel
                  journalposttype
                  journalstatus
                  tema
                  dokumenter {
                    dokumentInfoId
                    tittel
                  }
                }
              }
        }""".trimIndent()
    )
}

data class GraphQlQuery(
    val query: String
)
