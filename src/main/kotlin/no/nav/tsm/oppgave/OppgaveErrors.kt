package no.nav.tsm.oppgave

enum class OppgaveErrors {
    NotFound,  // relevant for hele flyten - route, service, client
    Unknown,  // relevant for hele flyten - route, service, client
    Unauthorized,  // oss problem, på et tidligere tidspunkt??? men dersom fra oppgave er det et oss problem
    Forbidden, // sjekkes i oppgaveService av oss, men dersom fra oppgaveclient er det et oss problem, men vi får den ikke fra oppgaveclient pga m2m
    BadRequest,  // oss problem
}
