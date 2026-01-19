/*
 * Copyright (C) 2026 Cyface GmbH
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

/**
 * A class providing the number of lanes mapping from OSM to RadSim.
 *
 * The mapping follows this hierarchy:
 * 1. Check `lanes` tag first
 * 2. If no `lanes`, sum `lanes:backward` and `lanes:forward`
 * 3. If still no lanes info, infer from `highway` tag
 */
object NumberOfLanes {
    /**
     * The RadSim tag used to store the number of lanes information.
     */
    const val RADSIM_TAG = "noOfLanes"

    /**
     * The specific OSM tags that are used to determine the RadSim number of lanes.
     *
     * This list is ordered by priority - but the list priority is not used anywhere currently.
     *
     * Used by:
     * - `de.radsim.verticle.roadNetwork.NetworkExtractor` to remove all unused key for import
     * - `de.radsim.simulator.model.RadSimToOsmTagMerger` to avoid conflicting tags before mapping changesets to OSC
     */
    @Suppress("unused") // Part of the API
    val specificOsmTags = listOf(
        "lanes",
        "lanes:forward",
        "lanes:backward",
    )

    /**
     * The alternative OSM tag used to estimate the number of lanes when no other lane tags are set.
     */
    private const val ALTERNATIVE_OSM_TAG = "highway"

    /**
     * Highway types that typically have no car lanes (0 lanes).
     */
    private val HIGHWAY_FALLBACK_0_LANES = setOf(
        "footway",
        "steps",
        "cycleway",
        "pedestrian",
        "platform",
        "elevator",
        "bridleway",
        "bus_stop"
    )

    /**
     * Highway types that typically have 1 lane.
     */
    private val HIGHWAY_FALLBACK_1_LANE = setOf(
        "track",
        "path",
        "service",
        "residential",
        "living_street",
        "unclassified",
        "construction",
        "proposed",
        "rest_area",
        "raceway",
        "tertiary_link",
        "secondary_link",
        "primary_link",
        "trunk_link"
    )

    /**
     * Highway types that typically have 2 lanes.
     */
    private val HIGHWAY_FALLBACK_2_LANES = setOf(
        "tertiary",
        "secondary",
        "primary"
    )

    /**
     * Parses a lanes value string and returns the numeric count, or null if unable to parse.
     *
     * @param value The lanes value string from OSM tags
     * @return The numeric lane count, or null if unable to parse
     */
    private fun parseLanesValue(value: String): Int? {
        return value.toIntOrNull()
    }

    /**
     * Find the RadSim number of lanes based on the provided OSM tags.
     *
     * This method implements a hierarchical mapping:
     * 1. Check `lanes` tag first (highest priority)
     * 2. If no `lanes`, sum `lanes:forward` and `lanes:backward`
     * 3. If still no lanes info, infer from `highway` tag
     * 4. Default to 1 lane for anything else
     *
     * Optimized to minimize parseLanesValue() calls [BIK-1483 performance]
     *
     * @param tags The OSM tags to search for the number of lanes.
     * @return The number of lanes based on the provided OSM tags.
     */
    @Suppress("ReturnCount")
    fun toRadSim(tags: Map<String, Any>): Int {
        // Validate that we're receiving OSM tags, not RadSim tags [BIK-1478]
        TagFormatValidator.requireOsmFormat(tags, "NumberOfLanes.toRadSim()")

        // Step 1: Check lanes tag first (most common case - short circuit if found)
        tags["lanes"]?.toString()?.let { lanesValue ->
            parseLanesValue(lanesValue)?.let { return it }
        }

        // Step 2: If no lanes, sum lanes:forward and lanes:backward
        val forwardValue = tags["lanes:forward"]?.toString()?.let { parseLanesValue(it) }
        val backwardValue = tags["lanes:backward"]?.toString()?.let { parseLanesValue(it) }

        if (forwardValue != null || backwardValue != null) {
            return (forwardValue ?: 0) + (backwardValue ?: 0)
        }

        // Step 3: Fallback to highway-based inference
        tags[ALTERNATIVE_OSM_TAG]?.toString()?.let { highwayValue ->
            return when (highwayValue) {
                in HIGHWAY_FALLBACK_0_LANES -> 0
                in HIGHWAY_FALLBACK_1_LANE -> 1
                in HIGHWAY_FALLBACK_2_LANES -> 2
                else -> 1
            }
        }

        // Step 4: Ultimate fallback
        return 1
    }

    /**
     * Creates the back-mapping OSM tag for the given number of lanes.
     *
     * @param numberOfLanes The RadSim number of lanes value.
     * @return The corresponding OSM tag, or null if the value should not be back-mapped.
     */
    @Suppress("unused") // Part of the API
    fun backMappingTag(numberOfLanes: Int): OsmTag? {
        return if (numberOfLanes >= 0) {
            OsmTag("lanes", numberOfLanes.toString())
        } else {
            null
        }
    }
}
