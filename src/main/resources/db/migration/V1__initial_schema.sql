CREATE TABLE oppgave
(
    id                       UUID PRIMARY KEY,
    personident              TEXT,
    tildelt_enhetsnr         TEXT,
    journalpost_id           TEXT,
    behandles_av_applikasjon TEXT,
    beskrivelse              TEXT,
    behandlingstema          TEXT,
    oppgavetype              TEXT        NOT NULL,
    behandlingstype          TEXT,
    aktiv_dato               DATE        NOT NULL,
    status                   TEXT,
    frist_ferdigstillelse    DATE,
    prioritet                TEXT        NOT NULL,
    sykmelding_id            TEXT,
    created                  TIMESTAMPTZ NOT NULL,
    oppgave_opprettet        TIMESTAMPTZ,
    oppgave_id               UUID,
    tsm_oppgavetype          TEXT
);

CREATE TABLE create_oppgave
(
    id                  UUID REFERENCES oppgave (id),
    status               TEXT        NOT NULL,
    created_timestamp   TIMESTAMPTZ NOT NULL,
    event_timestamp     TIMESTAMPTZ NOT NULL,
    completed_timestamp TIMESTAMPTZ
);

-- Jobs and related state
CREATE TABLE job
(
    name          TEXT PRIMARY KEY,
    desired_state TEXT        NOT NULL,
    updated_at    TIMESTAMPTZ NOT NULL,
    updated_by    TEXT        NOT NULL
);

CREATE TABLE job_status
(
    runner     TEXT        NOT NULL,
    job        TEXT        NOT NULL,
    state      TEXT        NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (runner, job)
);

-- Initial state for jobs
insert into job(name, desired_state, updated_at, updated_by)
values ('CREATE_OPPGAVE', 'RUNNING', now(), 'system');