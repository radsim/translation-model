/*
 * Copyright (C) 2019-2024 Cyface GmbH
 *
 * This file is part of the RadSim Translation Model.
 *
 *  The RadSim Translation Model is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The RadSim Translation Model is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with the RadSim Translation Model.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.radsim.translation.model

import de.cyface.model.osm.OsmTag
import org.slf4j.LoggerFactory
import java.util.function.BiConsumer
import java.util.function.BinaryOperator
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collector

/**
 * Mapped for conversion between RadSim tags and appropriate Open Street Map representations.
 *
 * @property tags The source tags, ether RadSim or OSM tags.
 */
@Suppress("unused") // Part of the API
class RadSimTagMapper(private val tags: List<OsmTag>) {

    /**
     * Convert RadSim Tags back to an appropriate Open Street Map representation, which is required by the Simulation.
     *
     * @return The converted list of Open Street Map tags
     */
    fun toOsm(): List<OsmTag> {
        return tags.stream().collect(object : Collector<OsmTag, MutableList<OsmTag>, List<OsmTag>> {
            override fun supplier(): Supplier<MutableList<OsmTag>> {
                return Supplier<MutableList<OsmTag>> { ArrayList() }
            }

            @SuppressWarnings("LongMethod") // TODO
            override fun accumulator(): BiConsumer<MutableList<OsmTag>, OsmTag> {
                return BiConsumer<MutableList<OsmTag>, OsmTag> { osmTags: MutableList<OsmTag>, radsSimTag: OsmTag ->
                    val key = radsSimTag.key
                    val value = radsSimTag.value
                    when (key) {
                        // For an overview see https://wiki.openstreetmap.org/wiki/Bicycle
                        SimplifiedBikeInfrastructure.RADSIM_TAG -> {
                            error("Should not be called as we now call recursiveBackMap from outside")
                            // Attention:
                            // [backend.RadSimTagMerger.merge] already implements the first step of the back-mapping:
                            // Remove any * :left / *:right / *:both / *:cycleway key tags.
                            // For more details about the back-mapping see [SimplifiedBikeInfrastructure].

                            // Then apply the from-to category rules until the target category is reached.
                            // We only write simple (side-agnostic) tags (see new rules 2025-08-21).
                            val targetInfra = SimplifiedBikeInfrastructure.fromValue(value as String)

                            val originalTagMap = osmTags.associate { it.key to it.value } // or original tags if known
                            val fromInfra = BikeInfrastructure.toRadSim(originalTagMap).simplified

                            osmTags.addAll(recursiveBackMap(fromInfra, targetInfra, originalTagMap, false))
                        }

                        SurfaceType.RADSIM_TAG -> {
                            val targetSurface = SurfaceType.fromValue(value as String)
                            osmTags.add(targetSurface.backMappingTag)
                        }

                        Speed.RADSIM_TAG -> {
                            val targetSpeed = Speed.fromValue(value as String)
                            targetSpeed.backMappingTag?.let { osmTags.add(it) }
                        }

                        else -> throw IllegalArgumentException("Unknown RadSim tag key: $key")
                    }
                }
            }

            override fun combiner(): BinaryOperator<MutableList<OsmTag>> {
                return BinaryOperator<MutableList<OsmTag>> { osmTags: MutableList<OsmTag>, osmTags2: List<OsmTag> ->
                    osmTags.addAll(osmTags2)
                    osmTags
                }
            }

            override fun finisher(): Function<MutableList<OsmTag>, List<OsmTag>?> {
                return Function<MutableList<OsmTag>, List<OsmTag>?> { osmTags: List<OsmTag>? -> osmTags }
            }

            override fun characteristics(): Set<Collector.Characteristics> {
                return emptySet()
            }
        })
    }

