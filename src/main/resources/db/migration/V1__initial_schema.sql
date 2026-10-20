CREATE TABLE journalpost
(
    sykmelding_id  UUID PRIMARY KEY,
    created        TIMESTAMP,
    journalpost_id TEXT NOT NULL
);