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
 * An enumeration of the possible surface qualities employed by RadSim.
 *
 * @property value The string representation of the surface quality.
 * @property backMappingTag The OSM tag used to back-map the RadSim surface quality to OSM.
 */
enum class SurfaceQuality(val value: String, val backMappingTag: OsmTag?) {
    /**
     * Represents a piece of a track with good surface quality for cycling.
     */
    GOOD("Good", OsmTag("smoothness", "good")),

    /**
     * Represents a piece of a track with medium surface quality for cycling.
     */
    MEDIUM("Medium", OsmTag("smoothness", "intermediate")),

    /**
     * Represents a piece of a track with bad surface quality for cycling.
     */
    BAD("Bad", OsmTag("smoothness", "very_bad")),

    /**
     * Represents a piece of a track with no information about the surface quality for cycling.
     */
    NO_INFORMATION("nA", null);

    companion object {
        /**
         * The RadSim tag used to store the surface quality.
         */
        const val RADSIM_TAG = "surfaceQuality"

        /**
         * The specific OSM tags that are used to determine the RadSim surface quality.
         *
         * "smoothness" is the default key used by BRouter
         */
        @Suppress("MemberVisibilityCanBePrivate") // Part of the API
        val specificOsmTags = listOf(
            "smoothness",
        )

        /**
         * Mappings used to map from OSM attributes to surface qualities.
         */
        private val MAPPINGS: List<Mapping<SurfaceQuality>>

        init {
            val mappings: MutableList<Mapping<SurfaceQuality>> = ArrayList()

            val categoryValueMap = mapOf(
                GOOD to listOf(
                    Regex("excellent"),
                    Regex("good"),
                ),
                MEDIUM to listOf(
                    Regex("intermediate"),
                ),
                BAD to listOf(
                    Regex("bad"),
                    Regex("very_bad"),
                    Regex("horrible"),
                    Regex("very_horrible"),
                    Regex("impassable"),
                ),
            )

            // Add mappings in order: check first tag for all values, then check next tag for all values, etc.
            specificOsmTags.forEach { osmTag ->
                categoryValueMap.forEach { (category, values) ->
                    values.forEach { mappings.add(Mapping(osmTag, it, category)) }
                }
            }

            MAPPINGS = mappings.toList()
        }

        /**
         * Find the surface quality based on the provided tags.
         *
         * This method checks the specific OSM tags in order of priority and returns the first match.
         *
         * Defaults to [NO_INFORMATION] if no matching surface quality is found.
         *
         * @param tags The tags to search for the surface quality.
         * @return The surface quality based on the provided tags.
         */
        fun toRadSim(tags: Map<String, Any>): SurfaceQuality {
            // Iterate through mapping list first to ensure hierarchical checks
            for (mapping in MAPPINGS) {
                val osmKey = mapping.tagKey
                val tagValue = tags[osmKey]?.toString() ?: continue
                if (mapping.tagValue.matches(tagValue)) {
                    return mapping.target
                }
            }

            return NO_INFORMATION
        }
    }
}
