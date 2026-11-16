package no.nav.tsm.oppgave

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.opentelemetry.instrumentation.annotations.WithSpan
import java.time.LocalDateTime
import java.util.UUID
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.TsmOppgaveType
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.client.OppgaveClient
import no.nav.tsm.oppgave.client.model.OppgaveClientErrors
import no.nav.tsm.oppgave.client.model.OppgaveResponse
import no.nav.tsm.oppgave.client.model.toOpprettOppgaveRequest
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveRepository

enum class OppgaveServiceErrors {
    Conflict,
    NotFound,
    NotSupported,
    Error,
}

class OppgaveService(
    private val oppgaveClient: OppgaveClient,
    private val oppgaveRepository: OppgaveRepository,
    private val createOppgaveRepository: CreateOppgaveRepository,
) {
    val logger = logger()

    @WithSpan
    suspend fun getOppgaver(ident: String): Either<OppgaveClientErrors, List<OppgaveResponse>> {
        // Todo deal with errors?
        return oppgaveClient.getOppgaver(ident)
    }

    @WithSpan
    suspend fun getOppgave(id: UUID): Either<OppgaveServiceErrors, Oppgave> {
        val oppgave = oppgaveRepository.byId(id) ?: return OppgaveServiceErrors.NotFound.left()
        return oppgave.right()
    }

    @WithSpan
    suspend fun storeOppgave(oppgave: Oppgave): Either<OppgaveServiceErrors, UUID> {
        if (oppgave.type != TsmOppgaveType.BEHANDLINGSDAGER) {
            return OppgaveServiceErrors.NotSupported.left()
        }
        // TODO: denne må vi prate om, returnerer nå id istedenfor oppgave
        val createdOppgave =
            oppgaveRepository.insert(oppgave) ?: return OppgaveServiceErrors.Error.left()
        createOppgaveRepository.insert(createdOppgave)
        return createdOppgave.right()
    }

    @WithSpan
    suspend fun createOppgave(id: UUID): Either<OppgaveServiceErrors, Oppgave> {
        val oppgave = oppgaveRepository.byId(id) ?: return OppgaveServiceErrors.NotFound.left()

        // Assume everything is okay
        val createdOppgave =
            oppgaveClient.createOppgave(oppgave.toOpprettOppgaveRequest(), oppgave.sykmeldingId)

        createdOppgave.fold(
            ifLeft = { error ->
                return OppgaveServiceErrors.Error.left()
            },
            ifRight = { oppgaveResponse ->
                if (oppgaveResponse.opprettetTidspunkt == null) {
                    logger.warn(
                        "Oppgave ${oppgaveResponse.id} er opprettet, men mangler opprettetTidspunkt. Bruker currentTime for opprettetTidspunkt"
                    )
                }
                val oppgave =
                    oppgaveRepository.updateCreatedOppgave(
                        id,
                        oppgaveResponse.id.toString(),
                        oppgaveResponse.opprettetTidspunkt ?: LocalDateTime.now(),
                    )
                return oppgave.right()
            },
        )
    }

    @WithSpan
    suspend fun updateJournalpostIdForOppgave(id: UUID, journalpostId: String): Int {
        return oppgaveRepository.updateJournalpostId(id, journalpostId)
    }
}
