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
 * @property value The value for the RadSim speed tag
 * @property backMappingTag The OSM tag used to back-map the RadSim speed to OSM
 */
@Suppress("SpellCheckingInspection")
enum class Speed(val value: String, val backMappingTag: OsmTag?) {
    /**
     * If the maximum allowed speed on a route section is less or equal to thirty.
     */
    @Suppress("SpellCheckingInspection")
    MAX_SPEED_MIV_LTE_30("<=30", OsmTag("maxspeed", "30")),

    /**
     * If the maximum allowed speed on a route section is greater than thirty but less or equal to fifty.
     */
    @Suppress("SpellCheckingInspection")
    MAX_SPEED_MIV_GT_30_LTE_50("31-50", OsmTag("maxspeed", "50")),

    /**
     * If the maximum allowed speed on a route section is greater than fifty.
     */
    @Suppress("SpellCheckingInspection")
    MAX_SPEED_MIV_GT_50(">50", OsmTag("maxspeed", "100")),

    /**
     * If no speed information is available.
     */
    NO_INFORMATION("nA", null);

    companion object {
        /**
         * The RadSim tag used to store the speed information.
         */
        const val RADSIM_TAG = "maxSpeed"

        /**
         * The specific OSM tags that are used to determine the RadSim speed.
         *
         * "maxspeed" is the default key used by BRouter
         * "zone:maxspeed": not used by a BRouter profile yet, but is mentioned in the BRouter lookups file
         * "maxspeed:forward/backward" is used by the profile "recumbent_bike_fast"
         */
        @Suppress("SpellCheckingInspection")
        val specificOsmTags = listOf(
            "maxspeed",
            // We include these in `AreaRoadNetworkProcessingVerticle` to ensure BRouter uses the original OSM tags.
            // We currently don't interpret these for the RadSim tags.
            // But we delete them when a RadSim tag overwrites the speed to ensure BRouter uses the correct speed.
            // This should not have any effects yet, as we do not re-route the changed ways.
            // *commented out* as we did not check them before and this would also interprete them in `toRadSim()`
            // "zone:maxspeed",
            // "maxspeed:forward",
            // "maxspeed:backward",
        )

        /**
         * The alternative OSM tag used to estimate the speed when no other speed tags are set.
         */
        private const val ALTERNATIVE_OSM_TAG = "highway"

        /**
         * Mappings used to map from OSM attributes to speed values.
         */
        private var MAPPINGS_PER_TAG: List<SpeedMapping>

        init {
            val mappings: MutableList<SpeedMapping> = ArrayList()

            // Define mappings for various speed-related OSM tags
            val categoryValueMap = mapOf(
                // living street (highway = living_street is checked in `toRadSim()`
                MAX_SPEED_MIV_LTE_30 to listOf(
                    Regex("10 mph"),
                    Regex("walk"),
                ),
                // rural street
                MAX_SPEED_MIV_GT_30_LTE_50 to listOf(
                    Regex("DE:rural"),
                    Regex("DE:urban"), // Cottbus'22 dataset
                ),
                // no information
                NO_INFORMATION to listOf(
                    Regex("unknown"),
                    Regex("none"),
                    Regex("signals"),
                ),
            )

            // Attention:
            // We cannot use Mapping<OsmTagKey, OsmValuesRegex, Speed> as we also need to check the values as int. This
            // is not possible with `Regex`. But we need to check the values as `String` and `Int` for the first tag
            // first, then for the next tag. This, we collect the value mappings tag-independent. See `toRadSim()`.
            categoryValueMap.forEach { (category, values) ->
                values.forEach { mappings.add(SpeedMapping(it, category)) }

                // Is 50 with details
                mappings.add(SpeedMapping(Regex("50\\([^)]*\\)"), MAX_SPEED_MIV_GT_30_LTE_50))
            }

            // Numeric values and 50 with details are interpreted in `findSpeed()`

            MAPPINGS_PER_TAG = mappings.toList()
        }

        /**
         * Find the RadSim speed based on the provided OSM tags.
         *
         * @param tags The OSM tags to search for the speed.
         * @return The speed based on the provided OSM tags.
         */
        @Suppress("SpellCheckingInspection", "ReturnCount") // TODO
        fun toRadSim(tags: Map<String, Any>): Speed {
            // Add mappings in order: check first tag for all values, then check next tag for all values, etc.
            for (osmKey in specificOsmTags) {
                val tagValue = tags[osmKey]?.toString() ?: continue

                // Check osm tag for all string-based values
                for (mapping in MAPPINGS_PER_TAG) {
                    if (mapping.tagValue.matches(tagValue)) {
                        return mapping.target
                    }
                }

                // Check osm tag for all numeric values
                val intValue = tagValue.toIntOrNull()
                if (intValue != null) {
                    @Suppress("MagicNumber")
                    return when {
                        intValue <= 30 -> MAX_SPEED_MIV_LTE_30
                        intValue <= 50 -> MAX_SPEED_MIV_GT_30_LTE_50
                        intValue > 50 -> MAX_SPEED_MIV_GT_50
                        else -> throw IllegalArgumentException("Unknown value for OSM tag `maxspeed`: $intValue")
                    }
                }
                throw IllegalArgumentException("Unknown value for OSM tag `maxspeed`: ${tags[osmKey]}")
            }

            // Check "highway" for alternative speed information
            if (tags.containsKey(ALTERNATIVE_OSM_TAG)) {
                val highwayValue = tags[ALTERNATIVE_OSM_TAG].toString()
                if (highwayValue == "living_street") {
                    return MAX_SPEED_MIV_LTE_30
                }
            }

            return NO_INFORMATION
        }
    }

    data class SpeedMapping(val tagValue: Regex, val target: Speed)
}
