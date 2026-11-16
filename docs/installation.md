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
| `BOARD_BUDDY_BASE_URL` | Public base URL of the portal, used for join links, QR codes and link previews | `https://blackforrestdevelopment.de` |
| `KEYCLOAK_JWKS_URL` | JWKS endpoint of the Keycloak realm used by the admin app | hosted instance |
| `KEYCLOAK_ISSUER` | Expected `iss` claim of admin tokens (realm URL) | hosted instance |
| `KEYCLOAK_ADMIN_CLIENT` | Keycloak client id of the admin app, checked against the `azp` claim | `board-buddy-admin` |
| `KEYCLOAK_ORIGIN` | Keycloak origin allowed by the admin app's Content-Security-Policy (admin frontend container) | `https://auth.blackforrestdevelopment.de` |

Admin users need the Keycloak realm role `board-buddy-admin`.

Both frontend containers run as a non-root user and listen on port **8080**.

If you serve the portal over plain `http` on another host than `localhost`, set `BOARD_BUDDY_PORTAL_COOKIE_SECURE=false`
(maps to `board-buddy.portal.cookie-secure`), otherwise browsers drop the player cookie. Do not do this in production.

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
