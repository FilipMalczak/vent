# vent

[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vent-event-sourced-db/Lobby)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Insight.io](https://www.insight.io/repoBadge/github.com/FilipMalczak/vent)](https://insight.io/github.com/FilipMalczak/vent)
[![Dependabot enabled](https://img.shields.io/badge/dependabot-enabled-yellow.svg)](https://dependabot.com)
[![BCH compliance](https://bettercodehub.com/edge/badge/FilipMalczak/vent?branch=dev)](https://bettercodehub.com/)

[![Bintray release](https://img.shields.io/badge/bintray-release-blue.svg) ](https://bintray.com/filipmalczak/maven/vent/_latestVersion)  

> Currently Bintray is misconfigured and artifacts are not deployed there.
> Until I fix it, use JFrog OSS Artifactory.
> https://docs.travis-ci.com/user/deployment/bintray/ will probably be the way to do this
 
_Vent - evented DB_

Status:
* master [![Build Status](https://travis-ci.org/FilipMalczak/vent.svg?branch=master)](https://travis-ci.org/FilipMalczak/vent) [![CodeFactor](https://www.codefactor.io/repository/github/filipmalczak/vent/badge/master)](https://www.codefactor.io/repository/github/filipmalczak/vent/overview/master) [![codecov](https://codecov.io/gh/FilipMalczak/vent/branch/master/graph/badge.svg)](https://codecov.io/gh/FilipMalczak/vent/branch/master)
* dev [![Build Status](https://travis-ci.org/FilipMalczak/vent.svg?branch=dev)](https://travis-ci.org/FilipMalczak/vent) [![CodeFactor](https://www.codefactor.io/repository/github/filipmalczak/vent/badge/dev)](https://www.codefactor.io/repository/github/filipmalczak/vent/overview/dev) [![codecov](https://codecov.io/gh/FilipMalczak/vent/branch/dev/graph/badge.svg)](https://codecov.io/gh/FilipMalczak/vent/branch/dev) 

This is very early stage of development. No need for a proper README.

I'm doing my best to comply with Semver, but until I reach at least v0.5.0 (or better yet, 1.0.0), I allow
myself to evolve the API. I don't intend to make any drastic changes though - mostly they will be package
structure changes.

Long story short - this will be an event sourcing wrapper over MongoDB that will allow for querying DB with timestamp,
in which case result will correspond to object (document) at that moment in time.

General idea for deployment is that the user should provide MongoDB instance and wrap it with Vent - either
with embedded instance (used in-memory) or exposed via some protocol (e.g. HTTP) - which enables usage
of Vent in other languages. It would be best if it could be horizontally-scalable (if you need more efficiency, spawn
more Vent servers and load-balance around them) and as of now it is (though once I start poking the transactionality,
it may get a bit messy).

## Get Vent

> Vent currently supports only Java 8. It has not been tested on J9+. Following "modules" are gradle modules,
> not Java modules.

Each module is published as an artifact with group `com.github.filipmalczak` (this is a subject to change if Vent will
become interesting; I'll prepare Github organization in such case). `master` and `dev` branches are published per-commit,
where `master` is always a release version, while `dev` a snapshot version.

Current versions (common for all modules) are:

- [![master](https://img.shields.io/badge/master-0.2.0-blue.svg)](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-release-local/com/github/filipmalczak)
- [![dev](https://img.shields.io/badge/dev-0.3.0--SNAPSHOT-brightgreen.svg)](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/com/github/filipmalczak)

Following modules are available:

- `velvet` - Simplified XPath-like expressions for Java map/list hierarchies, supporting read and write access
- `vent-adapters` - `ServiceLoader`- enabled adapters between types (e.g. can turn reactive implementation into 
blocking); this module contains only the infrastructure and API
- `vent-adapters-common` - This contains implementations for common adapters; currently a few dedicated wrappers and 
a generic one for reactive->blocking
- `vent-api` - Both generic and concrete **interfaces for Vent** database, collection, etc; also contains value
object definitions
- [`vent-mongo`](/vent-mongo/README.md) - **Vent-over-MongoDB implementation**, working aroung provided `ReactiveMongoTemplate`
- `vent-testing` - Testing utilities
- `vent-traits` - Reflection API for handling common type traits (like "reactive" or "asynchronous")
- `vent-utils` - General utilities
- `vent-web-common` - HTTP API request and response objects, as well as endpoint paths String constants 
- `vent-web-server` - Webflux-enabled **web server** configuration **that exposes VentDb over HTTP**
- `vent-web-client` - **Client for aforementioned server**, implementing VentDb API

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
- [ ] extract temporal service to dedicated module
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
- split read/write APIs (but provide utilities to merge them)
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
