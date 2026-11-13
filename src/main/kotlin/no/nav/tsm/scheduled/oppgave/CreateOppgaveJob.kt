package no.nav.tsm.scheduled.oppgave

import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import kotlin.time.toJavaDuration
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
            .let {}

        var count = 0
        /* do {
            val (next, didProduce) = createNextOppgave()
            if (next != null && didProduce) count++
        } while (next != null)*/

        if (count > 0) logger.info("Finished create oppgaver batch, created $count oppgaver")

        span.setAttribute("create-oppgave-job.oppgaver-created", count.toString())
    }

    /*private fun createNextOppgave(): Pair<CreateOppgave, Boolean> {
    TODO("Not yet implemented")
    */
    /*
     * kalle saf
     *
     * opprette oppgave i oppgave-api
     *
     *
     * lagre i db
     *
     *
     * */
    /*
    }*/
}
