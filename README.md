# CineRate

CineRate is a movie rating and review application built by our group for the Software Architectures course
(HEC Lausanne, UNIL, 2026). Registered users browse a catalogue of movies, rate them from 1 to 10 and review
them (one review per user and movie), keep a personal watchlist, and see the average rating of every movie
(e.g. "8.7 / 10"). Movie data will be imported from The Movie Database (TMDB), and an advanced search finds
movies by title, actor, director, genre or theme keywords.

The application is built incrementally:

1. **Phase 1**: domain model (`CineRateDomain`) and service layer (`CineRateService`)
2. **Phase 2**: web frontend
3. **Phase 3**: persistence in a relational database

## Modules

| Module | Content |
|---|---|
| `CineRateDomain/` | Plain Java domain model (`ch.unil.doplab.cinerate.domain`): `User`, `Movie`, `Review`, `Person`, `Actor`, `Director`, `Genre`, `Rating`, `Utils`, `ExceptionDescription`, with JUnit 5 tests |
| `CineRateService/` | REST web service (Jakarta EE, deployed on Payara): `ApplicationState` keeps the data in memory, one `Resource` class per domain object, errors returned as JSON. Users CRUD available under `/api/users` |

## Build and test

Requirements: Java 17 or later and Maven 3.

```
cd CineRateDomain
mvn test       # compile and run the unit tests
mvn install    # install the domain jar in the local Maven repository, for use by CineRateService
```

## Run the web service

Requirements: Java 17 for the code (IntelliJ uses JDK 21 to talk to Payara 7), IntelliJ IDEA Ultimate with the Payara plugin,
Docker Desktop and Postman.

1. **Domain**: open `CineRateDomain` in IntelliJ, then in the Maven tab run `clean` and `install`.
   Redo it after every change in the domain, otherwise the service does not see the changes.
2. **Payara**: in the `StudyBuddyDocker` folder (Moodle, week 7), run `docker compose up -d`, then check in
   Docker Desktop that the 2 containers (`payara-full`, `mysql8-db`) are running.
   Payara admin console: https://localhost:4848 (user `admin`, password `admin`).
3. **Service**: open `CineRateService` in IntelliJ and reload the Maven project. Create the run configuration
   `Run > Edit Configurations > + > Payara Server > Remote`:
   - *Server*: password `admin`;
   - *Deployment*: add the artifact `CineRateService:war`;
   - *Startup/Connection*: `Debug`, transport `Socket`, port `9009`.
4. Start it with the green arrow (or the green bug to debug). The console must show
   `Artifact CineRateService:war: Artifact is deployed successfully`.
5. **Test**: import `CineRateService/CineRate.postman_collection.json` in Postman and run the requests in order.

| What | Address |
|---|---|
| Service page | http://localhost:8080/CineRateService-1.0-SNAPSHOT/ |
| API base address | http://localhost:8080/CineRateService-1.0-SNAPSHOT/api |

Only one IntelliJ project at a time can use the debugger (port 9009).

### Users

| Request | Role |
|---|---|
| `GET /users` | List all the users |
| `GET /users/{id}` | Details of one user |
| `POST /users` | Create a user; the ID, the role (`member`) and the registration date are always set by the service |
| `PUT /users/{id}` | Update a user, without changing its ID or its role; the password is only changed if one is sent |
| `DELETE /users/{id}` | Delete a user |

Account rules: the username, the email and the password are required, the username must be unique, the email
must contain an `@` and the password must have at least 6 characters. Passwords are hashed with
`Utils.hashPassword` and are never returned in the responses.

Demo users created at startup: `alice` / `alice123`, `bob` / `bob12345`, `admin` / `admin123` (administrator).

### Errors

Every error is returned as JSON `{"message": ..., "type": ...}` with the status:
`400` if the data is invalid, `404` if the element does not exist, `500` for anything else.

## Group

Samy Hajhouj, Elias Mounerat, Mouhamed Sarira, Klinsmann Kodra
