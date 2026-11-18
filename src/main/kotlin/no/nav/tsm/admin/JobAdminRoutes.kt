package no.nav.tsm.admin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import no.nav.tsm.ktor.auth.entra.entraOnBehalfOf
import no.nav.tsm.ktor.auth.entra.obo.onBehalfOfUser
import no.nav.tsm.ktor.logger
import no.nav.tsm.scheduled.core.JobName
import no.nav.tsm.scheduled.core.db.JobRepository
import no.nav.tsm.scheduled.core.jobs.JobStatus
import java.time.OffsetDateTime

data class JobRunners(val runner: String, val state: JobStatus, val updatedAt: OffsetDateTime)

data class JobStatusResponse(
    val name: JobName,
    val runners: List<JobRunners>,
    val desiredState: JobStatus,
    val updatedAt: OffsetDateTime,
)

enum class JobUpdateAction {
    START,
    STOP,
}

data class JobUpdatePayload(val state: JobUpdateAction)

fun Application.configureJobAdminRoutes() {
    val logger = logger()
    val jobRepository: JobRepository by dependencies

    routing {
        entraOnBehalfOf {
            route("/internal/admin/jobs") {
                get {
                    val jobs = jobRepository.getJobs()
                    val statuses = jobRepository.getJobStatus().groupBy { it.job }
                    val response = jobs.map { job ->
                        val jobStatuses = statuses[job.jobName] ?: emptyList()
                        JobStatusResponse(
                            name = job.jobName,
                            desiredState = job.desiredState,
                            updatedAt = job.updatedAt,
                            runners = jobStatuses.map { runner ->
                                JobRunners(
                                    runner = runner.runner,
                                    state = runner.state,
                                    updatedAt = runner.updatedAt,
                                )
                            }
                        )
                    }
                    call.respond(HttpStatusCode.OK, response)
                }
                post("{name}/status") {
                    val name = call.parameters["name"]?.let { JobName.valueOf(it) }
                        ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val job = call.receive<JobUpdatePayload>()
                    val desiredState =
                        when (job.state) {
                            JobUpdateAction.START -> JobStatus.RUNNING
                            JobUpdateAction.STOP -> JobStatus.STOPPED
                        }

                    val principal = call.onBehalfOfUser()

                    logger.info(
                        "User ${principal.name} has requested to change the status of job $name to $desiredState"
                    )

                    jobRepository.updateJob(name, desiredState, principal.email)

                    call.respond(HttpStatusCode.Accepted, mapOf("ok" to true))
                }
            }
        }
    }
}