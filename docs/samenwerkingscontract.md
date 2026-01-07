# Samenwerkingscontract

Dit document beschrijft hoe wij samenwerken: wat we doen, hoe we werken, wie welke rol heeft en wanneer we iets opleveren. Het is een levend document; wijzigingen gebeuren in overleg en via een pull request.

## 1. Doel en scope

Dit contract geldt voor ons Elections-project. We zetten de projectstructuur op vanaf de basis conform de studiematerialen (studiemanual/DLO) en werken iteratief richting een werkende oplossing. Dit document borgt teamafspraken over samenwerking, kwaliteit en oplevering.

## 2. Inhoudsopgave

- [Samenwerkingscontract](#samenwerkingscontract)
  - [1. Doel en scope](#1-doel-en-scope)
  - [2. Inhoudsopgave](#2-inhoudsopgave)
  - [3. Teamleden (wie)](#3-teamleden-wie)
  - [4. Vergadertijden (wanneer)](#4-vergadertijden-wanneer)
  - [5. Rollen (hoe)](#5-rollen-hoe)
    - [5.1 Scrum Master verdeling per sprint (vanaf sprint 2)](#51-scrum-master-verdeling-per-sprint-vanaf-sprint-2)
    - [5.2 Taken van de Scrum Master](#52-taken-van-de-scrum-master)
  - [6. Afspraken (wat en hoe)](#6-afspraken-wat-en-hoe)
    - [6.1 Aanwezigheid](#61-aanwezigheid)
    - [6.2 Commits en commitconventies](#62-commits-en-commitconventies)
    - [6.3 Verantwoordelijkheden](#63-verantwoordelijkheden)
    - [6.4 Communicatie](#64-communicatie)
    - [6.5 Verwachtingen](#65-verwachtingen)
    - [6.6 Werktijden](#66-werktijden)
    - [6.7 Deadlines](#67-deadlines)
  - [7. Definition of Done (DoD)](#7-definition-of-done-dod)
  - [8. Sancties (bij structurele niet-naleving)](#8-sancties-bij-structurele-niet-naleving)
  - [9. Werkwijze (samenvatting)](#9-werkwijze-samenvatting)
  - [10. Wijzigingen aan dit contract](#10-wijzigingen-aan-dit-contract)
  - [11. Akkoordverklaring](#11-akkoordverklaring)

## 3. Teamleden (wie)

- Dominik Krystul
- Milan van Dongen
- Aydin Maleki
- Wessel Willemsen
- Akif Göge

## 4. Vergadertijden (wanneer)

- Daily stand-up: elke schooldag in de eerste 5 minuten van de les (maximaal 5 minuten)
- Sprint planning: op de eerste sprintdag (dinsdag) direct na de daily stand-up (±30–60 min)
- Backlog refinement: vrijdag 12:30–13:00
- Sprint review & retrospective: laatste dag van de sprint

Assumptie: tijden zijn aanpasbaar aan rooster/locatie; wijzigingen worden in de stand-up bevestigd.

## 5. Rollen (hoe)

- Scrum Master: rouleert per sprint (vanaf sprint 2, zie verdeling hieronder)
- Development Team: alle teamleden
- Product Owner: opdrachtgever/docent (voor prioritering en acceptatie)

### 5.1 Scrum Master verdeling per sprint (vanaf sprint 2)

| Sprint | Scrum Master       |
|-------:|--------------------|
| 2      | Milan van Dongen    |
| 3      | Wessel Willemsen   |
| 4      | Aydin Maleki       |
| 5      | Dominik Krystul    |
| mogelijke sprint 6  | Akif Göge          |

NB: Indien het aantal sprints wijzigt, zet de rotatie door in dezelfde volgorde.

### 5.2 Taken van de Scrum Master

- Faciliteert ceremonies: stand-up, planning, review en retro.
- Verwijdert impediments of regelt hulp zodat het team door kan.
- Bewaakt voortgang (board/burndown) en maakt blockers zichtbaar.
- Helpt het team de afgesproken werkwijze en kwaliteitsafspraken te volgen.

## 6. Afspraken (wat en hoe)

### 6.1 Aanwezigheid

- Iedereen is aanwezig bij stand-ups, ceremonies en geplande werksessies, tenzij vooraf gemeld en afgestemd.
- Afwezig of te laat? Meld dit zo snel mogelijk vóór aanvang van de les, met reden en verwacht tijdstip van aansluiting.
- > 15 minuten te laat zonder bericht telt als afwezig voor de stand-up.
- Inhalen: gemiste besluiten/acties worden zelfstandig nagelezen (notes/board) en bevestigd in het kanaal.

### 6.2 Commits en commitconventies

- Minimaal 1 betekenisvolle commit per werkdag per ontwikkelaar op een feature/bugfix branch.
- Branch-conventie: `feature/<korte-omschrijving>` of `fix/<issue-id>-<korte-omschrijving>`.
- Pull/Merge Requests verwijzen naar issue(s) en bevatten een korte beschrijving, checklist en testnotities.
 - Voer lokaal een snelle build/test uit vóór je pusht zodat de CI groen blijft.
 - Bespreek grotere of breaking wijzigingen vooraf in Discord en in het issue.

Schrijf commits volgens (lichte) Conventional Commits:

1. Titelregel (max ~50 tekens) met type-prefix:
	- `feat:` nieuwe functionaliteit
	- `fix:` bugfix
	- `docs:` documentatie
	- `refactor:`, `test:`, `chore:` etc.
2. Body (optioneel): leg uit wat en waarom, eventuele technische context of beperkingen.
3. Footer (optioneel): referenties en impact
	- Issue-verwijzing: `Closes #123` of `Refs #123`
	- Breaking change: `BREAKING CHANGE: beschrijf impact en migratiestappen`

Goede voorbeelden:

```
feat: valideer gebruikersinvoer op client

Voegt inline validatie toe voor verplichte velden en e-mailformaat.
Voorkomt foutieve data richting backend.

Closes #42
```

```
fix: voorkom NPE in PlayerSessionService bij null token

Check op null en werp duidelijke IllegalArgumentException met boodschap.
Voegt unit test toe voor edge cases.

Refs #57
```

Slechte voorbeelden om te vermijden:
- `update` / `fixes` zonder context
- Grote verzamelcommits met meerdere ongerelateerde wijzigingen
- Werk-in-uitvoering commits zonder beschrijving

### 6.3 Verantwoordelijkheden

- Iedereen is eigenaar van zijn taken en houdt de board-status actueel (Backlog → Doing → Verify → Done).
- Code reviews: minimaal 1 reviewer; de reviewer checkt functioneel en codekwaliteit
- Kwaliteit: houdt je aan afgesproken code style en Definition of Done (zie hieronder).
- Kennisdeling: korte demo of uitleg bij grotere wijzigingen in review of stand-up.
 - Module-eigenaarschap: teamleden nemen verantwoordelijkheid voor toegewezen onderdelen (bijv. parser/converters, validatie, API/endpoints, data-visualisaties, import/export tooling) en zorgen voor tijdige integratie.

### 6.4 Communicatie

- Primair: Discord-server (team). Kanaalgebruik:
	- `#announcements`: belangrijke updates, planning, besluiten (alleen SM/PO posten)
	- `#stand-up`: daily updates in format: gister / vandaag / blokkades
	- `#dev`: technische vragen en discussies
	- `#review`: PR-links en reviewverzoeken (koppel issue/PR)
	- `#planning`: agenda’s, ceremonies en afstemming met PO
	- `#voice-huddle`: ad-hoc overleg (max 10 min); besluit kort samenvatten in een thread
	- `#random`: informeel/overig
- Reactietijden: tijdens werktijden binnen 2 uur op mentions; buiten schooltijden op vrijwillige basis.
- Zichtbaarheid: zet je status op aanwezig/afwezig; meld focus-blokken (geen notificaties) in `#announcements` of je thread.


### 6.5 Verwachtingen

- Werkt taakgericht; communiceert vroeg over blokkades en vraagt hulp na > 30 minuten vastlopen.
- Schrijf waar zinvol unit/integration tests voor nieuwe functionaliteit of kritieke paden.
- Houdt documentatie bij (README, docs, comments) als onderdeel van de oplevering.

### 6.6 Werktijden

- We volgen het officiële rooster. Les-/projectblokken worden benut voor teamwerk en afstemming; tijden zijn per dag verschillend.
- Zelfstudieblokken zijn werktijd voor dit project: je werkt thuis of op school aan toegewezen items en houdt zichtbare voortgang (board + commits).
- Pauzes plan je zelf binnen de blokken; stem af bij gezamenlijke sessies of ceremonies.

### 6.7 Deadlines

- Sprintdoelen zijn de harde deadlines; tussen-deadlines worden in de planning en issues vastgelegd.
- PR freeze: op de dag van de sprint-review bij voorkeur vóór de les, alle feature-PRs gemerged of klaar voor demo.

## 7. Definition of Done (DoD)

Een item is klaar wanneer:

- [ ] De functionaliteit voldoet aan de afgesproken acceptatiecriteria.
- [ ] Er is minimaal één review gedaan; de PR is goedgekeurd en gemerged naar de juiste branch.
- [ ] De CI-build is groen.
- [ ] Linting en formatting zijn toegepast volgens de projectafspraken.
- [ ] Relevante tests zijn aanwezig en succesvol (unit- of integratietests) voor nieuwe of aangepaste paden.
- [ ] Documentatie is bijgewerkt waar nodig (bijv. README of API-beschrijving).
- [ ] De wijziging is demobaar en kort toegelicht (link of screenshot) en wordt meegenomen in de sprintreview.
- [ ] Eventuele configuratie of migratiestap is uitgevoerd en beschreven voor het team.
- [ ] Er staan geen losse TODO’s, dode code of onverklaarde warnings meer open.

## 8. Sancties (bij structurele niet-naleving)

Doel: helpen herstellen én het sprintdoel borgen. Maatregelen zijn geen straf, maar een middel om het team op koers te houden.

Wanneer grijpen we in:
- Herhaald te laat/afwezig zonder vooraf te melden.
- Afspraken niet nakomen (commits, reviews, ceremonies) ondanks eerdere afspraak.
- Niet reageren op blockers binnen werktijden.
- Kwaliteitsproblemen laten bestaan (rode CI, kapotte tests) zonder actie.

Stappenplan:
1) Signaal en hulp
	- Scrum Master spreekt de persoon aan (kort 1-op-1 of in stand-up).
	- Doel: oorzaak begrijpen, blokkades wegnemen.
	- Noteer een korte samenvatting in de relevante issue/thread.
2) Verbeterafspraak met termijn
	- Concrete acties, meetbaar resultaat en duidelijke deadline (1–3 werkdagen).
	- Afspreken hoe en wanneer we checken.
3) Herverdeling en borging
	- Taak/ownership tijdelijk herverdelen om sprintdoel te halen.
	- Buddy aanwijzen voor ondersteuning en kennisborging.
4) Escalatie
	- Naar PO/docent met feiten: wat, wanneer, impact, genomen stappen en voorstel.
5) Gevolgen beoordeling
	- Conform cursusafspraken; kan invloed hebben op individuele beoordeling.

Registratie en transparantie:
- Leg afspraken en voortgang vast in het issue/PR of een Discord-thread.
- Team-brede besluiten samenvatten in `#announcements`.

Herstel:
- Bij zichtbaar herstel vervallen extra maatregelen. We evalueren kort in de retro.


## 9. Werkwijze (samenvatting)

1) Intake en planning
	- Items staan in GitHub (board). We schrijven duidelijke titels en acceptatiecriteria.
	- In sprint planning kiezen we doelen; refinement gebruikt we om scope en afhankelijkheden te verhelderen.

2) Oppakken en zichtbaar maken
	- Claim je item, zet status op Doing, start een branch: `feature/...` of `fix/...`.
	- Meld blokkades direct in Discord (`#dev` of thread). Wacht niet langer dan 30 min.

3) Bouwen en testen
	- Werk in kleine stappen en commit regelmatig met duidelijke messages.
	- Schrijf/actualiseer tests voor nieuwe of kritieke paden.
	- Houd de build groen; fix rood eerst.

4) Review en merge
	- Open een PR met korte beschrijving en testnotities.
	- Minimaal één review. Verwerk feedback snel en netjes.
	- Merge na goedkeuring en groene CI.

5) Demo en borging
	- Toon de wijziging (screenshot/link) in review of retro.
	- Update documentatie wanneer gedrag of API is veranderd.

Richtlijnen
- Kleine PR’s hebben voorrang (snellere review, minder risico).
- Maximaal 2 actieve items per persoon (focus, minder contextswitching).
- Besluiten leggen we vast in issue/PR of in een Discord-thread met samenvatting.

## 10. Wijzigingen aan dit contract

Wijzigingen worden voorgesteld via PR op dit document en bekrachtigd in de eerstvolgende stand-up of retro.

## 11. Akkoordverklaring

Door ondertekening bevestigen teamleden dit samenwerkingscontract te volgen.

- Dominik Krystul — Datum: 10 / 09 / 2025
- Milan van Dongen — Datum: 10 / 09 / 2025
- Aydin Maleki — Datum: 10 / 09 / 2025
- Wessel Willemsen — Datum: 10 / 09 / 2025
- Akif Göge — Datum: 10 / 09 / 2025
