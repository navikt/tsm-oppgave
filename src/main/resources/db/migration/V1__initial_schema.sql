CREATE TABLE opprett_oppgave
(
    id                       UUID PRIMARY KEY,
    personident              TEXT,
    orgnr                    TEXT,
    tildelt_enhetsnr         TEXT,
    opprettet_av_enhetsnr    TEXT,
    journalpost_id           TEXT        NOT NULL,
    behandles_av_applikasjon TEXT,
    beskrivelse              TEXT,
    tema                     TEXT        NOT NULL,
    behandlingstema          TEXT,
    oppgavetype              TEXT        NOT NULL,
    behandlingstype          TEXT,
    aktiv_dato               TIMESTAMPTZ NOT NULL,
    frist_ferdigstillelse    TIMESTAMPTZ,
    prioritet                TEXT        NOT NULL,
    ekstern_referanse_id     TEXT,
    oppgave_opprettet        TIMESTAMPTZ,
    oppgave_created          TIMESTAMPTZ NOT NULL
);