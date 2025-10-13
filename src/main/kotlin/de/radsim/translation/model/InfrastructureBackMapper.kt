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
import java.util.function.Consumer

/**
 * Mapped for conversion between RadSim tags and appropriate Open Street Map representations.
 *
 * @property tags The source tags, ether RadSim or OSM tags.
 */
@Suppress("unused") // Part of the API
class InfrastructureBackMapper(private val tags: List<OsmTag>) {
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

    companion object {
        /**
         * The logger used by objects of this class. Configure it using <tt>src/main/resources/logback.xml</tt>.
         */
        private val LOGGER = LoggerFactory.getLogger(InfrastructureBackMapper::class.java)
    }
}
