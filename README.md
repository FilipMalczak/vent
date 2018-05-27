# vent

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Insight.io](https://www.insight.io/repoBadge/github.com/FilipMalczak/vent)](https://insight.io/github.com/FilipMalczak/vent)
[![Dependabot enabled](https://img.shields.io/badge/dependabot-enabled-yellow.svg)](https://dependabot.com)
[![BCH compliance](https://bettercodehub.com/edge/badge/FilipMalczak/vent?branch=dev)](https://bettercodehub.com/)

[![Bintray release](https://img.shields.io/badge/bintray-release-blue.svg) ](https://bintray.com/filipmalczak/maven/vent/_latestVersion)
[![Artifactory release](https://img.shields.io/badge/artifactory-release-blue.svg)](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-release-local/com/github/filipmalczak) 
[![Artifactory snapshot](https://img.shields.io/badge/artifactory-snapshot-brightgreen.svg)](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/com/github/filipmalczak)  

> todo comment on modules  

> Currently Bintray is misconfigured and artifacts are not deployed there.
> Until I fix it, use JFrog OSS Artifactory.
> https://docs.travis-ci.com/user/deployment/bintray/ will probably be the way to do this
 
Vent - evented DB

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
of Vent in other languages.

## Current module list

- `velvet` - simplified XPath-like expressions for Java map/list hierarchies, supporting read and write access
- `vent-adapters` - `ServiceLoader`- enabled adapters between types (e.g. can turn reactive implementation into 
blocking); this module contains only the infrastructure and API
- `vent-adapters-common` - this contains implementations for common adapters; currently a few dedicated wrappers and 
a generic one for reactive->blocking
- `vent-api` - Both generic and concrete interfaces for Vent database, collection, etc; also contains value
object definitions
- `vent-embedded` - Embedded Vent implementation, working aroung provided ReactiveMongoTemplate; exposed as
Spring 5 configuration to be imported
- `vent-testing` - Testing utilities
- `vent-traits` - Reflection API for handling common type traits (like "reactive" or "asynchronous")
- `vent-utils` - General utilities
- `vent-web-server` - Webflux-enabled web server configuration that exposes VentDb over HTTP

### Planned modules:

- `vent-web-client` - Client for aforementioned server, implementing VentDb API

 

## MVP

> MVP is under development. I'm gonna try to keep done things ticked, but don't trust it 100%, better read the code.

- [x] creating an object
- [x] changing object state by single events (put value, delete value, update the whole object)
- [x] getting object state at any moment in time
- [x] compacting (creating new page)
- [x] querying
- [x] deleting an object
- [ ] full HTTP API (partially done, though untested)

## Further features

- fetching some time period (instead of object state at timestamp)
- factory for Spring Data-like repositories based on Vent
- better configurability (better pointing to underlying Mongo instance, working 
properties for compacting)
- API extensions (mainly in default interface methods)
- more event types

## Other ideas:

> In random order

- event metadata and ID (e.g. event may represent transaction leg between two banking accounts; we want to join it with 
some transaction object)
- "ensure path" ([a: [[x: 1]]] -> "a[1].x = 3" should add to list new object with single member x=3)
- GET schema
- views (GET with sublist of members/member tree)
 
