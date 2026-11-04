# SnapIt backend API

Base URL: `http://localhost:8080`

All routes require `Authorization: Bearer <accessToken>` except registration and login. JSON timestamps use ISO-8601 local date-time strings. Paginated endpoints return:

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

## Local configuration

`SNAPIT_JWT_SECRET` is required when the application starts. `JwtService` expects Base64 that decodes to at least 32 cryptographically random bytes. Generate and store a developer-local user environment variable in PowerShell (do not put it in source control):

```powershell
$bytes = New-Object byte[] 32
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$secret = [Convert]::ToBase64String($bytes)
[Environment]::SetEnvironmentVariable('SNAPIT_JWT_SECRET', $secret, 'User')
$env:SNAPIT_JWT_SECRET = $secret
```

Use the current terminal for runs started from that terminal; restart IntelliJ after setting a user-level variable so its launched processes inherit it. Tests use a fixed, test-only key and do not require a developer secret.

Database settings are `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. `SNAPIT_CORS_ALLOWED_ORIGINS` is a comma-separated origin allowlist and defaults to `http://localhost:5173`. Configure production origins explicitly. CORS allows API methods `GET, POST, PUT, PATCH, DELETE, OPTIONS` and headers `Authorization, Content-Type`; credentials are disabled.

## Authentication

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/api/auth/register` | `{"username":"sam","email":"sam@example.com","password":"StrongPass123!"}` | `201` user `{id, username, email, role, createdAt}`; new users receive Free plan |
| POST | `/api/auth/login` | `{"email":"sam@example.com","password":"StrongPass123!"}` | `200` `{accessToken, tokenType, id, username, email}` |

## Saved items

| Method | Path | Request/parameters | Response |
|---|---|---|---|
| POST | `/api/saved-items` | `{"title":"Note","contentType":"TEXT","description":"Optional","extractedText":"Text","sourceUrl":null,"language":null}` | `201` plain-text save confirmation with ID |
| GET | `/api/saved-items` | `page=0&size=10&sortBy=createdAt&direction=DESC` | `200` paged saved-item responses |
| GET | `/api/saved-items/{id}` | — | `200` saved-item response |
| PUT | `/api/saved-items/{id}` | Saved-item JSON as above | `200` saved-item response |
| DELETE | `/api/saved-items/{id}` | — | `200` plain-text confirmation |
| GET | `/api/saved-items/search` | Optional `keyword`, `contentType`, `favorite`, `tagId`, `page`, `size`, `sortBy`, `direction` | `200` paged saved-item responses |
| GET | `/api/saved-items/favorites` | `page`, `size`, `sortBy`, `direction` | `200` paged saved-item responses |
| PUT | `/api/saved-items/{id}/favorite` | `{"favorite":true}` | `200` saved-item response |
| GET | `/api/saved-items/{id}/file` | — | `200` stored file served inline with its detected media type (for PDF/image previews) |
| DELETE | `/api/saved-items/{id}/file` | — | `204`; clears stored-file reference |

The saved-item response contains `id`, `title`, `sourceUrl`, `contentType`, `description`, `extractedText`, `processingStatus`, `favorite`, `language`, `createdAt`, and `updatedAt`; it never includes the local storage path. Sort fields are `createdAt`, `updatedAt`, `title`, `contentType`, and `favorite`; page size is limited to 100. Content type values are `WEBPAGE`, `IMGAGE` (legacy enum spelling), `TEXT`, and `PDF`.

## Extraction and uploads

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/api/webpages` | `{"url":"https://example.com"}` | `201` saved-item response |
| POST | `/api/snippets` | `{"title":"Example","content":"...","description":"...","language":"java"}` | `201` saved-item response |
| POST | `/api/images` | multipart form field `file` | `201` saved-item response after OCR |
| POST | `/api/pdfs` | multipart form field `file` | `201` saved-item response after text extraction |

Image and PDF uploads are limited to 10 MiB. Invalid or empty uploads return a JSON error.

## Collections and tags

