package no.nav.tsm.scheduled.oppgave

import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID
import kotlin.compareTo
import kotlin.time.toJavaDuration
import kotlin.toString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import no.nav.tsm.core.Environment
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.scheduled.core.JobName
import no.nav.tsm.scheduled.core.jobs.AbstractJob
import no.nav.tsm.scheduled.oppgave.db.CreateOppgaveRepository

class CreateOppgaveJob(
    private val oppgaveService: OppgaveService,
    private val createOppgaveRepository: CreateOppgaveRepository,
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

        try {
            val oppgave =
                requireNotNull(oppgaveService.getOppgave(next)) {
                    "Oppgave with id $next not found in db"
                }

            // TODO: duplikatsjekk mot oppgaveapi

            /*


            * sjekke om oppgaven allerede er opprettet i oppgave api
            *
            * sjekk at journalpostId er null
            * - hente journalpostid fra saf
            *
            * opprette mot oppgave api
            *
            * oppdatere oppgavetabell
            *
            * oppdatere createOppgaveTable
            *
            * */
            return next to true
        } catch (e: Exception) {
            return next to false
        }
    }
}
