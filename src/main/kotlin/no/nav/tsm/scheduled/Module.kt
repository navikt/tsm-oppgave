package no.nav.tsm.scheduled

import io.ktor.server.application.Application

fun Application.configureJobsModule() {
    configureJobDependencies()
    configureJobScheduling()
}