| Method | Path | Request/parameters | Response |
|---|---|---|---|
| POST | `/api/collections` | `{"name":"Reading","description":"..."}` | `201` collection response |
| GET | `/api/collections` | `page`, `size` | `200` paged collection responses |
| PUT | `/api/collections/{collectionId}` | Collection JSON | `200` collection response |
| DELETE | `/api/collections/{collectionId}` | — | `204` |
| PUT / DELETE | `/api/collections/{collectionId}/items/{savedItemId}` | — | `204` add/remove item |
| GET | `/api/collections/{collectionId}/items` | `page`, `size` | `200` paged saved-item responses |
| POST | `/api/tags` | `{"name":"topic"}` | `201` tag response |
| GET | `/api/tags` | `page`, `size` | `200` paged tag responses |
| PUT | `/api/tags/{tagId}` | `{"name":"topic"}` | `200` tag response |
| DELETE | `/api/tags/{tagId}` | — | `204` |
| PUT / DELETE | `/api/tags/{tagId}/items/{savedItemId}` | — | `204` add/remove item |
| GET | `/api/tags/{tagId}/items` | `page`, `size` | `200` paged saved-item responses |

## Reminders

| Method | Path | Request/parameters | Response |
|---|---|---|---|
| POST | `/api/reminders` | `{"savedItemId":1,"remindAt":"2030-01-01T09:00:00","message":"Review"}` | `201` reminder response |
| GET | `/api/reminders` | Optional `completed`, `page`, `size` | `200` paged reminder responses |
| GET | `/api/reminders/due` | `page`, `size` | `200` paged due, incomplete reminders for current user |
| PUT | `/api/reminders/{reminderId}` | Reminder JSON as above | `200` reminder response |
| PATCH | `/api/reminders/{reminderId}/completion` | `{"completed":true}` | `200` reminder response |
| DELETE | `/api/reminders/{reminderId}` | — | `204` |

Reminder times must be in the future when created or updated. Due reminders are exposed for client polling; no push/email delivery is configured.

## Profiles, sharing, and subscriptions

| Method | Path | Request/parameters | Response |
|---|---|---|---|
| GET | `/api/profiles/{username}` | — | `200` `{username, memberSince}` |
| POST | `/api/shares` | `{"savedItemId":1,"recipientUsername":"sam"}` | `201` `{id, savedItemId, savedItemTitle, recipientUsername, sharedAt}` |
| GET | `/api/shares/sent` | `page`, `size` | `200` paged share responses |
| GET | `/api/shares/received` | `page`, `size` | `200` paged saved-item responses |
| GET | `/api/shares/received/items/{savedItemId}` | — | `200` saved-item response, only while shared |
| DELETE | `/api/shares/{shareId}` | — | `204`; only the owner may revoke |
| GET | `/api/subscriptions/me` | — | `200` `{plan, startedAt, expiresAt}` |
| PUT | `/api/subscriptions/{userId}` | `{"plan":"PREMIUM","expiresAt":"2030-01-01T00:00:00"}` | `200` subscription response; ADMIN role only |

The profile is authenticated and intentionally exposes no email or saved content. Subscription-plan changes are admin-provisioned; payment processing and plan-specific usage limits are not configured.

## Error responses

Errors use `{ "message": "...", "fieldErrors": {} }`. Validation failures use `"message":"Validation failed."` and put field messages in `fieldErrors`. Statuses include `400` invalid input, `401` missing/invalid authentication, `403` forbidden, `404` resource unavailable to that user, `409` duplicate resource, `413` upload too large, `502` webpage fetch failure, and `500` file-storage failure.

All saved-item, collection, tag, reminder, file, and share operations are scoped to the authenticated user. A user receiving a share can read the shared item but does not gain access to the owner's other items or mutation endpoints.

## Database rollout note

The manual SQL files in `src/main/resources/db/manual-migrations/` are not run automatically by Spring Boot. They were applied to the configured local `snapitDB` after a read-only check found 11 legacy saved items and no users. Those 11 rows were preserved with `user_id = NULL`, not assigned to a user, and remain inaccessible through owner-scoped APIs. The application uses Hibernate `validate`; it will not create or alter schema on startup.
