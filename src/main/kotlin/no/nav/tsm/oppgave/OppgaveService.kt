package no.nav.tsm.oppgave

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
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
import no.nav.tsm.saf.client.SafClient
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveRepository

enum class OppgaveServiceErrors {
    Conflict,
    NotFound,
    NotSupported,
    Error,
}

class OppgaveService(
    private val oppgaveClient: OppgaveClient,
    private val safClient: SafClient,
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
        val createdOppgave =
            oppgaveRepository.insert(oppgave) ?: return OppgaveServiceErrors.Error.left()
        createOppgaveRepository.insert(createdOppgave)
        return createdOppgave.right()
    }

    // createOppgave

    /*
    * kaller oppgaveRepoById

    * */

    // validateOppgave
    // null sjekk osv... sjekker type osv...

    // updateJournalpost

    //

    @WithSpan
    suspend fun createOppgave(id: UUID): Either<OppgaveServiceErrors, Oppgave> {
        val oppgave = oppgaveRepository.byId(id)
        if (oppgave == null) {
            logger.warn("Oppgave med id $id ikke funnet")
            return OppgaveServiceErrors.NotFound.left()
        }

        if (oppgave !is Oppgave.Behandlingsdager) {
            logger.info("Oppgave med id $id er ikke av typen BEHANDLINGSDAGER")
            return OppgaveServiceErrors.NotSupported.left()
        }

        if (oppgave.journalpostId == null) {
            safClient
                .getJournalpost(oppgave.sykmeldingId)
                .fold(
                    ifLeft = { error ->
                        logger.warn("Kunne ikke hente journalpost for oppgave med id $id: $error")
                        return OppgaveServiceErrors.Error.left()
                    },
                    ifRight = { journalpost ->
                        updateJournalpostIdForOppgave(
                            id = id,
                            journalpostId = journalpost.journalpostId,
                        )
                    },
                )
        }
        return createInOppgaveApiAndUpdate(id)
    }

    @WithSpan
    suspend fun createInOppgaveApiAndUpdate(id: UUID): Either<OppgaveServiceErrors, Oppgave> =
        either {
            val oppgave =
                ensureNotNull(oppgaveRepository.byId(id)) { OppgaveServiceErrors.NotFound }

            // TODO: add more errors from oppgaveClient
            val oppgaveResponse =
                oppgaveClient
                    .createOppgave(
                        oppgave = oppgave.toOpprettOppgaveRequest(),
                        oppgave.sykmeldingId,
                    )
                    .mapLeft { OppgaveServiceErrors.Error }
                    .bind()

            if (oppgaveResponse.opprettetTidspunkt == null) {
                logger.warn(
                    "Oppgave ${oppgaveResponse.id} er opprettet, men mangler opprettetTidspunkt. Bruker currentTime for opprettetTidspunkt"
                )
            }

            oppgaveRepository.updateCreatedOppgave(
                id,
                oppgaveResponse.id.toString(),
                oppgaveResponse.opprettetTidspunkt ?: LocalDateTime.now(),
            )
        }

    @WithSpan
    suspend fun updateJournalpostIdForOppgave(id: UUID, journalpostId: String): Int {
        return oppgaveRepository.updateJournalpostId(id, journalpostId)
    }
}
