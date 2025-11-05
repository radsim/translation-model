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

/**
 * An enumeration providing the different speed features considered by RadSim.
 *
 * The categories are defined based on the hierarchical mapping from OSM maxspeed tags.
 *
 * @property value The value for the RadSim speed tag
 * @property backMappingTag The OSM tag used to back-map the RadSim speed to OSM
 */
@Suppress("SpellCheckingInspection")
enum class Speed(val value: String, val backMappingTag: OsmTag?) {
    /**
     * If the maximum allowed speed is 10 km/h or less (e.g., walking speed, living streets).
     */
    MAX_SPEED_10_OR_LESS("10orless", OsmTag("maxspeed", "10")),

    /**
     * If the maximum allowed speed is between 10 and 30 km/h.
     */
    MAX_SPEED_10_TO_30("10to30", OsmTag("maxspeed", "30")),

    /**
     * If the maximum allowed speed is between 30 and 50 km/h.
     */
    MAX_SPEED_30_TO_50("30to50", OsmTag("maxspeed", "50")),

    /**
     * If the maximum allowed speed is between 50 and 70 km/h.
     */
    MAX_SPEED_50_TO_70("50to70", OsmTag("maxspeed", "70")),

    /**
     * If the maximum allowed speed is 70 km/h or more (e.g., highways, rural roads).
     */
    MAX_SPEED_70_OR_MORE("70ormore", OsmTag("maxspeed", "100")),

    /**
     * If no speed information is available.
     */
    NO_INFORMATION("nA", null);

