# Rechtsseiten — offene Punkte

Platzhalter ausfüllen in:
`apps/portal-app/src/core/legal/legal.config.ts`

## Impressum

- Ladungsfähige Anschrift eintragen (Postfach reicht NICHT).
  Falls die Privatadresse vermieden werden soll: vorab anwaltlich klären,
  ob das Angebot überhaupt impressumspflichtig ist und welche Alternativen es gibt.
- Telefon / Kontaktformular als zweite Kontaktmöglichkeit ist empfohlen, aber nicht zwingend.
- Lizenz des Quellcodes eintragen (z. B. MIT) und GitHub-Link prüfen.

## Datenschutzerklärung

- Hosting bei Hetzner (DE): Auftragsverarbeitungsvertrag (AVV) im Hetzner-Konto
  abschließen, falls noch nicht geschehen.
- Echte Löschfrist für Server-Logfiles eintragen (Abschnitt 4).
- Echte Löschfrist / Löschbedingung für Spielernamen eintragen (Abschnitt 5).
- Prüfen: Werden wirklich KEINE Analyse-/Tracking-Tools oder externen Fonts/CDNs
  geladen? Falls doch, eigenen Abschnitt ergänzen.
- Prüfen: Werden wirklich nur Spielernamen gespeichert (keine E-Mail, keine Konten)?
  Falls Konten dazukommen, Abschnitt 5 erweitern.
- Empfehlung: Pseudonyme statt Klarnamen ermöglichen → senkt den Personenbezug.

## Stand-Datum

`legalDate` in `legal.config.ts` auf den aktuellen Monat/Jahr setzen, sobald die
Texte fertiggestellt sind.
