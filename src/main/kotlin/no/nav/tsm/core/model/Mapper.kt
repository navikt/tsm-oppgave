package no.nav.tsm.core.model

import no.nav.tsm.api.model.OpprettOppgaveRequest

fun OpprettOppgaveRequest.toOppgave(kilde: OppgaveKilde): OppgaveRequest.Post {
    val ident = requireNotNull(personident)

    val oppgave =
        when (kilde) {
            OppgaveKilde.UTENLANDSK ->
                Oppgave.Utenlandsk(
                    eksternReferanseId = eksternReferanseId,
                    personident = ident,
                    beskrivelse = beskrivelse,
                )

            OppgaveKilde.NASJONAL ->
                Oppgave.Nasjonal(
                    eksternReferanseId = eksternReferanseId,
                    personident = ident,
                    beskrivelse = beskrivelse,
                    journalpostId = journalpostId,
                )

            OppgaveKilde.TILBAKEDATERING ->
                Oppgave.Tilbakedatering(
                    eksternReferanseId = eksternReferanseId,
                    personident = ident,
                    beskrivelse = beskrivelse,
                )

            OppgaveKilde.FORDELING ->
                Oppgave.Fordeling(
                    eksternReferanseId = eksternReferanseId,
                    personident = personident,
                    beskrivelse = beskrivelse,
                    behandlingstype = behandlingstype,
                )

            OppgaveKilde.BEHANDLINGSDAGER ->
                Oppgave.Behandlingsdager(
                    eksternReferanseId = eksternReferanseId,
                    personident = ident,
                    beskrivelse = beskrivelse,
                )

            OppgaveKilde.OPPFOLGING ->
                Oppgave.Oppfolging(
                    eksternReferanseId = eksternReferanseId,
                    personident = ident,
                    beskrivelse = beskrivelse,
                    tildeltEnhetsnr = tildeltEnhetsnr,
                    journalpostId = journalpostId,
                )

            OppgaveKilde.INFOTRYGD ->
                Oppgave.Infotrygd(
                    eksternReferanseId = eksternReferanseId,
                    personident = ident,
                    beskrivelse = beskrivelse,
                    journalpostId = journalpostId,
                )
        }

    return OppgaveRequest.Post(
        oppgave = oppgave,
        aktivDato = aktivDato,
        fristFerdigstillelse = fristFerdigstillelse,
    )
}
