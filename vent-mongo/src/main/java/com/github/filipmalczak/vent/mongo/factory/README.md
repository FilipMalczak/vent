# Pluggable factory

Probably a candidate for dedicated utility module.

Provides an abstraction (Factory interface) of a factory, being an entity capable of producing a result based on its
config (e.g. result = Vent database, config = set of internal services). Another provided abstraction is PluggableFactory
abstract class with FactoryPlugin interface, introducing intermediate step - preparing an extension API that is
consumed by each plugin, together with raw result, giving a new (yet possibly the same) result.

Such plugins may start some jobs (like statistical, cleanups, etc) or provide poor mans aspects over the result. 