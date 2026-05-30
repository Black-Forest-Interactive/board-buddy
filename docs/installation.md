# Self-Hosting Board Buddy

This guide covers running your own instance of Board Buddy. The easiest way to try it is the hosted version at [blackforrestdevelopment.de](https://blackforrestdevelopment.de) — self-hosting is only necessary if you want full control over the data or run the app in a private network.

> **Note:** This is a work-in-progress stub. Detailed setup instructions will be added here. Feel free to [open an issue](../../issues) if you need help getting started.

---

## Prerequisites

- Docker and Docker Compose
- A domain or local hostname (optional, but recommended for QR-code sharing to work over the network)

---

## Architecture overview

Board Buddy consists of two services:

| Service | Description |
|---|---|
| **Backend** | Spring Boot / Kotlin REST API |
| **Frontend** | Angular 21 SPA served via Nginx |

Both are packaged as Docker images and can be run with Docker Compose.

---

## Quick start (placeholder)

```bash
# 1. Clone the repository
git clone https://github.com/<org>/board-buddy.git
cd board-buddy

# 2. Copy and adjust the environment file
cp .env.example .env
# Edit .env with your database credentials and base URL

# 3. Start the services
docker compose up -d
```

Once running, open `http://localhost` (or your configured domain) in a browser.

---

## Configuration (placeholder)

Key environment variables will be documented here:

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | JDBC connection string | — |
| `DB_USERNAME` | Database user | — |
| `DB_PASSWORD` | Database password | — |
| `BASE_URL` | Public base URL of the app | `http://localhost` |

---

## Updating

```bash
docker compose pull
docker compose up -d
```

Database migrations run automatically on startup.

---

## Legal pages (Impressum & Datenschutz)

The public repository ships with placeholder stubs for `/impressum` and `/datenschutz`.
If you self-host Board Buddy you are responsible for providing your own legal content.

Replace the following three files with your own content before building:

```
frontend/apps/portal-app/src/core/impressum/impressum.component.html
frontend/apps/portal-app/src/core/datenschutz/datenschutz.component.html
frontend/apps/portal-app/src/core/legal/legal.config.ts
```

The `legal.config.ts` contains all operator-specific values (name, address, e-mail, retention periods).
The two HTML files contain the actual legal text rendered on the pages.

---

## Support

If you run into issues, please [open a GitHub issue](../../issues) with your Docker version, OS, and any relevant log output.

---

*Back to [README](../README.md)*
