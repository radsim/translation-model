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
 * An enum with all the available infrastructure types.
 *
 * @property value The value for the RadSim infrastructure type tag.
 * @property backMappingTag The OSM tag used to back-map the RadSim infrastructure type to OSM.
 */
enum class BikeInfrastructure(val value: String, val backMappingTag: OsmTag?) {
    /**
     * Infrastructure type where bikes have their own track without any other traffic nearby.
     */
    TRACK("BicyclePath", OsmTag("cycleway", "track")),

    /**
     * Infrastructure type where bikes have their own lane.
     */
    LANE("MarkedPath", OsmTag("cycleway", "lane")),

    /**
     * Infrastructure type where bikes share their lane with other traffic forms.
     */
    MIXED("Road", OsmTag("bicycle", "yes")),

    /**
     * If no information about the infrastructure type is available.
     */
    NO_INFORMATION("nA", null);

    companion object {
        /**
         * The RadSim tag used to store the infrastructure type.
         */
        const val RADSIM_TAG = "roadStyle"

        /**
         * The specific OSM tags that are used to determine the RadSim infrastructure type.
         *
         * "cycleway" is the default key used by BRouter (probably, check again if unsure)
         * "cycleway[:l/r/b]": used e.g. by the profile "fastbike-verylowtraffic"
         */
        @Suppress("SpellCheckingInspection", "unused") // Part of the API
        val specificOsmTags = listOf(
            "highway",
            "bicycle",
            "cycleway",
            "cycleway:both",
            "cycleway:right",
            "cycleway:left",
        )

        /**
         * Mappings used to map from OSM attributes to infrastructure types.
         *
         * Attention:
         * Don't change the list order as it's ordered semantically, see documentation: secondary data processing
         *
         * If this list is changed, update `RadSimTagMerger`, too.
         *
         * We might want to add more tags like the ones mentioned here:
         * https://wiki.openstreetmap.org/wiki/DE:Tag:highway%3Dcycleway
         * Or e.g. https://www.openstreetmap.org/way/683435825 is treated like "mixed"
         * Or e.g. https://www.openstreetmap.org/way/4704437
         * And sidewalk [ :right/left/both ] : bicycle = [ yes ] -> treat in here?
         * And https://wiki.openstreetmap.org/wiki/DE:Key:bicycle_road implicits vehicle=no, maxspeed=30
         * Also consider mapping MIXED and add NO_INFORMATION as MIXED as default means all ways allow bicycles
         */
        @Suppress("SpellCheckingInspection")
        private var MAPPINGS: List<Mapping<BikeInfrastructure>> = listOf(
            // Track
            Mapping("highway", Regex("cycleway"), TRACK),

            Mapping("cycleway", Regex("track"), TRACK),
            Mapping("cycleway:right", Regex("track"), TRACK),
            Mapping("cycleway:left", Regex("track"), TRACK),

            Mapping("cycleway", Regex("opposite_track"), TRACK),

            Mapping("bicycle", Regex("path"), TRACK),
            Mapping("bicycle", Regex("track"), TRACK),

            // Lane
            Mapping("cycleway", Regex("lane"), LANE),
            Mapping("bicycle", Regex("lane"), LANE),
            Mapping("cycleway:right", Regex("lane"), LANE),
            Mapping("cycleway:left", Regex("lane"), LANE),
            Mapping("cycleway:both", Regex("lane"), LANE),

            Mapping("cycleway", Regex("opposite_lane"), LANE)
        )

        /**
         * Find the infrastructure type based on the provided tags.
         *
         * This method checks the specific OSM tags in order of priority and returns the first match.
         *
         * @param tags The tags to search for the infrastructure type.
         * @param defaultValue The default value if no matching infrastructure type is found.
         * @return The infrastructure type based on the provided tags.
         */
        fun toRadSim(tags: Map<String, Any>, defaultValue: BikeInfrastructure): BikeInfrastructure {
            // Iterate through mapping list first to ensure hierarchical checks
            for (mapping in MAPPINGS) {
                val osmKey = mapping.tagKey
                val tagValue = tags[osmKey]?.toString() ?: continue
                if (mapping.tagValue.matches(tagValue)) {
                    return mapping.target
                }
            }

            return defaultValue
        }
    }
}
