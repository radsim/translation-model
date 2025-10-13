package de.radsim.translation.model

import de.cyface.model.osm.OsmTag


@Suppress("unused") // Part of the API
object RadSimDeltaEngine {
    fun computeDelta(
        currentTags: Map<String, Any>,
        key: String,
        value: String
    ): Set<OsmTag> = when (key) {

        SimplifiedBikeInfrastructure.RADSIM_TAG -> {
            val from = BikeInfrastructure.toRadSim(currentTags).simplified
            val to = SimplifiedBikeInfrastructure.fromValue(value)
            InfrastructureBackMapper(emptyList())
                .recursiveBackMap(from, to, currentTags, false)
        }

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
}
