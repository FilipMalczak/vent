# Vent

[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vent-event-sourced-db/Lobby)
[![Project Stats](https://www.openhub.net/p/vent-db/widgets/project_thin_badge.gif)](https://www.openhub.net/p/vent-db)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Dependabot enabled](https://img.shields.io/badge/dependabot-enabled-yellow.svg)](https://dependabot.com)
[![BCH compliance](https://bettercodehub.com/edge/badge/FilipMalczak/vent?branch=dev)](https://bettercodehub.com/)

[![Bintray release](https://img.shields.io/badge/bintray-release-blue.svg) ](https://bintray.com/filipmalczak/maven/vent/_latestVersion)  

> Currently Bintray is misconfigured and artifacts are not deployed there.
> Until I fix it, use JFrog OSS Artifactory.
> https://docs.travis-ci.com/user/deployment/bintray/ will probably be the way to do this
 
_Vent - temporally-enabled DB_

Vent is a NoSQL database with similiar abstraction as MongoDB (meaning, it's a document/object database) that not only 
allows for handling current state of objects, but also keeps full history of them. You can execute atomic write actions
like "create an object", "put value `x` under path `a.b.c`", "update object to state `{a: 1, b: {c: 2}}`" or "delete 
value under path `x.y.z`", but when it comes to reading you can ask "give me current state of the object" as well as 
"give me state of the object at 1st of May 2018". That also works with querying ("find all objects that were matching 
some criteria on some date")!

In the future you'll also be able to fetch changes from some period, so you don't have to ask for every quantum of time
from that period.

Status:
* master [![Build Status](https://travis-ci.org/FilipMalczak/vent.svg?branch=master)](https://travis-ci.org/FilipMalczak/vent) [![CodeFactor](https://www.codefactor.io/repository/github/filipmalczak/vent/badge/master)](https://www.codefactor.io/repository/github/filipmalczak/vent/overview/master) [![codecov](https://codecov.io/gh/FilipMalczak/vent/branch/master/graph/badge.svg)](https://codecov.io/gh/FilipMalczak/vent/branch/master)
* dev [![Build Status](https://travis-ci.org/FilipMalczak/vent.svg?branch=dev)](https://travis-ci.org/FilipMalczak/vent) [![CodeFactor](https://www.codefactor.io/repository/github/filipmalczak/vent/badge/dev)](https://www.codefactor.io/repository/github/filipmalczak/vent/overview/dev) [![codecov](https://codecov.io/gh/FilipMalczak/vent/branch/dev/graph/badge.svg)](https://codecov.io/gh/FilipMalczak/vent/branch/dev) 

First-class implementations are reactive (backed with [Project Reactor](https://projectreactor.io/)), but thanks to 
[`traits`](/vent-traits) and [`adapters`](/vent-adapters) modules, it's pretty easy to turn reactive implementation to 
a blocking one. If you ever wonder why general API looks like a wet dream/nightmare of creator of Java generics, that is 
exactly the reason (so that interface shape is the same, but returned and consumed types are different).

At this point there is a MongoDB-backed implementation ready, as well as a server/client pair (where client implements 
Vent API) that can expose any implementation over HTTP, so you can use Vent from any machine or share a single DB 
instance between many client program instances.

In the future there will also be a GIT-based implementation.

I'm doing my best to comply with Semver, but until I reach at least v0.5.0 (or better yet, 1.0.0), I allow
myself to evolve the API. I don't intend to make any drastic changes though - mostly they will be package
structure changes, or (nearly) backwards-compatible stuff (like breaking full read-write API to read and write operations,
while keeping previous interface ).


## Get Vent

> Vent currently supports only Java 8. It has not been tested on J9+. Following "modules" are gradle modules,
> not Java modules.

Each module is published as an artifact with group `com.github.filipmalczak` (this is a subject to change if Vent will
become interesting; I'll prepare Github organization in such case). `master` and `dev` branches are published per-commit,
where `master` is always a release version, while `dev` a snapshot version.

Current versions (common for all modules) are:

- [![master](https://img.shields.io/badge/dynamic/json.svg?label=master&url=https%3A%2F%2Fraw.githubusercontent.com%2FFilipMalczak%2Fvent%2Fdev%2Fversions.json&query=%24.stable&colorB=blue)](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-release-local/com/github/filipmalczak) ![stable-codename](https://img.shields.io/badge/dynamic/json.svg?label=codename&url=https%3A%2F%2Fraw.githubusercontent.com%2FFilipMalczak%2Fvent%2Fdev%2Fversions.json&query=%24.codenames.stable&colorB=lightgrey&logo=github)
- [![dev](https://img.shields.io/badge/dynamic/json.svg?label=dev&url=https%3A%2F%2Fraw.githubusercontent.com%2FFilipMalczak%2Fvent%2Fdev%2Fversions.json&query=%24.snapshot&colorB=brightgreen&suffix=-SNAPSHOT)](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/com/github/filipmalczak) ![snapshot-codename](https://img.shields.io/badge/dynamic/json.svg?label=codename&url=https%3A%2F%2Fraw.githubusercontent.com%2FFilipMalczak%2Fvent%2Fdev%2Fversions.json&query=%24.codenames.snapshot&colorB=lightgrey&logo=github)

> You can find full list of releases [here](https://github.com/FilipMalczak/vent/releases).

> Codenames are given after writers that I either like or would like to give some kind of tribute to (patch versions 
> won't change the codename).
> When choosing them, I'm looking at [this](https://en.wikipedia.org/wiki/List_of_science-fiction_authors) list.
> I don't want to cause any legal problems because of that, so if you think that usage of some name conflicts with your
> copyright, just contact me and I'll change it.  

Following modules are available:

- `velvet` - Simplified XPath-like expressions for Java map/list hierarchies, supporting read and write access
- `vent-adapters` - `ServiceLoader`- enabled adapters between types (e.g. can turn reactive implementation into 
blocking); this module contains only the infrastructure and API
- `vent-adapters-common` - This contains implementations for common adapters; currently a few dedicated wrappers and 
a generic one for reactive->blocking
- `vent-api` - Both generic and concrete **interfaces for Vent** database, collection, etc; also contains value
object definitions
- `vent-temporal` - **temporal service** API, simple (local) implementation and abstract base class for remote implementations;
temporal service is a service that is able to answer questions "what time is it now?" and "what timezone are we in?",
as well as provide translations between `java.util.time`, `java.util.Date` and timestamps
- [`vent-mongo`](/vent-mongo/README.md) - **Vent-over-MongoDB implementation**, working aroung provided `ReactiveMongoTemplate`
- `vent-testing` - Testing utilities
- `vent-traits` - Reflection API for handling common type traits (like "reactive" or "asynchronous")
- `vent-utils` - General utilities
- `vent-web-common` - HTTP API request and response objects, as well as endpoint paths String constants 
- `vent-web-server` - Webflux-enabled **web server** configuration **that exposes VentDb over HTTP**
- `vent-web-client` - **Client for aforementioned server**, implementing VentDb API
- `ventrello` - Simple Trello-like app using Vent as a persistence layer (with jQuery and bootstrap on frontend, with 
fat client); PoC that Vent can be useful in real-life solutions

Artifactory Maven repositories are:

- [![Artifactory release](https://img.shields.io/badge/artifactory-release-blue.svg)](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-release-local/com/github/filipmalczak)
- [![Artifactory snapshot](https://img.shields.io/badge/artifactory-snapshot-brightgreen.svg)](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/com/github/filipmalczak)

> Follow one of links above, then click "Set me up" in top right-hand corner to see instructions for maven and gradle.

## Looking for contributors

Totally. Check out Vent gitter and I'll write down stuff to do as issues.

[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vent-event-sourced-db/Lobby) 

## "Customer Support"

You're most welcome to contact me on gitter, I'll be happy to help you start using Vent in your app or debug something
Vent-related. 

[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vent-event-sourced-db/Lobby)

## Oncoming features

### Version under construction

- [x] rename "embedded" to "mongo"
- [x] extract temporal service to dedicated module
- [x] split read/write APIs (but provide utilities to merge them)
- [ ] provide PoC application using Vent for persistence (Ventrello - Vent-enabled simplified Trello clone) (WIP)
- [ ] write a proper tutorial, its high time for that
- [ ] strategy-based approach to optimization, scheduled with a plugin
- [ ] implement archivization in Mongo impl (optimization - storing older pages for each object; archivization - storing 
older collection content)
- [ ] better configurability (better pointing to underlying Mongo instance, working page optimization)
- [ ] Vent plugins like optimization scheduler, but I guess transactions can be enabled this way too
- [ ] proper abstraction for transactionality (optimistic, version-based locking can be done by "freezing time" and is 
basically ready to implement against an interface, but real locking may be tricky)
- [ ] NTP-based temporal service
- [ ] some better logging for mongo impl
    - [ ] customizable for impl internals
    - [ ] wrapper for DB for public API
- [ ] asynchronous trait and accompanying adapters
- [ ] "trait chains"; e.g. if we have adapters for blocking -> async and async -> reactive, then we should be able to turn 
blocking -> reactive
- [ ] fix Bintray
- [ ] set up logging properly (use Slf4J everywhere, choose a modern backend for server and tests)

### Next version candidates

- provide additional impl over GIT
- RPC/RMI
- factory for Spring Data-like repositories based on Vent

### Backlog

> In random order, basically ideas what to do next. May be taken into next version on the spot, depends on what I fancy.
> Unless someone wants to help this is easier than github.

- query-based updates (PUT VALUE address.city = "Another one" WHERE or(facts.hasMoved, facts.wasRelocated))
- fetching some time period (instead of object state at timestamp)
- factory for Spring Data-like repositories based on Vent
- API extensions (mainly in default interface methods)
- more event types
- message-based (lets start with WebSockets) remote client
- event metadata and ID (e.g. event may represent transaction leg between two banking accounts; we want to join it with 
some transaction object)
- "ensure path" ([a: [[x: 1]]] -> "a[1].x = 3" should add to list new object with single member x=3)
- GET schema
- views (GET with sublist of members/member tree)
