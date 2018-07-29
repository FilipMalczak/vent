# Vent over MongoDB


This module contains implementation of Vent that uses MongoDB as a backend. The whole implementation is reactive, as it
is based on reactive MongoDB driver.

It implements each Vent collection as one or more MongoDB collections (one per period; this is prepared for archivization)
and each object as one or more Page objects. Page contains initial state, some metadata (ID, versions, etc) and event
list that can be applied to obtain state for given point in time.

This implementation needs some Mongo driver tweaking - make sure that codecs provided in 
[`RequiredCodecsForMongoVent`](/vent-mongo/src/main/java/com/github/filipmalczak/vent/mongo/RequiredCodecsForMongoVent.java)
are used by `ReactiveMongoOperations` that you supply to get a 
[`VentDb`](/vent-mongo/src/main/java/com/github/filipmalczak/vent/mongo/VentDb.java) 
instance. To get such an instance, use 
[`ReactiveMongoVentFactory`](/vent-mongo/src/main/java/com/github/filipmalczak/vent/mongo/ReactiveMongoVentFactory.java). 
Usually, you'll only need to provide (already tweaked) `ReactiveMongoOperations` to the factory, as other components are 
internal. In some cases (e.g. when you want to expose `VentDb` over some protocol) you may want to customize temporal 
service too.
