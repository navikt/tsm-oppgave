package no.nav.tsm.scheduled.oppgave

import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID
import kotlin.time.toJavaDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import no.nav.tsm.core.Environment
import no.nav.tsm.core.model.Oppgave
import no.nav.tsm.core.model.TsmOppgaveType
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.saf.client.SafClient
import no.nav.tsm.scheduled.core.JobName
import no.nav.tsm.scheduled.core.jobs.AbstractJob
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveRepository
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveStatus

class CreateOppgaveJob(
    private val oppgaveService: OppgaveService,
    private val createOppgaveRepository: CreateOppgaveRepository,
    private val safClient: SafClient,
    applicationScope: CoroutineScope,
    environment: Environment,
) : AbstractJob(JobName.CREATE_OPPGAVE, applicationScope) {
    private val logger = logger()

    private val batchDelay = environment.jobs.oppgaveCreator.delay
    private val hungOppgave = environment.jobs.oppgaveCreator.hungTimeout

    override suspend fun runJob() =
        withContext(Dispatchers.IO) {
            while (isActive) {
                delay(batchDelay)
                logger.debug("Running create oppgave job (${batchDelay}ms)")
                handleOppgaveBatch()
            }
        }

    @WithSpan(inheritContext = false)
    private suspend fun handleOppgaveBatch() {
        val span = Span.current()

        createOppgaveRepository
            .resetHangingJobs(OffsetDateTime.now(UTC).minus(hungOppgave.toJavaDuration()))
            .let {
                if (it > 0) {
                    span.setAttribute("create-oppgave-job.jobs-reset", it.toString())
                    logger.warn("Found $it jobs for oppgave that needed to be reset")
                }
            }

        var count = 0
        do {
            val (next, didCreate) = createNextOppgave()
            if (next != null && didCreate) count++
        } while (next != null)

        if (count > 0) logger.info("Finished create oppgaver batch, created $count oppgaver")

        span.setAttribute("create-oppgave-job.oppgaver-created", count.toString())
    }

    @WithSpan
    private suspend fun createNextOppgave(): Pair<UUID?, Boolean> {
        val next = createOppgaveRepository.getNext() ?: return null to false

        val oppgave = oppgaveService.getOppgave(next).getOrNull()
        if (oppgave == null) {
            logger.warn("Fant ikke oppgave for id $next")
            createOppgaveRepository.updateStatus(next, CreateOppgaveStatus.FAILED)
            return next to false
        }

        // TODO: duplikatsjekk mot oppgaveapi
        // Sjekk på sykmeldingsId i fremtiden?

        if (oppgave.type != TsmOppgaveType.BEHANDLINGSDAGER) {
            logger.info("Oppgave with id $next is not of type ${TsmOppgaveType.BEHANDLINGSDAGER}")
            createOppgaveRepository.updateStatus(next, CreateOppgaveStatus.NOT_PROCESSABLE)
            return next to false
        }

        val behandlingsdagerOppgave = oppgave as Oppgave.Behandlingsdager

        if (behandlingsdagerOppgave.journalpostId == null) {
            val journalpostId = safClient.getJournalpost(behandlingsdagerOppgave.sykmeldingId)
            journalpostId.fold(
                ifLeft = { error ->
                    logger.warn(
                        "SafClient returned with $error when fetching journalpost for oppgave with id $next"
                    )
                    createOppgaveRepository.updateStatus(next, CreateOppgaveStatus.FAILED)
                    return next to false
                },
                ifRight = { data ->
                    oppgaveService.updateJournalpostIdForOppgave(
                        id = next,
                        journalpostId = data.journalpostId,
                    )
                },
            )
        }

        oppgaveService
            .createOppgave(next)
            .fold(
                ifLeft = { error ->
                    createOppgaveRepository.updateStatus(next, CreateOppgaveStatus.FAILED)
                    return next to false
                },
                ifRight = { data ->
                    createOppgaveRepository.updateStatus(next, CreateOppgaveStatus.COMPLETED)
                    return next to true
                },
            )
    }
}
