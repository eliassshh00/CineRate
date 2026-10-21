# CineRate

CineRate is a movie rating and review application built by our group for the Software Architectures course
(HEC Lausanne, UNIL, 2026). Registered users browse a catalogue of movies, rate them from 1 to 10 and review
them (one review per user and movie), keep a personal watchlist, and see the average rating of every movie
(e.g. "8.7 / 10"). Movie data will be imported from The Movie Database (TMDB), and an advanced search finds
movies by title, actor, director, genre or theme keywords.

The application is built incrementally:

1. **Phase 1**: domain model (`CineRateDomain`) and service layer (`CineRateService`, coming next)
2. **Phase 2**: web frontend
3. **Phase 3**: persistence in a relational database

## Modules

| Module | Content |
|---|---|
| `CineRateDomain/` | Plain Java domain model (`ch.unil.doplab.cinerate.domain`): `User`, `Movie`, `Review`, `Person`, `Actor`, `Director`, `Genre`, `Rating`, `Utils`, `ExceptionDescription`, with JUnit 5 tests |
| `CineRateService/` | REST service exposing CRUD operations on the domain objects (to be added) |

## Build and test

Requirements: Java 17 or later and Maven 3.

```
cd CineRateDomain
mvn test       # compile and run the unit tests
mvn install    # install the domain jar in the local Maven repository, for use by CineRateService
```

## Group

Samy Hajhouj, Elias Mounerat, Mouhamed Sarira, Klinsmann Kodra
