# AL Carbon Calculator

Backend for a carbon footprint calculator built with Java 17, Spring Boot and MongoDB.

## Endpoints

### `POST /open/start-calc`

Registers a new calculation session with the user basic information.

**Request body** (all fields mandatory):

```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "phoneNumber": "11999999999",
  "uf": "SP"
}
```

**Response** – `201 Created`:

```json
{ "id": "<calculation-id>" }
```

---

### `PUT /open/info`

Sets (or overwrites) the consumption data for an existing calculation.

**Request body** (all fields mandatory; `recyclePercentage` is a double `0.0–1.0`):

```json
{
  "id": "<calculation-id>",
  "energyConsumption": 150.0,
  "transportation": [
    { "type": "CAR", "monthlyDistance": 500.0 }
  ],
  "solidWasteTotal": 30.0,
  "recyclePercentage": 0.4
}
```

**Response** – `200 OK`:

```json
{ "success": true }
```

---

### `GET /open/result/{id}`

Returns the calculated carbon emissions for the given calculation.

**Response** – `200 OK`:

```json
{
  "energy": 12.3,
  "transportation": 45.6,
  "solidWaste": 7.8,
  "total": 65.7
}
```

---

### `GET /status/check`

Health-check endpoint. Returns application version and current timestamp.

## Calculator logic

Emission factors are pre-loaded from the database (seeded via `init-mongo.js`).

| Category | Formula |
|---|---|
| Energy | `energyConsumption × EnergyEmissionFactor(uf)` |
| Transportation | `Σ monthlyDistance × TransportationEmissionFactor(type)` |
| Solid waste | `solidWasteTotal × recyclePercentage × recyclableFactor + solidWasteTotal × (1 - recyclePercentage) × nonRecyclableFactor` |
| Total | sum of all three |

## Validation and error handling

| Condition | HTTP status |
|---|---|
| Missing or invalid request fields | `400 Bad Request` |
| Calculation ID not found | `404 Not Found` |
| Missing emission factor for UF or transport type | `422 Unprocessable Entity` |
| Unexpected server error | `500 Internal Server Error` |

Field-level validation enforced:

- `name`, `email`, `phoneNumber`, `id` — required, non-blank
- `email` — valid e-mail format
- `uf` — exactly 2 letters (Brazilian state code)
- `energyConsumption`, `solidWasteTotal`, `monthlyDistance` — ≥ 0
- `recyclePercentage` — `0.0` to `1.0`

## Running locally

### 1. Start MongoDB

```bash
docker compose up -d
```

The database is seeded automatically from `init-mongo.js` on first start.  
To reset it: `docker compose down -v && docker compose up -d`.

### 2. Build and run

```bash
./gradlew bootRun
```

The application starts on **http://localhost:8085**.  
Swagger UI is available at **http://localhost:8085/swagger-ui.html**.

### 3. Run tests

```bash
./gradlew clean test
```

HTML test report: `build/reports/tests/test/index.html`

## Project structure

```
src/main/java/br/com/actionlabs/carboncalc/
├── config/          # Spring and OpenAPI/Swagger configuration
├── dto/             # Request/response DTOs (with Bean Validation)
├── enums/           # TransportationType enum
├── exception/       # Custom exceptions and global exception handler
├── model/           # MongoDB documents (CarbonCalculation, emission factors)
├── repository/      # Spring Data MongoDB repositories
├── rest/            # REST controllers
└── service/         # CarbonCalculationService interface and implementation
```
