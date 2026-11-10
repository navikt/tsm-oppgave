package no.nav.tsm.scheduled

import io.opentelemetry.instrumentation.annotations.WithSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import no.nav.tsm.core.Environment
import no.nav.tsm.ktor.logger
import no.nav.tsm.oppgave.OppgaveService
import no.nav.tsm.scheduled.jobs.AbstractJob

class CreateOppgaveJob(
    private val oppgaveService: OppgaveService,
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
                logger.debug("Running sykmeldinger producer job (${batchDelay}ms)")
                handleOppgaveBatch()
            }
        }

    @WithSpan(inheritContext = false) private suspend fun handleOppgaveBatch() {}
}
