package no.nav.tsm.scheduled.core.db

import io.opentelemetry.instrumentation.annotations.WithSpan
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaInstant
import no.nav.tsm.core.db.dbQuery
import no.nav.tsm.scheduled.core.Job
import no.nav.tsm.scheduled.core.JobName
import no.nav.tsm.scheduled.core.RunnerJobStatus
import no.nav.tsm.scheduled.core.jobs.JobStatus
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.deleteReturning
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.upsert

class JobRepository {

    @WithSpan
    suspend fun getJobs(): List<Job> = dbQuery {
        JobTable.selectAll()
            .map {
                Job(
                    jobName = JobName.valueOf(it[JobTable.name]),
                    desiredState = JobStatus.valueOf(it[JobTable.desiredState]),
                    updatedAt =
                        OffsetDateTime.ofInstant(
                            it[JobTable.updatedAt].toJavaInstant(),
                            ZoneOffset.UTC,
                        ), // TODO: ta en prat om OffsetDateTime valg
                    updatedBy = it[JobTable.updatedBy],
                )
            }
            .toList()
    }

    @WithSpan(inheritContext = false)
    suspend fun updateJobStatus(runner: String, jobName: JobName, jobStatus: JobStatus) {
        dbQuery {
            JobStatusTable.upsert(
                where = { JobStatusTable.runner eq runner and (JobStatusTable.job eq jobName.name) }
            ) {
                it[JobStatusTable.runner] = runner
                it[JobStatusTable.job] = jobName.name
                it[JobStatusTable.state] = jobStatus.name
                it[JobStatusTable.updatedAt] = Clock.System.now()
            }
        }
    }

    @WithSpan
    suspend fun deleteRunner(runner: String) = dbQuery {
        JobStatusTable.deleteReturning { JobStatusTable.runner eq runner }
            .map {
                RunnerJobStatus(
                    runner = it[JobStatusTable.runner],
                    job = JobName.valueOf(it[JobStatusTable.job]),
                    state = JobStatus.valueOf(it[JobStatusTable.state]),
                    updatedAt =
                        OffsetDateTime.ofInstant(
                            it[JobStatusTable.updatedAt].toJavaInstant(),
                            ZoneOffset.UTC,
                        ),
                )
            }
            .toList()
    }

    suspend fun deleteOldJobsRunners() = dbQuery {
        JobStatusTable.deleteReturning {
                JobStatusTable.updatedAt less Clock.System.now() - 3600.milliseconds
            }
            .map {
                RunnerJobStatus(
                    runner = it[JobStatusTable.runner],
                    job = JobName.valueOf(it[JobStatusTable.job]),
                    state = JobStatus.valueOf(it[JobStatusTable.state]),
                    updatedAt =
                        OffsetDateTime.ofInstant(
                            it[JobStatusTable.updatedAt].toJavaInstant(),
                            ZoneOffset.UTC,
                        ),
                )
            }
            .toList()
    }

    @WithSpan
    suspend fun updateJob(jobName: JobName, desiredState: JobStatus, updatedBy: String) {
        dbQuery {
            JobTable.update({ JobTable.name eq jobName.name }) {
                it[JobTable.desiredState] = desiredState.name
                it[JobTable.updatedAt] = Clock.System.now()
                it[JobTable.updatedBy] = updatedBy
            }
        }
    }

    @WithSpan
    suspend fun getJobStatus(): List<RunnerJobStatus> {
        return dbQuery {
            JobStatusTable.selectAll()
                .map {
                    RunnerJobStatus(
                        runner = it[JobStatusTable.runner],
                        job = JobName.valueOf(it[JobStatusTable.job]),
                        state = JobStatus.valueOf(it[JobStatusTable.state]),
                        updatedAt =
                            OffsetDateTime.ofInstant(
                                it[JobStatusTable.updatedAt].toJavaInstant(),
                                ZoneOffset.UTC,
                            ),
                    )
                }
                .toList()
        }
    }

    suspend fun updateJobStatusTimestamp(runner: String) = dbQuery {
        JobStatusTable.update({ JobStatusTable.runner eq runner }) {
            it[JobStatusTable.updatedAt] = Clock.System.now()
        }
    }
}