    companion object {
        /**
         * The RadSim tag used to store the speed information.
         */
        const val RADSIM_TAG = "maxSpeed"

        @Suppress("unused") // Part of the API
        fun fromValue(value: String): Speed =
            entries.firstOrNull { it.value == value }
                ?: error("Unexpected RadSim speed tag: $value")

        /**
         * The specific OSM tags that are used to determine the RadSim speed.
         *
         * This list is ordered by priority - but the list priority is not used anywhere currently.
         *
         * Used by:
         * - `de.radsim.verticle.roadNetwork.NetworkExtractor` to remove all unused key for import
         * - `de.radsim.simulator.model.RadSimToOsmTagMerger` to avoid conflicting tags before mapping changesets to OSC
         */
        @Suppress("SpellCheckingInspection", "unused") // Part of the API
        val specificOsmTags = listOf(
            "maxspeed",
            "maxspeed:forward",
            "maxspeed:backward",
            "zone:maxspeed",
        )

        /**
         * The alternative OSM tag used to estimate the speed when no other speed tags are set.
         */
        private const val ALTERNATIVE_OSM_TAG = "highway"

        /**
         * Highway-based fallback mappings when no speed tags are present.
         */
        private val HIGHWAY_FALLBACK_70_OR_MORE = setOf(
            "motorway",
            "motorway_link",
            "primary",
            "primary_link",
            "secondary",
            "secondary_link"
        )

        private val HIGHWAY_FALLBACK_10_OR_LESS = setOf(
            "living_street",
            "footway",
            "pedestrian",
            "steps"
        )

        private val HIGHWAY_FALLBACK_30_TO_50 = setOf(
            "residential",
            "tertiary",
            "tertiary_link"
        )

        private val HIGHWAY_FALLBACK_10_TO_30 = setOf(
            "path",
            "track"
        )

        /**
         * Parses a speed value string and returns the numeric speed in km/h, or null if unable to parse.
         *
         * Handles special cases like:
         * - "DE:rural" / "de:rural" -> 100 km/h
         * - "DE:urban" -> 50 km/h
         * - "DE:motorway" -> 200 km/h
         * - "DE:20" -> 20 km/h
         * - "walk" / "Schrittgeschwindigkeit" -> 5 km/h
         * - "10;30" -> 30 km/h (takes maximum)
         * - "signals" -> 50 km/h
         * - "none" -> null
         *
         * @param value The speed value string from OSM tags
         * @return The numeric speed in km/h, or null if unable to parse
         */
        @Suppress("MagicNumber", "CyclomaticComplexMethod")
        private fun parseSpeedValue(value: String): Int? {
            return when {
                value == "none" -> null
                value == "DE:rural" || value == "de:rural" -> 100
                value == "DE:urban" -> 50
                value == "DE:motorway" -> 200
                value.startsWith("DE:") && value.substring(3).toIntOrNull() != null -> value.substring(3).toInt()
                value == "walk" || value == "Schrittgeschwindigkeit" -> 5
                value == "10;30" -> 30 // Multiple values, take maximum
                value == "signals" -> 50
                else -> value.toIntOrNull()
            }
        }

        /**
         * Classifies a numeric speed value into a RadSim speed category.
         *
         * @param speed The speed in km/h
         * @return The corresponding Speed category
         */
        @Suppress("MagicNumber")
        private fun classifySpeed(speed: Int): Speed {
            return when {
                speed <= 10 -> MAX_SPEED_10_OR_LESS
                speed <= 30 -> MAX_SPEED_10_TO_30
                speed <= 50 -> MAX_SPEED_30_TO_50
                speed <= 70 -> MAX_SPEED_50_TO_70
                else -> MAX_SPEED_70_OR_MORE
            }
        }

        /**
         * Find the RadSim speed based on the provided OSM tags.
         *
         * This method implements a hierarchical mapping based on the Python reference implementation:
         * 1. Check maxspeed tag first (highest priority)
         * 2. If no maxspeed, check maxspeed:forward and maxspeed:backward (takes maximum)
         * 3. If still no speed, check zone:maxspeed (handles German zone values)
         * 4. If no speed tags found, infer from highway tag
         * 5. Default to 10_OR_LESS for anything else
         *
         * @param tags The OSM tags to search for the speed.
         * @return The speed based on the provided OSM tags.
         */
        @Suppress("SpellCheckingInspection", "ReturnCount", "ComplexMethod")
        fun toRadSim(tags: Map<String, Any>): Speed {
            // Validate that we're receiving OSM tags, not RadSim tags [BIK-1478]
            TagFormatValidator.requireOsmFormat(tags, "Speed.toRadSim()")

            var speedToClassify: Int? = null

            // Step 1: Check maxspeed tag first
            val maxspeedValue = tags["maxspeed"]?.toString()
            if (maxspeedValue != null && maxspeedValue != "none") {
                speedToClassify = parseSpeedValue(maxspeedValue)
            }

            // Step 2: If no maxspeed, check maxspeed:forward and maxspeed:backward (take maximum)
            if (speedToClassify == null) {
                val forwardValue = tags["maxspeed:forward"]?.toString()?.let { parseSpeedValue(it) }
                val backwardValue = tags["maxspeed:backward"]?.toString()?.let { parseSpeedValue(it) }

                if (forwardValue != null || backwardValue != null) {
                    speedToClassify = maxOf(forwardValue ?: 0, backwardValue ?: 0)
                }
            }

            // Step 3: If still no speed, check zone:maxspeed
            if (speedToClassify == null) {
                val zoneValue = tags["zone:maxspeed"]?.toString()
                if (zoneValue != null) {
                    @Suppress("MagicNumber")
                    speedToClassify = when (zoneValue) {
                        "30" -> 30
                        "20" -> 20
                        "DE:urban" -> 50
                        "DE:20" -> 20
                        "DE:motorway" -> 200
                        else -> null
                    }
                }
            }

            // Step 4: If we have a speed value, classify it
            if (speedToClassify != null) {
                return classifySpeed(speedToClassify)
            }

            // Step 5: Fallback to highway-based inference
            val highwayValue = tags[ALTERNATIVE_OSM_TAG]?.toString()
            if (highwayValue != null) {
                return when (highwayValue) {
                    in HIGHWAY_FALLBACK_70_OR_MORE -> MAX_SPEED_70_OR_MORE
                    in HIGHWAY_FALLBACK_10_OR_LESS -> MAX_SPEED_10_OR_LESS
                    in HIGHWAY_FALLBACK_30_TO_50 -> MAX_SPEED_30_TO_50
                    in HIGHWAY_FALLBACK_10_TO_30 -> MAX_SPEED_10_TO_30
                    else -> MAX_SPEED_10_OR_LESS // Default fallback
                }
            }

            // Step 6: Ultimate fallback
            return MAX_SPEED_10_OR_LESS
        }
    }
}
