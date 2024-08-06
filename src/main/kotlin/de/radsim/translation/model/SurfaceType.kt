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
 * An enumeration with all the different surface types considered by RadSim.
 *
 * The categories are defined by [BIK-1085] from 2024-07.
 *
 * @property value The value for the RadSim surface type tag.
 * @property backMappingTag The OSM tag used to back-map the RadSim surface type to OSM. To determine good back-mapping
 * candidates TUD checked the penalties in the BRouter profiles, which depend heavily on the profile type.
 */
enum class SurfaceType(val value: String, val backMappingTag: OsmTag) {
    /**
     * If the surface is comfortable for fast bikes.
     */
    COMFORT_1_ASPHALT("comfort_1", OsmTag("surface", "asphalt")),

    /**
     * If the surface is comfortable for city bikes.
     */
    COMFORT_2_COMPACTED("comfort_2", OsmTag("surface", "compacted")),

    /**
     * If the surface is uncomfortable for city bikes.
     */
    COMFORT_3_COBBLESTONE("comfort_3", OsmTag("surface", "cobblestone")),

    /**
     * If the surface is uncomfortable for most bikes.
     *
     * `gravel` is loose, coarse gravel and can be broken rocks with sharp edges which are terrible for bicycles.
     * `fine_gravel` is in [COMFORT_2_COMPACTED] as it was often used like `compacted` is considered good for bikes.
     * See discussion in [BIK-1098].
     */
    COMFORT_4_GRAVEL("comfort_4", OsmTag("surface", "gravel"));

    companion object {
        /**
         * The RadSim tag used to store the surface type.
         */
        const val RADSIM_TAG = "surface"

        /**
         * The specific OSM tags that are used to determine the RadSim surface type.
         *
         * This list is ordered by priority - ** do not reorder! **
         *
         * *Do not add `highway`: Alternative OSM tags like `highway` which are used to estimate the surface
         * type when no other surface tags are set. Do not add `highway` and other routing-essential tags
         * to this list or else they are deleted during back-mapping, see `RadSimTagMerger.remove()`.
         *
         * "surface" is the default key used by BRouter
         * "cycleway:surface[:l/r/b]" are not used by BRouter profiles, but is mentioned in "lookups"
         *
         * Typos in tag key do not happen very often and can be neglected.
         * See https://taginfo.openstreetmap.org/api/4/key/combinations?key=cycleway&sortorder=desc
         * (documentation https://taginfo.openstreetmap.org/taginfo/apidoc#api_4_key_combinations)
         * and https://taginfo.openstreetmap.org/api/4/key/similar?key=cycleway
         *
         * We don't check "sidewalk" and "footway" surfaces on purpose (TUD).
         */
        @Suppress("MemberVisibilityCanBePrivate") // Part of the API
        val specificOsmTags = listOf(
            "surface",
            "cycleway:surface",
            "cycleway:both:surface",
            "cycleway:right:surface",
            "cycleway:left:surface",
        )

        /**
         * The alternative OSM tag used to estimate the surface type when no other surface tags are set.
         */
        private const val ALTERNATIVE_OSM_TAG = "highway"

        /**
         * Mappings used to map from OSM attributes to surface types.
         */
        private var MAPPINGS: List<Mapping<SurfaceType>>

        init {
            val mappings: MutableList<Mapping<SurfaceType>> = ArrayList()

            /**
             * The mapping of OSM tags to RadSim surface types.
             *
             * In the dataset of the pilot municipalities (2024) the TUD found that in tag values some contributors
             * use other characters like `;`, `/`, `_`, etc. instead of `:`, (e.g. `cobblestone;flattened`).
             * This is why we use `.` in the regex expressions.
             */
            @Suppress("RedundantSuppression") // IDE bug: marks SpellCheckingInspection as redundant sometimes
            val categoryValueMap = mapOf(
                COMFORT_1_ASPHALT to listOf(
                    Regex("asphalt"),
                    Regex("asphalt.paving_stones"),
                    Regex("bricks"),
                    Regex("concrete"),
                    Regex("concrete.lanes"),
                    Regex("concrete.plates"),
                    Regex("granite.plates"),
                    Regex("paved"),
                    Regex("paving_stones"),
                    Regex("paving_stones.50"),
                    Regex("paving_stones.lanes"),
                    Regex("plates"),
                    Regex("tartan"),
                ),
                COMFORT_2_COMPACTED to listOf(
                    Regex("compacted"),
                    Regex("unpaved"),
                    Regex("fine_gravel"), // historically often used like compacted (see OSM Wiki)
                    Regex("grass_paver"),
                    Regex("dirt"),
                    Regex("dirt.sand"),
                ),
                COMFORT_3_COBBLESTONE to listOf(
                    Regex("asphalt.cobblestone"),
                    Regex("cobblestone"),
                    Regex("cobblestone.flattened"),
                    Regex("metal"),
                    Regex("metal_grid"),
                    Regex("sett"),
                    Regex("tiles"),
                    @Suppress("SpellCheckingInspection")
                    Regex("unhewn_cobblestone"),
                ),
                COMFORT_4_GRAVEL to listOf(
                    Regex("bare_rock"),
                    Regex("bushes"),
                    Regex("earth"),
                    Regex("grass"),
                    Regex("grass.ground"),
                    Regex("gravel"), // gravel can be broken rocks with sharp edges which are terrible for bicycles
                    Regex("gravel.grass"),
                    Regex("ground"),
                    Regex("ground.grass"),
                    Regex("ground.mud"),
                    Regex("ground.wood"),
                    Regex("mud"),
                    @Suppress("SpellCheckingInspection")
                    Regex("pebblestone"),
                    Regex("rock"),
                    Regex("roots"),
                    Regex("sand"),
                    Regex("sandstone"),
                    Regex("stepping_stones"),
                    Regex("stone"),
                    Regex("wood"),
                    @Suppress("SpellCheckingInspection")
                    Regex("woodchips"),
                )
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
         * Find the RadSim surface type based on the OSM tags.
         *
         * This method checks the specific OSM tags in order of priority and returns the first match. [BIK-1086]
         *
         * Typos in the OSM tags are normalized and also interpreted. [BIK-1087]
         *
         * @param tags The OSM tags to search for the surface type.
         * @return The surface type based on the provided tags.
         */
        @SuppressWarnings("ReturnCount") // TODO
        fun toRadSim(tags: Map<String, Any>): SurfaceType {
            // Iterate through mapping list first to ensure hierarchical checks
            for (mapping in MAPPINGS) {
                val osmKey = mapping.tagKey
                val tagValue = tags[osmKey]?.toString() ?: continue
                if (mapping.tagValue.matches(tagValue)) {
                    return mapping.target
                }
            }

            // If none of these tags exist or the value is not known, guess based on `highway` tag [BIK-1088]
            if (tags.containsKey(ALTERNATIVE_OSM_TAG)) {
                val highwayValue = tags[ALTERNATIVE_OSM_TAG].toString()
                if (highwayValue == "path" || highwayValue == "track") {
                    return COMFORT_2_COMPACTED
                }
            }

            // Default for everything else
            return COMFORT_1_ASPHALT
        }
    }
}
