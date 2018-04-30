# vent
Vent - evented DB

This is very early stage of development. No need for a proper README.

Long story short - this will be an event sourcing wrapper over MongoDB that will allow for querying DB with timestamp,
in which case result will correspond to object (document) at that moment in time.

## TODO:

- rethink naming

### MVP:

- organising in collections (this will either need a SPeL hack or switch to MongoTemplate)
- querying
- COMPACT (store a memory, replace object with snapshot and empty event list)
- GET at datetime

### Required for full product:

- querying at datetime 

### Other ideas:

> In random order

- vent metadata and ID (e.g. vent may represent transaction leg between two banking accounts; we want to join it with 
some transaction object)
- "ensure path" ([a: [[x: 1]]] -> "a[1].x = 3" should add to list new object with single member x=3)
- GET schema
- views (GET with sublist of members/member tree)
 
