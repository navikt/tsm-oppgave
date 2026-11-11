package no.nav.tsm.scheduled

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import no.nav.tsm.core.Environment
import no.nav.tsm.scheduled.core.JobSchedulerService
import no.nav.tsm.scheduled.core.db.JobRepository
import no.nav.tsm.scheduled.core.jobs.AbstractJob
import no.nav.tsm.scheduled.oppgave.CreateOppgaveJob

fun Application.configureJobDependencies() {
    val environment: Environment by dependencies
    val createOppgaveJob: CreateOppgaveJob by dependencies
    val list: List<AbstractJob> = listOf(createOppgaveJob)

    dependencies {
        provide<List<AbstractJob>> { list }
        provide<String>("runner") { environment.runtime.name }
        provide(JobRepository::class)
        provide(JobSchedulerService::class)
    }
}
