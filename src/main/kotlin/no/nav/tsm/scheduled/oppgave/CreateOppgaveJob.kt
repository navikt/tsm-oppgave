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
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.oppgave.OppgaveServiceErrors
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
        logger.info("getting next oppgave to create in creatOppgaveJov")
        val next = createOppgaveRepository.getNext() ?: return null to false

        // TODO: duplikatsjekk

        oppgaveService
            .createOppgave(next)
            .fold(
                ifLeft = { error ->
                    val status =
                        when (error) {
                            OppgaveServiceErrors.NotFound -> CreateOppgaveStatus.FAILED
                            OppgaveServiceErrors.Error -> CreateOppgaveStatus.FAILED
                            OppgaveServiceErrors.NotSupported -> CreateOppgaveStatus.NOT_PROCESSABLE
                            else -> CreateOppgaveStatus.FAILED
                        }
                    createOppgaveRepository.updateStatus(next, status)
                    return next to false
                },
                ifRight = {
                    createOppgaveRepository.updateStatus(next, CreateOppgaveStatus.COMPLETED)
                    createOppgaveRepository.setToCompleted(next)
                    return next to true
                },
            )
    }
}
