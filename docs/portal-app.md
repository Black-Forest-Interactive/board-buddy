# Using Board Buddy — Portal App

Board Buddy is designed to be picked up without any tutorial. This guide walks through everything step by step, from creating your account to resolving your first battle.

> **Live version:** [blackforrestdevelopment.de](https://blackforrestdevelopment.de)

---

## Table of Contents

1. [Creating your account](#1-creating-your-account)
2. [The home screen](#2-the-home-screen)
3. [Creating a session](#3-creating-a-session)
4. [Joining a session](#4-joining-a-session)
5. [Inside a session — the lobby](#5-inside-a-session--the-lobby)
6. [Building your army](#6-building-your-army)
7. [Researching technologies](#7-researching-technologies)
8. [Combat](#8-combat)

---

## 1. Creating your account

When you open Board Buddy for the first time you are asked for a **display name**. This is the name other players will see during sessions.

- Enter a name of at least 3 characters
- Tap **Start** — no email or password required

Your player profile is stored in the app. You can view it at any time from the navigation menu.

---

## 2. The home screen

After logging in you land on the **Home** screen. From here you can:

- **Create a new session** — start a new game as the host
- **Join a session** — enter a code to join an existing game
- **Resume a session** — your active sessions are listed below the action cards; tap any of them to jump back in

If a session has an active battle in progress, a crossed-swords icon is shown on its card.

---

## 3. Creating a session

Tap **Create Session** on the home screen and fill in the form:

| Field | What to enter |
|---|---|
| **Name** | A label for this game session (e.g. "Friday night Civ") |
| **Game** | Select the board game variant |
| **Rule Set** | Choose the rule set that governs unit stats and tech effects |
| **Nation** | Pick your civilization — the flag and name are shown to all players |

Tap **Create** to start the session. You become the **host** and are taken directly to the session lobby.

As the host you can share the session with other players via:

- **QR code** — tap the QR button in the top bar; others can scan it with their phone
- **Copy link / join code** — tap the copy button; paste or share the code manually

---

## 4. Joining a session

Tap **Join Session** on the home screen. If you have not set a display name yet, you will be asked for one here.

Fill in:

| Field | What to enter |
|---|---|
| **Join code** | The code or link the host shared with you |
| **Nation** | Pick your civilization |

Tap **Join** — you are taken straight into the session lobby.

---

## 5. Inside a session — the lobby

The lobby is the main session screen. It shows a card for every participant with their **nation flag**, **player name**, **government type**, and a host badge for the session creator.

**Your own card** has two action buttons:
- **Army** — go to your army management view
- **Research** — go to the technology tree

**Other players' cards** have a **Start Battle** button that lets you initiate combat against that player (see [Combat](#8-combat)).

The host can refresh the participant list at any time using the reload button. During a battle the lobby switches to the battle view automatically; it returns once the battle is concluded.

---

## 6. Building your army

Navigate to **Army** from your player card in the lobby.

The army screen shows a card for every available unit type (Infantry, Artillery, Cavalry, Planes, …). A unit type becomes available once you have researched the corresponding technology.

**Reading a unit card:**

- The **level badge** (top-right corner) shows the current upgrade level from your tech tree
- The **HP range** and **damage range** show the minimum and maximum values that can be rolled when a unit of this type is created
- Units you have already produced are listed inside the card with their individual HP and damage pips

**Producing a unit:**

1. Tap **Produce Unit** on the unit type you want to build
2. A new unit is created with randomised HP and damage values within the range for its current level
3. The unit appears in the card's list and is ready to be deployed in battle

Units that have not been unlocked via research are shown greyed out and cannot be produced.

---

## 7. Researching technologies

Navigate to **Research** from your player card in the lobby.

Technologies are arranged in **tiers** (I, II, III, …). Use the sticky tier navigation at the top to jump to a specific tier.

**Reading a technology card:**

- **Name and image** identify the technology
- The **description** explains its effect in game terms
- The **effects panel** at the bottom lists which unit types are upgraded and to which level once this tech is discovered

**Researching a technology:**

- Technologies that are available to research (prerequisites met) are fully visible and have an active **Research** button
- Technologies whose prerequisites are not yet met are dimmed and cannot be selected
- Already-discovered technologies are highlighted with a green checkmark and ring — their Research button is hidden

Tap **Research** (or tap the card itself) to discover the technology. The affected unit types are immediately upgraded to the new level, which is reflected in your army view.

Use the **eye toggle** in the top bar to hide technologies you have already discovered and focus on what remains.

---

## 8. Combat

### Starting a battle

From the lobby, tap **Start Battle** on another player's card. A dialog appears where you configure the battle:

| Field | Description |
|---|---|
| **Attacker** | Pre-filled with your name |
| **Defender** | Pre-filled with the target player's name |
| **Attacker army count** | How many units the attacker draws from their army |
| **Defender army count** | How many units the defender draws from their army |
| **Walled city** | Check this if the defending city has a wall (affects combat rules) |

Tap **Start Battle** to confirm. Both players are now in combat mode.

### The battle screen

The battle screen is divided into four sections:

**Turn banner** — shows whose turn it currently is. When it is your turn, the banner is highlighted. When the battle is over, a winner banner (or draw) is shown instead.

**Opponent info** — a card showing the opposing player's name, their nation flag, how many units they have in hand, and how many are deployed on fronts.

**Fronts** — each front is a face-off between one of your units and one of the opponent's. Fronts are displayed as two-column grids: your side on the left, the opponent's on the right. The health of each unit is shown as a row of green pips; depleted units are struck through.

**Reserve units** — your units that are not yet deployed. During your turn, tap a unit to select it. Two things you can then do:
- **Deploy to a new front** — tap **Deploy** on the selected unit to open a new front
- **Attack an existing front** — tap **Attack** on a front where your opponent has a unit but you do not

**Battle log** — a chronological record of every action taken during the battle (deployments, attacks, damage dealt).

### Taking a turn

1. **Select a unit** from your reserve by tapping its card
2. Either **Deploy** it (opens a new front) or **Attack** an existing open front
3. The turn passes to your opponent

Repeat until all reserve units are placed or one side's units are all defeated.

### Ending a battle

Once there are no more moves possible, a winner (or draw) is declared and shown in the banner. The **host** taps **Finish** to close the battle and return everyone to the lobby. The host can also **Cancel** a battle at any point to abort it without a result.

---

*Back to [README](../README.md)*
