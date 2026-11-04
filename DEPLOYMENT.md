# Free deployment

This setup uses Vercel for the Vite frontend, Render for the Spring Boot API,
and Neon for PostgreSQL. Vercel's Hobby plan is for personal, non-commercial
projects. Check each provider's current limits before deploying.

## 1. Create the Neon database

Create a Neon project and copy its connection details. Keep the hostname,
database name, username, and password private. Neon currently lists a 1 GB
storage limit and 100 compute-hours per month on its Free plan; free projects
are not automatically removed for inactivity.

## 2. Deploy the API on Render

Create a Render Blueprint from this repository and select `render.yaml`. The
Blueprint creates the free API service. Supply these prompted values using the
Neon connection details:

- `DB_URL`: `jdbc:postgresql://<neon-host>/<database>?sslmode=require`
- `DB_USERNAME`: Neon database username
- `DB_PASSWORD`: Neon database password
- `SNAPIT_CORS_ALLOWED_ORIGINS`: the exact Vercel site origin, such as
  `https://your-project.vercel.app` (no trailing slash)

Render generates `SNAPIT_JWT_SECRET` for the API. Keep the same generated secret
when redeploying so issued tokens remain valid.

The API starts at `https://snapit-api.onrender.com` unless the Render service
name is changed. Its free instance sleeps after 15 minutes without traffic, so
the first request after inactivity can take about a minute. Its local
filesystem is temporary; uploaded images and PDFs do not survive an instance
restart or redeploy.

The backend's Flyway baseline migration creates the schema on the new, empty
Neon database. Do not point the deployment at a database containing unrelated
tables without reviewing the migration and baseline behavior first.

## 3. Deploy the frontend on Vercel

Import this repository into Vercel and set the project root directory to
`frontend`. Vercel detects Vite; use `npm run build` and `dist` if prompted.
Set the `VITE_API_BASE_URL` environment variable to the Render API origin, for
example `https://snapit-api.onrender.com`, then deploy.

Use the final Vercel deployment origin as Render's
`SNAPIT_CORS_ALLOWED_ORIGINS` value. If the Vercel project or domain changes,
update this value and redeploy the API.