    fun recursiveBackMap(
        from: SimplifiedBikeInfrastructure,
        to: SimplifiedBikeInfrastructure,
        currentTags: Map<String, Any>,
        fromOutside: Boolean, // Just for debugging right now
        visited: MutableSet<Pair<SimplifiedBikeInfrastructure, Map<String, Any>>> = mutableSetOf()
    ): Set<OsmTag> {
        if (from == to) return emptySet()
        if (from == SimplifiedBikeInfrastructure.NO) {
            require(currentTags["highway"] != "cycleway") {
                "Unexpected 'highway=cycleway' in back-mapping from NO category" // [BIK-1440]
            }
        }

        val state = from to currentTags
        if (!visited.add(state)) {
            error("Back-mapping recursion detected: $from → $to (state revisited)")
        }

        val delta = BackMappingRules.applyRule(from, to, currentTags)
        val updatedTags = currentTags.toMutableMap()
        delta.forEach { tag ->
            if (tag.value is String && (tag.value as String).isEmpty()) { // Allow tag removal with empty value delta
                updatedTags.remove(tag.key)
            } else {
                updatedTags[tag.key] = tag.value
            }
        }
        val next = BikeInfrastructure.toRadSim(updatedTags).simplified

        if (next == from) {
            LOGGER.error(
                "Stall: from=$from to=$to\nCurrent=$currentTags\nDelta=$delta\nUpdated=$updatedTags\nNext=$next"
            )
            error("Back-mapping stalled: $from → $to produced no category change")
        }

        // Instead of filtering out empty-value tags before returning, return the raw delta including removals.
        // This allows us to signal to the outside world, that tags have to be removed from the way.
        // We cannot do this here, as we only see the changed tags, not all original OSM tags.
        return if (next == to) {
            delta
        } else {
            if (fromOutside) {
                // Just added so we know if this is actually ever called
                LOGGER.warn("Recursion during outside-call, visited: ${visited.size}")
            }
            (delta + recursiveBackMap(next, to, updatedTags, fromOutside, visited)).toSet()
        }
    }

    /**
     * Maps the `OsmTag`s to `RadSimTag`s.
     *
     * @return the `RadSimTag`s
     */
    @SuppressWarnings("CyclomaticComplexMethod") // TODO
    fun toRadSim(): HashMap<String, String> {
        val original = HashMap<String, Any>()
        tags.forEach(Consumer { osmTag: OsmTag -> original[osmTag.key] = osmTag.value })
        val originalTags = HashMap<String, String>()

        // `findSurfaceType` ensures the osm tags are checked hierarchically [BIK-1086]
        when (SurfaceType.toRadSim(original)) {
            SurfaceType.COMFORT_1_ASPHALT ->
                originalTags[SurfaceType.RADSIM_TAG] = SurfaceType.COMFORT_1_ASPHALT.value
            SurfaceType.COMFORT_2_COMPACTED ->
                originalTags[SurfaceType.RADSIM_TAG] = SurfaceType.COMFORT_2_COMPACTED.value
            SurfaceType.COMFORT_3_COBBLESTONE ->
                originalTags[SurfaceType.RADSIM_TAG] = SurfaceType.COMFORT_3_COBBLESTONE.value
            SurfaceType.COMFORT_4_GRAVEL ->
                originalTags[SurfaceType.RADSIM_TAG] = SurfaceType.COMFORT_4_GRAVEL.value
        }

        val bikeInfrastructureMap = BikeInfrastructure.entries.associateWith { it.value }
        val bikeInfrastructure = BikeInfrastructure.toRadSim(original)
        val radSimTagValue = bikeInfrastructureMap[bikeInfrastructure] ?: error("Invalid value: $bikeInfrastructure")
        originalTags[BikeInfrastructure.RADSIM_TAG] = radSimTagValue

        when (Speed.toRadSim(original)) {
            Speed.MAX_SPEED_MIV_LTE_30 ->
                originalTags[Speed.RADSIM_TAG] = Speed.MAX_SPEED_MIV_LTE_30.value
            Speed.MAX_SPEED_MIV_GT_30_LTE_50 ->
                originalTags[Speed.RADSIM_TAG] = Speed.MAX_SPEED_MIV_GT_30_LTE_50.value
            Speed.MAX_SPEED_MIV_GT_50 ->
                originalTags[Speed.RADSIM_TAG] = Speed.MAX_SPEED_MIV_GT_50.value
            else ->
                originalTags[Speed.RADSIM_TAG] = Speed.NO_INFORMATION.value
        }
        return originalTags
    }

    companion object {
        /**
         * The logger used by objects of this class. Configure it using <tt>src/main/resources/logback.xml</tt>.
         */
        private val LOGGER = LoggerFactory.getLogger(RadSimTagMapper::class.java)
    }
}
