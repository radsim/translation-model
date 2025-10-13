package de.radsim.translation.model

import de.cyface.model.osm.OsmTag
import org.slf4j.LoggerFactory

@Suppress("unused") // Part of the API
object RadSimDeltaEngine {

    private val LOGGER = LoggerFactory.getLogger(RadSimDeltaEngine::class.java)

    fun computeDelta(
        currentTags: Map<String, Any>,
        key: String,
        value: String
    ): Set<OsmTag> = when (key) {
        // Infrastructure (roadStyle) — now handled here (was InfrastructureBackMapper)
        SimplifiedBikeInfrastructure.RADSIM_TAG -> {
            val from = BikeInfrastructure.toRadSim(currentTags).simplified
            val to = SimplifiedBikeInfrastructure.fromValue(value)
            infraDelta(from, to, currentTags)
        }

        // Stateless mappings stay simple deltas
        SurfaceType.RADSIM_TAG -> {
            val targetSurface = SurfaceType.fromValue(value)
            setOf(targetSurface.backMappingTag)
        }

        Speed.RADSIM_TAG -> {
            val targetSpeed = Speed.fromValue(value)
            targetSpeed.backMappingTag?.let { setOf(it) } ?: emptySet()
        }

        else -> error("Unknown RadSim key: $key")
    }

    /**
     * Back-mapping engine for infrastructure transitions using the matrix rules.
     * Produces a *delta* (add/remove tags) to move from → to.
     */
    private fun infraDelta(
        from: SimplifiedBikeInfrastructure,
        to: SimplifiedBikeInfrastructure,
        current: Map<String, Any>
    ): Set<OsmTag> {
        if (from == to) return emptySet()

        val visited = mutableSetOf<Pair<SimplifiedBikeInfrastructure, Map<String, Any>>>()
        return recurse(from, to, current, visited)
    }

    private fun recurse(
        from: SimplifiedBikeInfrastructure,
        to: SimplifiedBikeInfrastructure,
        current: Map<String, Any>,
        visited: MutableSet<Pair<SimplifiedBikeInfrastructure, Map<String, Any>>>
    ): Set<OsmTag> {
        if (from == SimplifiedBikeInfrastructure.NO) {
            require(current["highway"] != "cycleway") {
                "Unexpected 'highway=cycleway' in back-mapping from NO category" // [BIK-1440]
            }
        }

        val state = from to current
        if (!visited.add(state)) {
            error("Back-mapping recursion detected: $from → $to (state revisited)")
        }

        // 1) Compute the delta from matrix
        val delta = BackMappingRules.applyRule(from, to, current)

        // 2) Apply delta to produce the *next* tag state
        val updated = current.toMutableMap()
        delta.forEach { tag ->
            val v = tag.value
            if (v is String && v.isEmpty()) {
                updated.remove(tag.key)
            } else {
                updated[tag.key] = v
            }
        }

        // 3) Classify next
        val next = BikeInfrastructure.toRadSim(updated).simplified

        // 4) Guard: if rule didn’t move category, it’s a stall
        if (next == from) {
            LOGGER.error(
                "Stall: from=$from to=$to\nCurrent=$current\nDelta=$delta\nUpdated=$updated\nNext=$next"
            )
            error("Back-mapping stalled: $from → $to produced no category change")
        }

        // 5) Done or recurse
        // Instead of filtering out empty-value tags before returning, return the raw delta including removals.
        // This allows us to signal to the outside world, that tags have to be removed from the way.
        // We cannot do this here, as we only see the changed tags, not all original OSM tags.
        return if (next == to) {
            delta
        } else {
            delta + recurse(next, to, updated, visited)
        }
    }
}
